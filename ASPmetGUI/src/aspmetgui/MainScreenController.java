/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aspmetgui;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author LAPTOPPT
 */
public class MainScreenController implements Initializable {
    private Stage stage;
    
    boolean toggleFullscreen = false;
    
    private ASP application;
    
    private double mutationPercentage = 2.25;
    
    private int populationSize = 10;
    private int stopTime = 120;
    private int stopNrGenerations = 100;
    private int optimationNrParts = 0;
    
    private Marian marian;
    
    private ArrayList<TextField> arrayListOptimationParts = new ArrayList<>();
    private ArrayList<String> filepaths = new ArrayList<>();
    
    private String console = "";
    private String directory;
    
    private Thread tr;

    @FXML
    Parent root;

    @FXML
    private Label labelPopulationSize;
    @FXML
    private Label labelMutationPercentage;
    @FXML
    private Label labelOptimationParts;

    @FXML
    private CheckBox checkboxStopTime;
    @FXML
    private CheckBox checkboxStopNrGenerations;

    @FXML
    private Slider sliderPopulationSize;
    @FXML
    private Slider sliderMutationPercentage;
    @FXML
    private Slider sliderStopTime;
    @FXML
    private Slider sliderStopNrGenerations;
    @FXML
    private Slider sliderOptimationNrParts;

    @FXML
    private TextArea textAreaConsole;

    @FXML
    private Button buttonStart;

    @FXML
    private ChoiceBox choiceBoxProblems;

    @FXML
    private GridPane gridPaneProblem;

    @FXML
    private GridPane gridPaneSettings;

    @FXML
    private LineChart lineChartMinMax;
    @FXML
    private LineChart lineChartFitness;

    @FXML
    private Canvas canvasProblemGraphical;
    
    @FXML
    private ProgressBar progressBar;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sliderPopulationSize.setValue(populationSize);
        sliderMutationPercentage.setValue(mutationPercentage);
        sliderStopTime.setValue(stopTime);
        sliderStopNrGenerations.setValue(stopNrGenerations);
        sliderOptimationNrParts.setValue(optimationNrParts);

