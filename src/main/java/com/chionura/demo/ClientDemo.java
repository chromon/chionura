package com.chionura.demo;

import com.chionura.client.NIOClient;
import com.chionura.packet.Header;

import java.io.IOException;

public class ClientDemo {
    public static void main(String[] args) {

        Header header = new Header();
        header.setServiceMethod("com.chionura.demo.ServiceDemo.print");
        header.setArgs("abc", 123);

        NIOClient client;
        try {
            client = new NIOClient(9911);
            Object result = client.call(header);
            if (result != null) {
                System.out.println(result);
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
