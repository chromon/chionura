package com.chionura.demo;

import com.chionura.registry.Registry;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class RegistryDemo {
    public static void main(String[] args) throws IOException {

        Logger log = Logger.getLogger("Registry");

        InetSocketAddress addr = new InetSocketAddress("localhost", 8001);

        HttpServer server = HttpServer.create(addr,0);
        server.createContext("/_rpc_/registry", new Registry(1000 * 50 * 5));
        log.info("Registry is running at: " + addr.getHostName() + ":" +addr.getPort());
        server.start();
    }
}
