package com.chionura.demo;

import com.chionura.server.NIOServer;
import com.chionura.service.Service;
import com.chionura.service.ServiceRegister;

import java.io.IOException;

public class ServerDemo {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Service service = new Service("com.chionura.demo.ServiceDemo");
        if (service.classIsAvailable()) {
            service.newService();
        }
        System.out.println(service.getMethod("print").getName());

        ServiceRegister.registerService(service);

        int port = 9911;
        NIOServer server = new NIOServer(port);
        server.listen();
    }
}
