package org.htmlunit.maven.runner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.net.URL;
import java.util.Properties;

import org.htmlunit.maven.RunnerContext;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/** Tests the {@link FreemarkerRunner} class.
 */
public class FreemarkerRunnerTest {
  private RunnerContext context;
  private FreemarkerRunner runner;
  private boolean verified;

  @Before
  public void setUp() {
    context = new RunnerContext();
    Properties runnerConfig = new Properties();
    runnerConfig.put("outputDirectory",
        System.getProperty("java.io.tmpdir"));
    runnerConfig.put("testFiles",
        "classpath:org/htmlunit/maven/*Test.ftl");

    context.setTimeout(10);
    context.getWebClientConfiguration().setProperty("javaScriptEnabled",
        String.valueOf(true));
    context.setRunnerConfiguration(runnerConfig);
  }

  @Test
  public void run() {
    runner = new FreemarkerRunner() {
      @Override
      protected void testFinished(final URL test, final HtmlPage page) {
        DomNodeList<DomElement> items = page.getElementsByTagName("li");

        assertThat(items.size(), is(3));
        assertThat(items.get(0).asText(), is("John"));
        assertThat(items.get(1).asText(), is("Joe"));
        assertThat(items.get(2).asText(), is("Moe"));
        verified = true;
      }
    };
    runner.initialize(context);
    runner.run();
    assertThat(verified, is(true));
  }
}
