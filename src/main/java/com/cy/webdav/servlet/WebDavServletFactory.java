
package com.cy.webdav.servlet;

import com.cy.webdav.config.WebDavConfig;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Objects;
import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.cryptomator.webdav.core.filters.*;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;

/**
 * @author Haechi
 */
@Slf4j
public class WebDavServletFactory {
	private WebDavServletFactory(){}

	private static final String WILDCARD = "/*";

	public static ServletContextHandler createServletContext(Path rootPath, String contextPath) {
		final Servlet servlet = new FixedPathNioWebDavServlet(rootPath);
		final ServletContextHandler servletContext = new ServletContextHandler(null, contextPath, ServletContextHandler.SESSIONS);
		final ServletHolder servletHolder = new ServletHolder(contextPath, servlet);
		servletContext.addServlet(servletHolder, WILDCARD);
		servletContext.addFilter(LoggingFilter.class, WILDCARD, EnumSet.of(DispatcherType.REQUEST));
		servletContext.addFilter(UnicodeResourcePathNormalizationFilter.class, WILDCARD, EnumSet.of(DispatcherType.REQUEST));
		servletContext.addFilter(PostRequestBlockingFilter.class, WILDCARD, EnumSet.of(DispatcherType.REQUEST));
		servletContext.addFilter(MkcolComplianceFilter.class, WILDCARD, EnumSet.of(DispatcherType.REQUEST));
		servletContext.addFilter(AcceptRangeFilter.class, WILDCARD, EnumSet.of(DispatcherType.REQUEST));
		servletContext.addFilter(MacChunkedPutCompatibilityFilter.class, WILDCARD, EnumSet.of(DispatcherType.REQUEST));
		return servletContext;
	}

	public static WebDavServletController createServletController(Path rootPath, String untrimmedContextPath,
			ServerConnector serverConnector, ContextHandlerCollection contextHandlerCollection,
			ContextPathRegistry contextPathRegistry) {
		var trimmedCtxPath = untrimmedContextPath;
		while (trimmedCtxPath.endsWith("/")) {
			trimmedCtxPath = trimmedCtxPath.substring(0, trimmedCtxPath.length() - 1);
		}
		String contextPath = trimmedCtxPath.startsWith("/") ? trimmedCtxPath : "/" + trimmedCtxPath;
		ServletContextHandler contextHandler = createServletContext(rootPath, contextPath);

		return new WebDavServletController(contextHandler, contextHandlerCollection, serverConnector, contextPathRegistry, contextPath ,createSecurityHandler());
	}

	public static ConstraintSecurityHandler createSecurityHandler() {
		HashLoginService loginService = new HashLoginService();

		loginService.setName( WebDavConfig.LOGIN_SERVICE_NAME );

		loginService.setConfig( Objects.requireNonNull( WebDavServletFactory.class.getResource( "/realm.properties" ) ).toString());

		Constraint constraint = new Constraint(Constraint.__BASIC_AUTH, "");

		constraint.setRoles( WebDavConfig.DAV_ROLE );

		constraint.setAuthenticate(true);

		ConstraintMapping constraintMapping = new ConstraintMapping();

		constraintMapping.setConstraint(constraint);

		constraintMapping.setPathSpec(WebDavConfig.PATH_SPEC);

		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();

		securityHandler.setAuthenticator(new BasicAuthenticator());
		securityHandler.setRealmName(WebDavConfig.REALM_NAME);
		securityHandler.setLoginService(loginService);

		String[] user = WebDavConfig.GLOBAL_ROLE;

		for ( String s : user ) {
			securityHandler.addRole(s);
		}

		securityHandler.addConstraintMapping(constraintMapping);

		return securityHandler;
	}

}
