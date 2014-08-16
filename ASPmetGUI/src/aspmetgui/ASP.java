/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aspmetgui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author LAPTOPPT
 */
public class ASP extends Application {
    private Stage stage;
    private Boolean fullscreen = false;
    
        
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        loadStartScreen();
        stage.setTitle("ASP volgens Marian door Peter Tielbeek en Gerco Versloot");
        System.out.println(ASP.class.getResource("Icon.png").getPath());
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));
         
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        //set Stage boundaries to visible bounds of the main screen
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        stage.show();
    }
        
    public void loadStartScreen() {
        try {
            StartScreenController StartScreen = (StartScreenController) replaceSceneContent("StartScreen.fxml");
            StartScreen.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(ASP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void toggleFullscreen(){
        if(this.fullscreen){
            this.fullscreen = false;
        } else {
            this.fullscreen = true;
        }
        this.stage.setFullScreen(fullscreen);
    }
        
    public void loadMainScreen(String directory) {
        try {
            MainScreenController MainScreen = (MainScreenController) replaceSceneContent("MainScreen.fxml");
            MainScreen.setApp(this);
            MainScreen.setDirectory(directory);
        } catch (Exception ex) {
            Logger.getLogger(ASP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = ASP.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(ASP.class.getResource(fxml));
        BorderPane page;
        try {
            page = (BorderPane) loader.load(in);
        } finally {
            in.close();
        } 
        Scene scene = new Scene(page);
        Font.loadFont(ASP.class.getResource("BebasNeue.otf").toExternalForm(), 10);
        
        stage.setScene(scene);
        stage.setFullScreen(fullscreen);            
        
        return (Initializable) loader.getController();
    }
    
    private static void createFixedProblem(int problemSize) throws IOException {
        
        Path path = Paths.get("./"+problemSize+" Fixed");

	 //creates a file
        Files.createFile(path);
                  
                  
        String msg = "";
        int block_counter = 1;
        for (int line = 0; line < problemSize; line++) {
            for (int collum = 0; collum < problemSize; collum++) {
                msg += block_counter++ + " " + collum + " " + collum + " " + line + " " + line + "\n" ;  
            }
        }
        
        Files.write(path, msg.getBytes());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        try {
//            createFixedProblem(2);
//            createFixedProblem(3);
//            createFixedProblem(4);
//            createFixedProblem(6);
//            createFixedProblem(7);
//            createFixedProblem(10);
//        } catch (IOException ex) {
//            Logger.getLogger(ASP.class.getName()).log(Level.SEVERE, null, ex);
//        }
            launch(args);
    }
    
}
