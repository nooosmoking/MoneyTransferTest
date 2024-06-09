package com.example.app;

import com.example.server.Server;
import com.example.config.MoneyTransferApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(MoneyTransferApplicationConfig.class);
        Server server = context.getBean(Server.class);
        String[] address = getAddress(args);
        server.run(address[0], Integer.parseInt(address[1]));
    }

    private static String[] getAddress(String[] args) {
        if (args.length != 2 || !args[0].startsWith("--url=") ||!args[1].startsWith("--port=")) {
            System.err.println("Please write url and port in arguments.");
            System.exit(-1);
        }
        return new String[]{args[0].split("=")[1], args[1].split("=")[1]};
    }
}
