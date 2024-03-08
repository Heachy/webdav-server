package com.cy.webdav;

import com.cy.webdav.config.WebDavConfig;
import com.cy.webdav.exception.ServerLifecycleException;
import com.cy.webdav.server.WebDavServerHandle;
import com.cy.webdav.server.WebDavServerManager;
import com.cy.webdav.servlet.WebDavServletController;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Haechi
 * @date 2024/3/8
 */
@AllArgsConstructor
@Slf4j
public class WebDav {
    private WebDavServletController servlet;

    private WebDavServerHandle serverHandle;

    public void stop() {
        servlet.stop();
        serverHandle.server().stop();
    }

    public static WebDav start()throws Exception{

        Path pathToMirror = Paths.get( WebDavConfig.WEBDAV_PATH );
        if (!Files.isDirectory(pathToMirror)) {
            log.error("Invalid directory.");
            System.exit(1);
        }
        int port = WebDavConfig.PORT;
        WebDavServerHandle serverHandle;
        try {
            serverHandle = WebDavServerManager.getOrCreateServer(port);
        } catch ( ServerLifecycleException e) {
            throw new Exception("Failed to start server", e);
        }

        WebDavServletController servlet;
        try {
            servlet = serverHandle.server().createWebDavServlet(pathToMirror, WebDavConfig.CONTEXT_PATH);
            servlet.start();
        } catch (RuntimeException e) {
            throw new Exception("Failed to start server", e);
        }

        var uri = servlet.getServletRootUri();
        log.info("Mounting {}...", uri);
        return new WebDav( servlet,serverHandle );
    }
}
