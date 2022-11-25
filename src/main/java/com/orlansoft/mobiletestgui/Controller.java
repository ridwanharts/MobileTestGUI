package com.orlansoft.mobiletestgui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

public class Controller implements Initializable {

    @FXML
    private Button btnStartTest;
    @FXML
    private Button btnStartServer;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnSetting;
    @FXML
    private Button btnGenerate;
    @FXML
    private TextArea tvLog;
    @FXML
    private Button btnEditor;
    
    private static Scene scene;
    private int loops = 0;
    private boolean stop = false;
    String run = "";
    AppiumServer appiumServer;
    
    @FXML
    private void startTest() throws IOException {
        try {
            
            String command = "java -jar "+ System.getProperty("user.dir") +"/test/MobileTestAutomation.jar";
            System.out.print(command + "\n");
            Process proc = Runtime.getRuntime().exec(command);
            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            int count = 0;
            while ((line = reader.readLine()) != null) {
                System.out.print(line + "\n");
                //if (count == 0) {
                    tvLog.setText(tvLog.getText().concat("\n" + "" + line));
                //}
                count++;
            }
            proc.waitFor();
        } catch (InterruptedException ex) {
            System.out.print(ex.getMessage() + "\n");
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void startServer() {
        appiumServer.start();
        tvLog.setText("Server Run.");
    }

    @FXML
    protected void stopTest() {
        if(!appiumServer.isRunning()){
            //forceStop();
        }else{
            appiumServer.stop();
            tvLog.setText(tvLog.getText().concat("\n" + "Server Stopped."));
        }
        
    }
    
    @FXML
    protected void openSetting(){
        
        File f = new File("SETUP.txt");
        DialogSetting.display(f);
    }

    @FXML
    protected void generateTestProject() throws IOException {
        
        String destination = "test/";
        String password = "password";
        File f;

        try {
            f = new File("test/ProjectTest.zip");
            FileUtils.copyInputStreamToFile(App.class.getResourceAsStream("/ProjectTest.zip"),f);
            
            ZipFile zipFile = new ZipFile(f);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        
        f = new File("test/SETUP.txt");
        File fd = new File("SETUP.txt");
        FileUtils.copyFile(f, fd);
        f.delete();
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //if(appiumServer == null){
            appiumServer = new AppiumServer();
        //}
    }
    

    public void forceStop() {
        try {
            String command = "pkill -9 -f appium";
            Process proc = Runtime.getRuntime().exec(command);
            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    protected void openEditor(){
        try {
            //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Demo.fxml"));
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/EditorFile.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Editor File");
            stage.setScene(new Scene(root1));
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
}
