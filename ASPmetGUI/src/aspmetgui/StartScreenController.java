package aspmetgui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author LAPTOPPT
 */
public class StartScreenController implements Initializable {

    @FXML
    Parent root;
    Stage stage;
    boolean toggleFullscreen = false;
    private ASP application;
    final int BUFFER = 2048;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void specifyDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setTitle("Please choose the directory containing the problems");
        File file = chooser.showDialog(stage);

        application.loadMainScreen(file.getAbsolutePath());
    }

    public void toggleFullscreen() {
        if (toggleFullscreen) {
            toggleFullscreen = false;
        } else {
            toggleFullscreen = true;
        }
        application.setFullscreen(toggleFullscreen);
    }

    public void setApp(ASP application) {
        this.application = application;
    }
}
