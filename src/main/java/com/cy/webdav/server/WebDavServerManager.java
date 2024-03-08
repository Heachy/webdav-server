package com.cy.webdav.server;

import com.cy.webdav.exception.ServerLifecycleException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebDavServerManager {

	private static final ConcurrentMap<Integer, ReferenceCountingHandle> RUNNING_SERVERS = new ConcurrentHashMap<>();
	private WebDavServerManager() {
	}

	public static WebDavServerHandle getOrCreateServer(int port) throws ServerLifecycleException {
		return RUNNING_SERVERS.compute(port, (p, handle) -> {
			if (handle == null || handle.counter.getAndIncrement() == 0) {
				// if counter was 0 -> a concurrent thread is about to terminate it.
				var server = tryCreate(p);
				return new ReferenceCountingHandle(port, server, new AtomicInteger(1));
			} else {
				// handle exists. we increased the counter already.
				return handle;
			}
		});
	}

	private static WebDavServer tryCreate(int port) throws ServerLifecycleException {
		var bindAddr = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
		var server = WebDavServer.create(bindAddr);
		server.start();
		return server;
	}

	private record ReferenceCountingHandle(int port, WebDavServer server, AtomicInteger counter) implements WebDavServerHandle {

		@Override
		public void close() throws IOException {
			if (counter.decrementAndGet() == 0) {
				RUNNING_SERVERS.remove(port, this);
				try {
					server.terminate();
				} catch (ServerLifecycleException e) {
					throw new IOException(e);
				}
			}
		}

	}

}
