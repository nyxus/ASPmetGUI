package aspmetgui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public void extractFileAndRun(){

        try {
            InputStream is = getClass().getResourceAsStream("GeneticProblem.exe");

            OutputStream os = new FileOutputStream("GeneticProblem.exe");

            byte[] buffer = new byte[1024];
            int bytesRead;
            //read from is to buffer
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            //flush OutputStream to write any buffered data to file
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Process ps = Runtime.getRuntime().exec(new String[]{"cmd.exe","/c","start","GeneticProblem.exe"});
        } catch (IOException ex) {
            Logger.getLogger(StartScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void specifyDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setTitle("Please choose the directory containing the problems");
        String path = getClass().getResource("").getPath();
        System.out.print(path.substring(0, path.length()-25));
        chooser.setInitialDirectory(new File(path.substring(6, path.length()-26)));
        File file = chooser.showDialog(stage);

        application.loadMainScreen(file.getAbsolutePath());
    }

    public void toggleFullscreen() {
        application.toggleFullscreen();
    }

    public void setApp(ASP application) {
        this.application = application;
    }
}
