/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orlansoft.mobiletestgui;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import java.io.File;

/**
 *
 * @author ridwan
 */
public class AppiumServer {
    private final AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
    private AppiumDriverLocalService server;
    private int port;
    private final String appiumLogsLoc = "appium-logs";
    private final String logFileName = "logs";

    public AppiumServer() {
        this.port = getAvailablePort();
        this.serviceBuilder.usingPort(port);
        
        File directory = new File("test/");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = new File("test/Log.txt");
        try {
            serviceBuilder.withLogFile(file);
            //this.server.addOutPutStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.server = AppiumDriverLocalService.buildService(serviceBuilder);
    }

    public void stop() {
        this.server.stop();
    }
    
    public void start(){
        this.server.start();
    }
    
    public boolean isRunning(){
        return this.server.isRunning();
    }

    public AppiumDriverLocalService get() {
        return this.server;
    }

    public void redirectLog() {
        this.server.clearOutPutStreams();

    }

    public int getAvailablePort() {
        int port = 4723;
        

//        try {
//            ServerSocket serverSocket = new ServerSocket(0);
//            port = serverSocket.getLocalPort();
//            serverSocket.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return port;
    }
}
