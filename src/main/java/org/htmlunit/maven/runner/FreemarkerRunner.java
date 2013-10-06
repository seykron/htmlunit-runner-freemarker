package org.htmlunit.maven.runner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlunit.maven.AbstractRunner;
import org.htmlunit.maven.ResourceUtils;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/** Runner to load tests from Freemarker templates in the file system.
 */
public class FreemarkerRunner extends AbstractRunner {

  /** Considers the test file a Freemarker template. It processes the file with
   * Freemarker engine and then it writes results to the template.
   * <p>{@inheritDoc}</p>
   */
  @Override
  protected void loadTest(final StringTemplate runnerTemplate,
      final URL test) {
    TemplateLoader templateLoader = new URLTemplateLoader() {
      @Override
      protected URL getURL(final String requiredTemplateName) {
        try {
          String templatePath;
          templatePath = StringUtils.substringBeforeLast(test.toString(), "/");
          URL url = new URL(templatePath + "/" + requiredTemplateName);
          if (isValidUrl(url)) {
            return url;
          } else {
            return null;
          }
        } catch (MalformedURLException cause) {
          throw new RuntimeException("Cannot generate template URL", cause);
        }
      }
    };
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(out);

    try {
      String name = StringUtils.substringAfterLast(test.toString(), "/");

      Configuration config = new Configuration();
      Object dataModel = initializeConfiguration(config);
      TemplateLoader currentTemplateLoader = config.getTemplateLoader();

      if (currentTemplateLoader != null) {
        config.setTemplateLoader(new MultiTemplateLoader(
            new TemplateLoader[] { templateLoader, currentTemplateLoader }));
      } else {
        config.setTemplateLoader(templateLoader);
      }

      Template template = config.getTemplate(name);
      template.process(dataModel, writer);

      runnerTemplate.setAttribute("testFiles", out.toString());
    } catch (IOException cause) {
      throw new RuntimeException("Cannot open freemarker template", cause);
    } catch (TemplateException cause) {
      throw new RuntimeException("Cannot process freemarker template", cause);
    } finally {
      IOUtils.closeQuietly(out);
      IOUtils.closeQuietly(writer);
    }
  }

  /** Initializes Freemarker configuration and returns the data model available
   * during template processing.
   *
   * @return Returns an object provided to the template, never returns null.
   */
  protected Object initializeConfiguration(final Configuration config) {
    return new HashMap<String, Object>();
  }

  /** Determines whether the specific URL is valid and it can be reached.
   *
   * @param url Url to verify. Cannot be null.
   * @return Returns <code>true</code> if the URL is valid, false otherwise.
   */
  private boolean isValidUrl(final URL url) {
    try {
      // This is ugly, but there's no way to check whether the url is actually
      // reachable without fetching it.
      ResourceUtils.readAsText(url);
      return true;
    } catch (Exception cause) {
      return false;
    }
  }
}
