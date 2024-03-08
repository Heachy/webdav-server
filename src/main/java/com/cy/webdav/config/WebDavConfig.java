package com.cy.webdav.config;

import com.cy.webdav.servlet.WebDavServletFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Haechi
 * @date 2024/3/8
 */
@Slf4j
public class WebDavConfig {
    public static Properties prop;
    public static String HOST;
    public static int PORT;
    public static String LOGIN_SERVICE_NAME;
    public static String REALM_NAME;
    public static String [] GLOBAL_ROLE;
    public static String [] DAV_ROLE;
    public static String PATH_SPEC;
    public static String CONTEXT_PATH;

    public static String WEBDAV_PATH;

    static {
        prop = new Properties();
        try ( InputStream input = WebDavServletFactory.class.getClassLoader().getResourceAsStream( "config.properties" ) ) {
            prop.load( input );

            HOST = prop.getProperty( "host", "localHOST" );
            PORT = Integer.parseInt( prop.getProperty( "port","8081" ) );
            LOGIN_SERVICE_NAME = prop.getProperty( "loginServiceName", "WebdavRealm" );
            REALM_NAME = prop.getProperty( "realmName", "WebdavRealm" );
            GLOBAL_ROLE = prop.getProperty( "globalRole", "user" ).split(",");
            DAV_ROLE = prop.getProperty( "davRole", "user" ).split(",");
            PATH_SPEC = prop.getProperty( "pathSpec", "/*" );
            CONTEXT_PATH = prop.getProperty( "contextPath", "/webdav" );
            WEBDAV_PATH = prop.getProperty( "webdavPath");
        } catch ( IOException ex ) {
            log.error( "Could not load properties" );
            System.exit( 1 );
        }
    }
}
