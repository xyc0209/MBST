package com.badsmell.base;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class MyLogger{

    private BufferedWriter writer;

    public MyLogger(String filePath) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(filePath, true));
    }

    public void log(String message) throws IOException {
        String logMessage = new Date() + " - " + message;
        writer.write(logMessage);
        writer.newLine();
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();
    }
}