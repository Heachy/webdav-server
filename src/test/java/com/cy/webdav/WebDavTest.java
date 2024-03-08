package com.cy.webdav;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Haechi
 * @date 2024/3/8
 */
public class WebDavTest {
    private static final Logger LOG = LoggerFactory.getLogger(WebDavTest.class);

    public static void main(String[] args) throws Exception {

        WebDav webDav;
        try {
            webDav = WebDav.start();
        } catch ( RuntimeException e ) {
            throw new Exception( "Failed to start server", e );
        }

        LOG.info( "Enter anything to unmount..." );

        new Scanner( System.in ).nextLine();

        webDav.stop();

    }
}
