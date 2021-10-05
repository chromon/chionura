package com.chionura.demo;

import com.chionura.client.LBClient;
import com.chionura.client.NIOClient;
import com.chionura.common.Constants;
import com.chionura.discovery.MultiServersDiscovery;
import com.chionura.packet.Header;

import java.io.IOException;

public class ClientDemo {
    public static void main(String[] args) {

        Header header = new Header();
        header.setServiceMethod("com.chionura.demo.ServiceDemo.print");
        header.setArgs("abc", 123);

//        // 普通客户端
//        NIOClient client;
//        try {
//            client = new NIOClient("localhost", 9911);
//            Object result = client.call(header);
//            if (result != null) {
//                System.out.println(result);
//            }
//            client.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 支持负载均衡客户端
        MultiServersDiscovery d = new MultiServersDiscovery(
                new String[]{"localhost:9911", "localhost:9912"});
        LBClient client = new LBClient(d, Constants.RANDOMSELECT);
        try {
            String result = (String) client.call(header);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
