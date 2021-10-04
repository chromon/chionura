package com.chionura.service;

import org.junit.Test;

public class ServiceTest {

    @Test
    public void testMethodAvailable() {
        Service service = new Service("com.chionura.demo.ServiceDemo");
        if (service.classIsAvailable()) {
            service.newService();
        }

        System.out.println(service.isMethodAvailable("print", "abc", 123));

        System.out.println(service.call("print", "abc", 123));
    }
}
