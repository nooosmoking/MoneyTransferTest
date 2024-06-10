package com.example.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "log.txt";
    private static Logger instance;
    private BufferedWriter writer;
    private DateTimeFormatter formatter;

    public Logger() {
        try {
            writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        } catch (IOException e) {
            System.err.println("Error while creating logger");
        }
    }

    public synchronized static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void logOperation(String userA, String userB, double amount) {
        String message = "User \"" + userA + "\" was sent " + amount + "$ to the user \"" + userB + "\"";
        logMessage(message);
    }

    public void logUserBalance(String user, double balance) {
        String message = "User \"" + user + "\" balance is " + balance + "$";
        logMessage(message);
    }

    public void logSignIn(String user) {
        String message = "User \"" + user + "\" was signed in";
        logMessage(message);
    }

    public void logSignUp(String user) {
        String message = "User \"" + user + "\" was signed up";
        logMessage(message);
    }

    private synchronized void logMessage(String message) {
        String formattedDateTime = LocalDateTime.now().format(formatter);
        try {
            writer.write(formattedDateTime + ": " + message + "\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error while sending logs");
        }
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Error while closing logger");
            }
        }
    }
}
