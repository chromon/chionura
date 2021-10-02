package com.chionura.demo;

import com.chionura.client.NIOClient;
import com.chionura.packet.Header;

import java.io.IOException;

public class ClientDemo {
    public static void main(String[] args) {

        Header header = new Header();
        header.setServiceMethod("Foo.Test");
        header.setArgs("test", 1234, true);

        NIOClient client;
        try {
            client = new NIOClient(9911);
            client.call(header);
            client.call(header);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