        labelPopulationSize.setText("Populationsize ( " + populationSize + " )");
        labelMutationPercentage.setText("Mutationpercentage ( " + mutationPercentage + "% )");
        checkboxStopTime.setText("Time ( " + stopTime + "SEC )");
        checkboxStopNrGenerations.setText("Nr Of Generations ( " + mutationPercentage + " )");
        labelOptimationParts.setText("Nr Of Parts ( " + optimationNrParts + " )");
        initializeCanvas();
    }

    public void initializeCanvas() {
        GraphicsContext gc = canvasProblemGraphical.getGraphicsContext2D();
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        String defaultText = "Please run a problem first";
        double characterWidth = 5;

        gc.setFill(Color.BLACK);
        gc.fillText("Please run a problem first", canvasWidth / 2 - ((defaultText.length() / 2) * characterWidth), canvasHeight / 2);
    }

    public void searchDirectory(String directory) {
        ArrayList<String> filenames = new ArrayList<>();

        File dir = new File(directory);

        String[] children = dir.list();

        if (children == null) {
            a("Either dir does not exist or is not a directory");
        } else {
            for (int i = 0; i < children.length; i++) {
                String filename = children[i];

                if (checkIfProblem(directory + "\\" + filename)) {
                    choiceBoxProblems.getItems().add(filename);
                    filepaths.add(directory + "\\" + filename);
                }
            }
            choiceBoxProblems.getSelectionModel().selectFirst();
        }
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean checkIfProblem(String filepath) {
        int problemSize = 0;
        int compare = 0;
        String splitarray[];
        String strLine = "";

        try {
            //Open bestand op de opgegeven locatie
            FileInputStream fstream = new FileInputStream(filepath);

            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            //Lees bestand per regel
            strLine = br.readLine().replace(" ", "");

            //Close the input stream
            in.close();
        } catch (Exception e) {
            //Catch exception if any
            System.out.println("checkIfProblemError: " + e.getMessage());
        }
        return isNumeric(strLine);
    }

    @FXML
    public File loadProblem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please open a problem");
        File file = fileChooser.showOpenDialog(stage);

        return file;
    }

    @FXML
       public void runMarian() {
        populationSize = (int) sliderPopulationSize.getValue();
        mutationPercentage = (double) sliderMutationPercentage.getValue();

        System.out.println("populationSize: " + populationSize);
        System.out.println("mutationPercentage: " + mutationPercentage);

        a(filepaths.get(choiceBoxProblems.getSelectionModel().getSelectedIndex()));
        final Marian marianTask = new Marian(filepaths.get(choiceBoxProblems.getSelectionModel().getSelectedIndex()), populationSize, mutationPercentage);
               marian = marianTask;
 
        
       //  copyWorker = createWorker(file, populationSize, mutationPercentage);
        final int totalIterations = 100;
        Task<Integer> Task = new Task<Integer>() {
            @Override 
            protected Integer call() throws Exception {
                int iterations;
                
                
                Population pop = marianTask.generatePopulationBetter(populationSize);

                for (iterations = 0; iterations < totalIterations; iterations++) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
                    
                  
                    
                    System.out.println("Start crossover");
                    pop = marianTask.crossOver(pop);
                    System.out.println("Start selection");
                    pop = marianTask.getSelectionPandG(pop, populationSize);
                    System.out.println("Start mutation");
                    pop = marianTask.pseudoMutation(pop);
                      System.out.println( "new avg" + (1.0/(double)marianTask.getFirstMin().getCosts() ) / (1.0/(double)pop.getMin().getCosts())   );
                    System.out.println("Gener " + iterations + ": Max: " + pop.getMax().getCosts() + "  Min: " + pop.getMin().getCosts());
                    System.out.println("Gener " + iterations + ": Max: " + pop.getMax().getFitness() + "  Min(from first): " + (1.0-(1.0/((double)marianTask.getFirstMin().getCosts()/(double)pop.getMin().getCosts())))*10 + "  AVG: " + pop.getAverageFittness() );
                    System.out.println("---------------------------------------------------------------");

                    updateProgress(iterations, totalIterations);
                }
                return iterations;
            }
        };
        
        
        Task<Canvas> drawProblem = new Task<Canvas>() {
            @Override 
            protected Canvas call() throws Exception {
                System.out.println("Drawing problem building");
                canvasProblemGraphical = new Canvas(canvasProblemGraphical.getWidth(),canvasProblemGraphical.getHeight());
                
                GraphicsContext gc = canvasProblemGraphical.getGraphicsContext2D();
                double canvasWidth = gc.getCanvas().getWidth();
                double canvasHeight = gc.getCanvas().getHeight();


                ArrayList<Block> blockCollection = marian.getBlockCollection();
                gc.clearRect(0, 0, canvasWidth, canvasHeight);
                gc.setFill(Color.GREEN);
                gc.fill();
                gc.setStroke(Color.BLUE);

                int id;
                double minx;
                double maxx;
                double miny;
                double maxy;
                double height;
                double width;
                Random random = new Random();

                double gridSize = (canvasWidth / (marian.getLength()));
                
                for (Block block : blockCollection) {
                    minx = block.getMinX();
                    maxx = block.getMaxX();
                    miny = block.getMinY();
                    maxy = block.getMaxY();
                    width = block.getWidth();
                    height = block.getHeight();
                    id = block.getID();

                    double x = minx * gridSize;
                    double y = canvasHeight - height * gridSize - (miny * gridSize);
                    double breedte = width * gridSize;
                    double hoogte = height * gridSize;
                    int randomcolor;
                    
                    //            x - x position of the upper left corner of the rectangle.
                    //            y - y position of the upper left corner of the rectangle.
                    //            w - width of the rectangle.
                    //            h - height of the rectangle.

                    int min = 200;
                    int max = 250;
                    randomcolor = random.nextInt(max - min) + min;

                    gc.setFill(Color.rgb(randomcolor, randomcolor, randomcolor));
                    gc.fillRect(x, y, breedte-1, hoogte-1);
                    
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(1);
                    gc.strokeLine(x, y, x+breedte, y);
                    gc.strokeLine(x, y, x, y+hoogte);
                    gc.strokeLine(x, y+hoogte, x+breedte, y+hoogte);
                    gc.strokeLine(x+breedte, y, x+breedte, y+hoogte);
                    
                    gc.setStroke(Color.WHITE);
                    gc.stroke();
                    gc.setFill(Color.BLACK);
                    gc.fillText(Integer.toString(id), x + breedte / 2, y + hoogte / 2);
        
                    updateMessage("Progress: " + block.getID() + "-"+blockCollection.size());
                    //updateValue(canvasProblemGraphical);
                    
                }
                return canvasProblemGraphical;
            }
        };


        
        
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(Task.progressProperty());
        
        
        Task.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                a(newValue);
            }
        });
        
        Task.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                System.out.println("output: " + newValue);
            }
        });
        
        drawProblem.valueProperty().addListener(new ChangeListener<Canvas>() {
            @Override
            public void changed(ObservableValue<? extends Canvas> observable, Canvas oldValue, Canvas newValue) {
                gridPaneProblem.add(newValue, 0, 2);
                System.out.println("Drawing problem");
            }
        });
        
        drawProblem.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                a(newValue);
            }
        });
        
        this.tr = new Thread(Task);
        tr.setDaemon(true);
        tr.start();
        
        Thread drawTask = new Thread(drawProblem);
        drawTask.setDaemon(true);
        drawTask.start();
        

        
        
        //a("File "+file.getAbsolutePath()+" loaded.");
        //marian = new Marian(file.getAbsolutePath(), populationSize, mutationPercentage);
       // marian.start();
        
        


        a("done! o.O");
    }

    public void stopOperation() {
        //tr.stop();
        a("Application stopped.");
    }

    public void a(String alert) {
        console = alert + "\n" + console;

        textAreaConsole.setText(console);
    }

    @FXML
    public void setLabelPopulationSize() {
        populationSize = (int) sliderPopulationSize.getValue();

        labelPopulationSize.setText("Populationsize ( " + populationSize + " )");
    }

    @FXML
    public void setLabelMutationPercentage() {
        mutationPercentage = (double) sliderMutationPercentage.getValue();
        DecimalFormat df = new DecimalFormat("0.00");

        labelMutationPercentage.setText("Mutationpercentage ( " + df.format(mutationPercentage) + "% )");
    }

    @FXML
    public void setLabelStopTime() {
        stopTime = (int) sliderStopTime.getValue();

        checkboxStopTime.setText("TIME ( " + stopTime + "SEC )");
    }

    @FXML
    public void setLabelStopNrGenerations() {
        stopNrGenerations = (int) sliderStopNrGenerations.getValue();

        checkboxStopNrGenerations.setText("Nr Of Generations ( " + stopNrGenerations + " )");
    }

    @FXML
    public void setLabelOptimationParts() {
        optimationNrParts = (int) sliderOptimationNrParts.getValue();
        int startrow = 14;


        for (int i = 0; i < optimationNrParts; i++) {
            TextField tf = new TextField();
            tf.setPrefWidth(10);
            tf.setMaxWidth(30);
            tf.setId("textFieldOptimationPart" + i);
            arrayListOptimationParts.add(tf);
        }
            gridPaneSettings.getChildren().addAll(arrayListOptimationParts);

        labelOptimationParts.setText("Nr Of Parts( " + optimationNrParts + " )");
    }

    @FXML
    public void toggleFullscreen() {
        if (toggleFullscreen) {
            toggleFullscreen = false;
        } else {
            toggleFullscreen = true;
        }
        application.setFullscreen(toggleFullscreen);
    }

    @FXML
    public void stopProgram() {
        System.exit(0);
    }

    public void setApp(ASP application) {
        this.application = application;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
        searchDirectory(directory);
    }

}
