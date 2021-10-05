package com.chionura.demo;

import com.chionura.server.NIOServer;
import com.chionura.service.Service;
import com.chionura.service.ServiceRegister;

import java.io.IOException;

public class ServerDemo2 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Service service = new Service("com.chionura.demo.ServiceDemo");
        if (service.classIsAvailable()) {
            service.newService();
        }

        ServiceRegister.registerService(service);

        NIOServer server = new NIOServer("localhost", 9912);
        server.listen();
    }
}
