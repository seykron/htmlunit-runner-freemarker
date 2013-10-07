package org.htmlunit.maven.runner;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.WebUtils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;

/** Runner to process Freemarker templates in a valid Spring MVC web context.
 *
 * It allows to create a custom {@link GenericWebApplicationContext} via
 * {@link #buildApplicationContext()}.
 */
public class FreemarkerWebContextRunner extends FreemarkerRunner {

  /** Maps spring macros path into the view configuration and initializes the
   * web context.
   *
   * {@inheritDoc}
   */
  @Override
  protected Object initializeConfiguration(final Configuration config) {
    Map<String, Object> model = new HashMap<String, Object>();

    try {
      HttpServletRequest mockServletRequest = buildMockServletRequest();
      config.setSharedVariable("springMacroRequestContext",
          new RequestContext(mockServletRequest, model));
      config.setSharedVariable("request", mockServletRequest);

      config.setTemplateLoader(new ClassTemplateLoader(getClass(),
          "/org/springframework/web/servlet/view/freemarker"));
    } catch (TemplateModelException cause) {
      throw new RuntimeException("Cannot configure freemarker.", cause);
    }

    return model;
  }

  /** Creates the test application context. Can be overridden to configure a
   * different test application context.
   * @return Returns the test application context; never returns null.
   */
  protected GenericWebApplicationContext buildApplicationContext() {
    return new GenericWebApplicationContext();
  }

  /**
   * Creates a mocked HttpServletRequest.
   *
   * @param locale Request Locale, it cannot be null.
   *
   * @return the mocked HttpServletRequest. It never returns null.
   */
  private HttpServletRequest buildMockServletRequest() {
    MockServletContext servletContext = new MockServletContext();
    servletContext.addInitParameter(WebUtils.HTML_ESCAPE_CONTEXT_PARAM,
        WebUtils.HTML_ESCAPE_CONTEXT_PARAM);
    servletContext.setContextPath("testcontext/");

    // Creates a Web Application Context.
    GenericWebApplicationContext context = buildApplicationContext();
    context.setServletContext(servletContext);
    context.refresh();

    // Creates a LocaleResolver.
    LocaleResolver localeResolver = new FixedLocaleResolver();

    // Mocks the HttpServletRequest.
    HttpServletRequest servletRequest;
    servletRequest = new MockHttpServletRequest(servletContext);
    servletRequest.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE,
        localeResolver);
    // For spring 2.
    servletRequest.setAttribute(DispatcherServlet
        .WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
    // For spring 3.
    servletRequest.setAttribute(RequestContext.class.getName() + ".CONTEXT",
        context);
    return servletRequest;
  }
}
