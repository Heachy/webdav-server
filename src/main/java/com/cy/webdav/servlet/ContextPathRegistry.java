package com.cy.webdav.servlet;

/**
 * @author Haechi
 */
public interface ContextPathRegistry {

	boolean add(String contextPath);
	boolean remove(String contextPath);

}
