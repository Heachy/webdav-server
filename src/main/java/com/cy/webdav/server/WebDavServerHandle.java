package com.cy.webdav.server;

import java.io.IOException;

/**
 * @author Hacehi
 */
public interface WebDavServerHandle extends AutoCloseable {

	WebDavServer server();

	@Override
	void close() throws IOException;
}
