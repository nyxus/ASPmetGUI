/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aspmetgui;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author LAPTOPPT
 */
public class ASP extends Application {
    private Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        loadStartScreen();
        stage.setTitle("ASP volgens Marian door Peter Tielbeek en Gerco Versloot");
        System.out.println(ASP.class.getResource("Icon.png").getPath());
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));
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
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void setFullscreen(Boolean fullscreen){
        stage.setFullScreen(fullscreen);
    }
    
}
