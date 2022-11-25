/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orlansoft.mobiletestgui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author ridwan
 */
public class DialogSetting {

    static String deviceName, androidVersion, listFileTest[];
    static boolean drivermate, salesmate, numbering, isNoReset; 

    public static String display(File file) {
        loadFileSetup(file);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        Font fontAll = Font.font( 12);
        Font fontRadioBtn = Font.font( 11);
        
        TextField tfDeviceName = new TextField();
        tfDeviceName.setPrefWidth(200);
        TextField tfAndroidVersion = new TextField();
        tfAndroidVersion.setPrefWidth(200);

        Label lblDeviceName = new Label("Device Name:");
        lblDeviceName.setFont(fontAll);
        lblDeviceName.setPrefWidth(150);
        Label lblAndroidVersion = new Label("Android Version:");
        lblAndroidVersion.setFont(fontAll);
        lblAndroidVersion.setPrefWidth(150);
        Button btnSave = new Button("Save");
        btnSave.setPrefWidth(150);
        Label lblListProject = new Label("List File Test:");
        lblListProject.setPrefWidth(150);
        lblListProject.setFont(fontAll);
        TextArea taListProject = new TextArea();
        taListProject.setPromptText("Pisahkan dengan coma misalkan: flow_putaway.txt,flow_test.txt");
        taListProject.setPrefWidth(400);
        taListProject.setPrefHeight(200);
        taListProject.setWrapText(true);
        Label lblNoReset = new Label("No Reset:");
        lblListProject.setPrefWidth(150);
        lblListProject.setFont(fontAll);
        //Reset app state before this session.
        
        RadioButton radioButton1 = new RadioButton("Drivermate");
        radioButton1.setFont(fontRadioBtn);
        RadioButton radioButton2 = new RadioButton("Salesmate");
        radioButton2.setFont(fontRadioBtn);
        RadioButton radioButton3 = new RadioButton("Numbering");
        radioButton3.setFont(fontRadioBtn);

        ToggleGroup toggleGroup = new ToggleGroup();

        radioButton1.setToggleGroup(toggleGroup);
        radioButton2.setToggleGroup(toggleGroup);
        radioButton3.setToggleGroup(toggleGroup);
        
        tfDeviceName.setText(deviceName);
        tfAndroidVersion.setText(androidVersion);

        if(drivermate){
            toggleGroup.selectToggle(radioButton1);
        }else if(salesmate){
            toggleGroup.selectToggle(radioButton2);
        }else if(numbering){
            toggleGroup.selectToggle(radioButton3);
        }
        
        // listen to changes in selected toggle
        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {

                if (new_toggle.toString().contains("Drivermate") ) {
                    drivermate = true;
                    salesmate = false;
                    numbering = false;
                }else if (new_toggle.toString().contains("Salesmate") ) {
                    drivermate = false;
                    salesmate = true;
                    numbering = false;
                }else if (new_toggle.toString().contains("Numbering") ) {
                    drivermate = false;
                    salesmate = false;
                    numbering = true;
                }
            }
        });
        StringBuilder a = new StringBuilder();
        for (int i = 0; i < listFileTest.length; i++) {
            if (i > 0 && i != listFileTest.length) {
                a.append("\n");
            }
            a.append(listFileTest[i]);

            taListProject.setText(a.toString());
        }
            
        GridPane layout = new GridPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 52, 5, 5));
        hbox.setSpacing(10);
        hbox.getChildren().addAll(radioButton1, radioButton2,radioButton3);
        
        CheckBox cbNoReset = new CheckBox();
        cbNoReset.setSelected(isNoReset);
        cbNoReset.setText("Don't reset app state before this session.");

        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setVgap(5);
        layout.setHgap(5);

        layout.add(lblDeviceName, 0, 1);layout.add(tfDeviceName, 1, 1);
        layout.add(lblAndroidVersion, 0, 2);layout.add(tfAndroidVersion, 1, 2);
        layout.add(lblListProject, 0, 3);layout.add(taListProject, 1, 3);
        layout.add(hbox, 1, 4);
        layout.add(lblNoReset, 0, 5);layout.add(cbNoReset, 1, 5);
        layout.add(btnSave, 1, 6);
        //layout.add(label1, 1, 0);
        
        cbNoReset.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // TODO Auto-generated method stub
                isNoReset = newValue;
                System.out.println("cek " + oldValue + " - " + newValue);
                String repCbReset = "Set(\"noReset\",\"" + newValue + "\")";
                replaceLines(file, 11, repCbReset);
            }
        });
        
        btnSave.setOnAction(e -> {
            //deviceName = text1.getText();
            String rep = "Set(\"deviceName\",\"" + tfDeviceName.getText() + "\")";
            replaceLines(file, 5, rep);

            //Set("platformVersion","10")
            rep = "Set(\"platformVersion\",\"" + tfAndroidVersion.getText() + "\")";
            replaceLines(file, 7, rep);
                        
            //Set("projectTest","Drivermate")
            String repAppPackage="", repAppActivity="", repFileTest, repType="";
            if (drivermate) {
                repType="Set(\"type\",\"native\")";
                rep = "Set(\"projectTest\",\"Drivermate\")";
                repAppPackage = "Set(\"appPackage\",\"com.ods.loadsheet\")";
                repAppActivity = "Set(\"appActivity\",\"com.ods.loadsheet.activity.ActivityMain\")";
            } else if (salesmate) {
                repType="Set(\"type\",\"hybrid\")";
                rep = "Set(\"projectTest\",\"Salesmate\")";
                repAppPackage = "Set(\"appPackage\",\"com.ionic.odsscm\")";
                repAppActivity = "Set(\"appActivity\",\"com.ionic.odsscm.MainActivity\")";
            } else if (numbering) {
                repType="Set(\"type\",\"native\")";
                rep = "Set(\"projectTest\",\"Numbering\")";
                repAppPackage = "Set(\"appPackage\",\"com.orlansoft.numbering\")";
                repAppActivity = "Set(\"appActivity\",\"com.orlansoft.numbering.view.main.MainActivity\")";
            }
            replaceLines(file, 2, repType);
            replaceLines(file, 3, rep);
            replaceLines(file, 9, repAppPackage);
            replaceLines(file, 10, repAppActivity);
            
            //Set("fileTest","flow_putaway.txt|flow_test.txt")
            repFileTest = "Set(\"fileTest\",\""+ taListProject.getText().replaceAll("\\r|\\n","|") +"\")";
            replaceLines(file, 4, repFileTest);

            stage.close();
        });
        
        Scene scene = new Scene(layout, 480, 350);
        stage.setTitle("Setting Test");
        stage.setScene(scene);
        stage.showAndWait();
        
        return deviceName + "#" + androidVersion;
    }
    
    private static void loadFileSetup(File f){
        try {
            FileReader fr = new FileReader(f);
            BufferedReader reader = new BufferedReader(fr);
            String text;
            while ((text = reader.readLine()) != null) {
                if (text.contains("Set(")){
                    setCapabilities(text);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private static void setCapabilities(String text) {
        String sSubstring = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
        String[] sSplit = sSubstring.split(",");
        sSplit[1] = sSplit[1].replace("\"", "");
        if (text.startsWith("Set(\"type\"")){
            //type = sSplit[1];
        }else if (text.startsWith("Set(\"fileTest\"")) {
            listFileTest = sSplit[1].split("\\|");
        }else if (text.startsWith("Set(\"projectTest\"")){
            if(sSplit[1].equalsIgnoreCase("Drivermate")){
                drivermate = true;
            }else if(sSplit[1].equalsIgnoreCase("Salesmate")){
                salesmate = true;
            }else if(sSplit[1].equalsIgnoreCase("Numbering")){
                numbering = true;
            }
            //projectTest = sSplit[1];
        }else if (text.startsWith("Set(\"deviceName\"")){
            deviceName = sSplit[1];
        }else if (text.startsWith("Set(\"udid\"")){
            //caps.setCapability("udid", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"platformName\"")){
            //caps.setCapability("platformName", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"platformVersion\"")){
            androidVersion = sSplit[1];
        }else if (text.startsWith("Set(\"skipUnlock\"")){
            //caps.setCapability("skipUnlock", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"appPackage\"")){
            //caps.setCapability("appPackage", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"appActivity\"")){
            //caps.setCapability("appActivity", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"noReset\"")){
            if(sSplit[1].equalsIgnoreCase("true")){
                isNoReset = true;
            }else{
                isNoReset = false;
            }
        }else if (text.startsWith("Set(\"autoGrantPermissions\"")){
            //caps.setCapability("autoGrantPermissions", sSplit[1]); //permission auto approve
        }
    }
    
    public static void replaceLines(File f, int no, String s) {
        try {
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(f));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            int count = 1;
            while ((line = file.readLine()) != null) {
                if (count == no) {
                    line = s;
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
                count++;
            }
            file.close();
            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream("SETUP.txt");
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }
}
