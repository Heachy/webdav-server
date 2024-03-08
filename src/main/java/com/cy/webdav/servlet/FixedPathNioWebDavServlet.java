package com.cy.webdav.servlet;

import java.nio.file.Path;
import org.cryptomator.webdav.core.servlet.AbstractNioWebDavServlet;

class FixedPathNioWebDavServlet extends AbstractNioWebDavServlet {

	private final Path rootPath;

	public FixedPathNioWebDavServlet(Path rootPath) {
		this.rootPath = rootPath;
	}

	@Override
	protected Path resolveUrl(String relativeUrl) throws IllegalArgumentException {
		return rootPath.resolve(relativeUrl);
	}

}

