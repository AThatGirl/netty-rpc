package com.cj.rpc.provider;

import com.cj.rpc.provider.server.NettyRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerBootstrapApplication implements CommandLineRunner {

    //将nettyRpcServer注入
    @Autowired
    NettyRpcServer nettyRpcServer;
    public static void main(String[] args) {
        SpringApplication.run(ServerBootstrapApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(() -> nettyRpcServer.start("127.0.0.1", 8899)).start();
    }
}
