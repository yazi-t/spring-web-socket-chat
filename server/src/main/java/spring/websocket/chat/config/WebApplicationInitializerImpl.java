package spring.websocket.chat.config;

import org.springframework.core.Conventions;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.util.Assert;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Configures WebServlet context. replaces traditional web.xml file.
 * <p>
 * implements {@link org.springframework.web.WebApplicationInitializer} interface.
 * Implementing this interface we can achieve 100% java configuration.
 * Note: Can implement {@link org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer}
 *       if does not have any additional web.xml configurations.
 *
 * @author Yasitha Thilakaratne
 */
public class WebApplicationInitializerImpl implements org.springframework.web.WebApplicationInitializer {

    private static final String SERVLET_CONTEXT_PREFIX = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.";
    public static final String DEFAULT_FILTER_NAME = "springSecurityFilterChain";
    private final Class<?>[] configurationClasses;



    public WebApplicationInitializerImpl() {
        this.configurationClasses = null;
    }

    public WebApplicationInitializerImpl(Class... configurationClasses) {
        this.configurationClasses = configurationClasses;
    }

    /**
     * Configure the given ServletContext with any servlets, filters, listeners context-params
     * and attributes necessary for initializing this web application.
     *
     * In here,
     * <ul>
     *     <li>Registers dispatcher servlet.</li>
     *     <li>Registers spring security filter chain</li>
     *     <li>Registers {@link HttpSessionEventPublisher}</li>
     * </ul>
     *
     * @param servletContext the ServletContext to initialize
     * @throws ServletException
     */
    @Override
    public final void onStartup(ServletContext servletContext) throws ServletException {
        // dispatcher servlet
        AnnotationConfigWebApplicationContext webCtx = new AnnotationConfigWebApplicationContext();
        webCtx.register(SpringConfig.class);
        webCtx.setServletContext(servletContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(webCtx));
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);
        servlet.addMapping("/");

        // spring security filter chain
        this.beforeSpringSecurityFilterChain(servletContext);
        if(this.configurationClasses != null) {
            AnnotationConfigWebApplicationContext rootAppContext = new AnnotationConfigWebApplicationContext();
            rootAppContext.register(this.configurationClasses);
            servletContext.addListener(new ContextLoaderListener(rootAppContext));
        }
        if(this.enableHttpSessionEventPublisher()) {
            servletContext.addListener("org.springframework.security.web.session.HttpSessionEventPublisher");
        }
        servletContext.setSessionTrackingModes(this.getSessionTrackingModes());
        this.insertSpringSecurityFilterChain(servletContext);
        this.afterSpringSecurityFilterChain(servletContext);

        // HttpSessionEventPublisher listener
        servletContext.addListener(new HttpSessionEventPublisher());
    }

    protected boolean enableHttpSessionEventPublisher() {
        return false;
    }

    private void insertSpringSecurityFilterChain(ServletContext servletContext) {
        String filterName = "springSecurityFilterChain";
        DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy(filterName);
        String contextAttribute = this.getWebApplicationContextAttribute();
        if(contextAttribute != null) {
            springSecurityFilterChain.setContextAttribute(contextAttribute);
        }

        this.registerFilter(servletContext, true, filterName, springSecurityFilterChain);
    }

    protected final void insertFilters(ServletContext servletContext, Filter... filters) {
        this.registerFilters(servletContext, true, filters);
    }

    protected final void appendFilters(ServletContext servletContext, Filter... filters) {
        this.registerFilters(servletContext, false, filters);
    }

    private void registerFilters(ServletContext servletContext, boolean insertBeforeOtherFilters, Filter... filters) {
        Assert.notEmpty(filters, "filters cannot be null or empty");
        Filter[] var4 = filters;
        int var5 = filters.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Filter filter = var4[var6];
            if(filter == null) {
                throw new IllegalArgumentException("filters cannot contain null values. Got " + Arrays.asList(filters));
            }

            String filterName = Conventions.getVariableName(filter);
            this.registerFilter(servletContext, insertBeforeOtherFilters, filterName, filter);
        }

    }

    private final void registerFilter(ServletContext servletContext, boolean insertBeforeOtherFilters, String filterName, Filter filter) {
        FilterRegistration.Dynamic registration = servletContext.addFilter(filterName, filter);
        if(registration == null) {
            throw new IllegalStateException("Duplicate Filter registration for \'" + filterName + "\'. Check to ensure the Filter is only configured once.");
        } else {
            registration.setAsyncSupported(this.isAsyncSecuritySupported());
            EnumSet dispatcherTypes = this.getSecurityDispatcherTypes();
            registration.addMappingForUrlPatterns(dispatcherTypes, !insertBeforeOtherFilters, new String[]{"/*"});
        }
    }

    private String getWebApplicationContextAttribute() {
        String dispatcherServletName = this.getDispatcherWebApplicationContextSuffix();
        return dispatcherServletName == null?null:"org.springframework.web.servlet.FrameworkServlet.CONTEXT." + dispatcherServletName;
    }

    protected Set<SessionTrackingMode> getSessionTrackingModes() {
        return EnumSet.of(SessionTrackingMode.COOKIE);
    }

    protected String getDispatcherWebApplicationContextSuffix() {
        return null;
    }

    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
    }

    protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
    }

    protected EnumSet<DispatcherType> getSecurityDispatcherTypes() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC);
    }

    protected boolean isAsyncSecuritySupported() {
        return true;
    }
}
