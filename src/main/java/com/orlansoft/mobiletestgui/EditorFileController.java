package com.orlansoft.mobiletestgui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author ridwan
 */
public class EditorFileController implements Initializable {
    
    @FXML
    private TextArea textArea;
    @FXML
    private Label statusMessage;
    private File loadedFileReference;
    private FileTime lastModifiedTime;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void chooseFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        //only allow text files to be selected using chooser
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
        );
        //set initial directory somewhere user will recognise
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") +"/test"));
        //let user select file
        File fileToLoad = fileChooser.showOpenDialog(null);
        //if file has been chosen, load it using asynchronous method (define later)
        if (fileToLoad != null) {
            loadFileToTextArea(fileToLoad);
        }
    }
    
    private void loadFileToTextArea(File fileToLoad) {
        Task<String> loadFileTask = fileLoaderTask(fileToLoad);
        loadFileTask.run();
    }
    
    private Task<String> fileLoaderTask(File fileToLoad) {
        //Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));
                //Use Files.lines() to calculate total lines - used for progress
                long lineCount;
                try (Stream<String> stream = Files.lines(fileToLoad.toPath())) {
                    lineCount = stream.count();
                }
                //Load in all lines one by one into a StringBuilder separated by "\n" - compatible with TextArea
                String line;
                StringBuilder totalFile = new StringBuilder();
                long linesLoaded = 0;
                while ((line = reader.readLine()) != null) {
                    totalFile.append(line);
                    totalFile.append("\n");
                    updateProgress(++linesLoaded, lineCount);
                }
                return totalFile.toString();
            }
        };
        //If successful, update the text area, display a success message and store the loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                textArea.setText(loadFileTask.get().toString());
                statusMessage.setText("File loaded: " + fileToLoad.getPath());
                loadedFileReference = fileToLoad;
                //scheduleFileChecking(loadedFileReference);
            } catch (InterruptedException | ExecutionException e) {
                Logger.getLogger(getClass().getName()).log(SEVERE, null, e);
                textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            }
        });
        //If unsuccessful, set text area with error message and status message to failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            statusMessage.setText("Failed to load file");
        });
        return loadFileTask;
    }
    
    private ScheduledService<Boolean> createFileChangesCheckingService(File file) {
        ScheduledService<Boolean> scheduledService = new ScheduledService<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        FileTime lastModifiedAsOfNow = Files.readAttributes(file.toPath(), BasicFileAttributes.class).lastModifiedTime();
                        return lastModifiedAsOfNow.compareTo(lastModifiedTime) > 0;
                    }
                };
            }
        };
        scheduledService.setPeriod(Duration.seconds(10));
        return scheduledService;
    }
    
    private void scheduleFileChecking(File file) {
        ScheduledService<Boolean> fileChangeCheckingService = createFileChangesCheckingService(file);
        fileChangeCheckingService.setOnSucceeded(workerStateEvent -> {
            //first time task runs, this may be null. Avoid NPE.
            if (fileChangeCheckingService.getLastValue() == null) {
                return;
            }
            if (fileChangeCheckingService.getLastValue()) {
                //no need to keep checking
                fileChangeCheckingService.cancel();
                notifyUserOfChanges();
            }
        });
        System.out.println("Starting Checking Service...");
        fileChangeCheckingService.start();
    }
    
    private void notifyUserOfChanges() {
        //loadChangesButton.setVisible(true);
    }
    
    public void loadChanges(ActionEvent event) {
        loadFileToTextArea(loadedFileReference);
        //loadChangesButton.setVisible(false);
    }
    
    public void saveFile(ActionEvent event) {
        if (loadedFileReference != null) {
            try {
                FileWriter myWriter = new FileWriter(loadedFileReference);
                myWriter.write(textArea.getText());
                myWriter.close();
                lastModifiedTime = FileTime.fromMillis(System.currentTimeMillis() + 3000);
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(SEVERE, null, e);
            }
        }
    }
    
    public void closeWindows(){
        // get a handle to the stage
        Stage stage = (Stage) textArea.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
    
}
