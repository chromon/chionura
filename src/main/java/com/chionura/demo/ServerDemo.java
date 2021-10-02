package com.chionura.demo;

import com.chionura.server.NIOServer;

import java.io.IOException;

public class ServerDemo {
    public static void main(String[] args) throws IOException {
        int port = 9911;
        NIOServer server = new NIOServer(port);
        server.listen();
    }
}
