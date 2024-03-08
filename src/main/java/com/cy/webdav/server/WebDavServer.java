package com.cy.webdav.server;

import com.cy.webdav.exception.ServerLifecycleException;
import com.cy.webdav.servlet.WebDavServletController;
import com.cy.webdav.servlet.WebDavServletFactory;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import com.cy.webdav.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WebDAV server, that WebDAV servlets can be added to using {@link #createWebDavServlet(Path, String)}.
 * <p>
 * An instance of this class can be obtained via {@link #create(InetSocketAddress)}.
 * @author Haechi
 */
public class WebDavServer {

	private static final Logger LOG = LoggerFactory.getLogger(WebDavServer.class);

	private final Server server;
	private final ExecutorService executorService;
	private final ServerConnector localConnector;
	private final ContextHandlerCollection servletCollectionCtx;
	private final DefaultServlet defaultServlet;

	WebDavServer(Server server, ExecutorService executorService, ServerConnector connector, ContextHandlerCollection servletCollectionCtx, DefaultServlet defaultServlet) {
		this.server = server;
		this.executorService = executorService;
		this.localConnector = connector;
		this.servletCollectionCtx = servletCollectionCtx;
		this.defaultServlet = defaultServlet;
	}

	public static WebDavServer create(InetSocketAddress bindAddr) {
		return WebDavServerFactory.createWebDavServer(bindAddr);
	}

	/**
	 * Starts the WebDAV server.
	 *
	 * @throws ServerLifecycleException If any exception occurs during server start (e.g. port not available).
	 */
	public synchronized void start() throws ServerLifecycleException {
		if (executorService.isShutdown()) {
			throw new IllegalStateException("Server has already been terminated.");
		}
		try {
			server.start();
			LOG.info("WebDavServer started.");
		} catch (Exception e) {
			throw new ServerLifecycleException("Server couldn't be started", e);
		}
	}

	/**
	 * Stops the WebDAV server.
	 *
	 * @throws ServerLifecycleException If the server could not be stopped for any unexpected reason.
	 */
	public synchronized void stop() throws ServerLifecycleException {
		try {
			server.stop();
			LOG.info("WebDavServer stopped.");
		} catch (Exception e) {
			throw new ServerLifecycleException("Server couldn't be stopped", e);
		}
	}

	/**
	 * Stops the WebDAV server and shuts down its executor service. After terminating, this instance can no longer be restarted.
	 * 
	 * @throws ServerLifecycleException If the server could not be stopped for any unexpected reason.
	 */
	public synchronized void terminate() throws ServerLifecycleException {
		stop();
		executorService.shutdownNow();
	}

	/**
	 * Creates a new WebDAV servlet (without starting it yet).
	 *
	 * @param rootPath The path to the directory which should be served as root resource.
	 * @param contextPath The servlet context path, i.e. the path of the root resource.
	 * @return The controller object for this new servlet
	 */
	public WebDavServletController createWebDavServlet(Path rootPath, String contextPath) {
		return WebDavServletFactory.createServletController(rootPath, contextPath, localConnector, servletCollectionCtx, defaultServlet);
	}

}
