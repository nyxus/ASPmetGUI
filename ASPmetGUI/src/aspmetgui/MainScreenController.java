/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aspmetgui;

import aspmetgui.Exceptions.AlgorithmNotSet;
import static aspmetgui.Marian.MarianOptimised;
import static aspmetgui.Marian.MarianOrignal;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
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

    private boolean toggleFullscreen = false;
    private boolean toggleBlockNr = true;

    private ASP application;

    private double mutationPercentage = 2.25;

    private int populationSize = 150;
    private int stopTime = 120;
    private int stopNrGenerations = 100;
    private int optimationNrParts = 4;
    private int cycles = 5;


    private ArrayList<TextField> arrayListOptimationParts = new ArrayList<>();
    private ArrayList<String> filepaths = new ArrayList<>();

    private String console = "";
    private String directory;

    private Thread taskManagerThread;
    private int amountMarianRuns = 0;
    private ObservableList<XYChart.Series<String, Integer>> runList;
    private Series<String, Integer> runOriginal;
    private Series<String, Integer> runOptimised;

    @FXML
    Parent root;

    @FXML
    private Label labelPopulationSize;
    @FXML
    private Label labelMutationPercentage;
    @FXML
    private Label labelOptimationParts;
    @FXML
    private Label labelOptimationRemaining;

    @FXML
    private CheckBox checkboxStopTime;
    @FXML
    private CheckBox checkboxStopNrGenerations;
    @FXML
    private CheckBox checkboxStopInfinite;
    @FXML
    private CheckBox checkboxMarian;
    @FXML
    private CheckBox checkboxMarianWithOptimization;

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
    private Slider sliderCycles;

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
    private GridPane gridPaneOptimationParts;

    @FXML
    private LineChart lineChartMinMax;
    
    @FXML
    private LineChart lineChartCompare;
    
    @FXML
    private LineChart lineChartFitness;
    
    @FXML
    private LineChart lineChartFitnessOptimized;

    @FXML
    private Tab tabMinMaxMarian;
    @FXML
    private Tab tabFitness;
    @FXML
    private Tab tabMinMaxMarianOptimized;
    @FXML
    private Tab tabFitnessOptimized;

    @FXML
    private Canvas canvasProblemGraphical;

    @FXML
    private ProgressBar progressBar;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

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
        sliderCycles.setValue(cycles);

        labelPopulationSize.setText("Populationsize ( " + populationSize + " )");
        labelMutationPercentage.setText("Mutationpercentage ( " + mutationPercentage + "% )");
        checkboxStopTime.setText("Time ( " + stopTime + "SEC )");
        checkboxStopNrGenerations.setText("Nr Of Generations ( " + mutationPercentage + " )");
        labelOptimationParts.setText("Nr Of Parts ( " + optimationNrParts + " )");
        
        /*
        this.runList = lineChartCompare.getData();
        this.runOriginal = new Series<>();
        this.runOptimised = new Series<>();
        this.runOriginal.setName("Marian original");
        this.runOptimised.setName("Marian optimised"); 
        this.runList.addAll(runOriginal, runOptimised);
        this.lineChartCompare.setData(runList);
        */
        initializeCanvas();
        

//        final NumberAxis xAxis = new NumberAxis();
//        final NumberAxis yAxis = new NumberAxis(-1000,1000,10);
//        xAxis.setAutoRanging(false);
//        yAxis.setAutoRanging(false);
//         lineChartMinMax = new LineChart[5,50](xAxis, yAxis);
//        tabMinMaxMarian.setContent(lineChartMinMax);

        setLabelOptimationParts();
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
            for (String filename : children) {
                if (checkIfProblem(directory + "\\" + filename)) {
                    choiceBoxProblems.getItems().add(filename);
                    filepaths.add(directory + "\\" + filename);
                }
            }
            choiceBoxProblems.getSelectionModel().selectFirst();
        }
    }

    public boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean checkIfProblem(String filepath) {
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
        }
        return isNumeric(strLine);
    }

    public File loadProblem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please open a problem");
        File file = fileChooser.showOpenDialog(stage);

        return file;
    }

 
    public void runMarian() {
        
        populationSize = (int) sliderPopulationSize.getValue();
        int nrOfGenerations = (int) Math.round(sliderStopNrGenerations.getValue());
        long runTime = Math.round(sliderStopTime.getValue()) * 1000;
        System.out.println("time:" + runTime);
        mutationPercentage = (double) sliderMutationPercentage.getValue();
        System.out.println("populationSize: " + populationSize);
        System.out.println("mutationPercentage: " + mutationPercentage);

        a(filepaths.get(choiceBoxProblems.getSelectionModel().getSelectedIndex()));
        String filename = filepaths.get(choiceBoxProblems.getSelectionModel().getSelectedIndex());


        StopConditionsMarian stopConditions = new StopConditionsMarian(checkboxStopNrGenerations.isSelected(), checkboxStopTime.isSelected(), checkboxStopInfinite.isSelected(), nrOfGenerations, runTime);

        Marian marian = new Marian(filename, populationSize, mutationPercentage, 5, stopConditions, getOptimizedSelectionMarian());
        Population newPop = marian.generatePopulationBetter(populationSize);
        
        
        marian.setUsePopulation(newPop);

        lineChartFitness.getData().clear();
//        lineChartFitnessOptimized.getData().clear();
        lineChartMinMax.getData().clear();
        //lineChartCompare.getData().clear();
                        
        ArrayList<Integer> Algorithms = new ArrayList<>();

        if(this.checkboxMarian.isSelected()){
            Algorithms.add(Marian.MarianOrignal);
        }
        if(this.checkboxMarianWithOptimization.isSelected()){
            Algorithms.add(Marian.MarianOptimised);
        }
        
        
        TaskManager taskManager = new TaskManager(marian,  this, (int)Math.round(sliderCycles.getValue()), Algorithms, lineChartMinMax, lineChartFitness, lineChartCompare);
        
        taskManager.valueProperty().addListener(new ChangeListener<TaskManager.TaskUpdate>() {
            @Override
            public void changed(ObservableValue<? extends TaskManager.TaskUpdate> observable, TaskManager.TaskUpdate oldValue, TaskManager.TaskUpdate newValue) {
                switch(newValue.getUpdateType()){
                    case TaskManager.TaskUpdate.TaskNotInialised:
                        break;
                    case Marian.MarianOrignal:
                    case Marian.MarianOptimised:
                    case TaskManager.TaskUpdate.TaskEnd:
                        ObservableList compare = lineChartCompare.getData();
                        compare.addAll(newValue.getList().get(0), newValue.getList().get(1));
                        break;
                }
            }
        });
        
        taskManager.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                a(newValue);
            }
        });
        
        
        
         String startMessage = "";
        
        if(Algorithms.size() > 0){
            startMessage = "Start algorithm(s): ";
            for (Integer Algorithm : Algorithms) {
                switch (Algorithm) {
                    case Marian.MarianOrignal:
                         startMessage += "marian orignal, "; 
                        break;
                    case Marian.MarianOptimised:
                        startMessage += "marian optimised, "; 
                        break;
 
                } 
            }
            startMessage += "problem size: " + marian.getProblemSize() + " settings: " + marian.getPopulationSize() + ", " + marian.getMutationPercentage();
            startMessage += " cycles: " + cycles;
            a(startMessage);
            
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().bind(taskManager.progressProperty());
            
            taskManagerThread = new Thread(taskManager);
            taskManagerThread.setDaemon(true);
            taskManagerThread.start();
            
        }else{
            a("Program can not start, please select an algorithm");
        }

        

        Task<Canvas> drawProblem = new drawProblem(canvasProblemGraphical, marian, toggleBlockNr);

        drawProblem.valueProperty().addListener(new ChangeListener<Canvas>() {
            @Override
            public void changed(ObservableValue<? extends Canvas> observable, Canvas oldValue, Canvas newValue) {
                gridPaneProblem.add(newValue, 0, 2);
            }
        });

        drawProblem.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                a(newValue);
            }
        });

        Thread drawTask = new Thread(drawProblem);
        drawTask.setDaemon(true);
        drawTask.start();

    }

    public Double[] getOptimizedSelectionMarian() {
        Double[] collection = new Double[arrayListOptimationParts.size()];
        double count = 0;

        for (int i = 0; i < arrayListOptimationParts.size(); i++) {
            Double retrievedDouble = Double.parseDouble(arrayListOptimationParts.get(i).getText().replace(",", "."));

            count += retrievedDouble;

            collection[i] = retrievedDouble;
        }

        return collection;
    }
    
    public void clearCharts(){
        stopOperation();
        lineChartFitness.getData().clear();
        lineChartFitnessOptimized.getData().clear();
        lineChartMinMax.getData().clear();
        lineChartCompare.getData().clear();
        a("Clear graphs");
        
    }

    public void stopOperation() {
        taskManagerThread.interrupt();
        a("Application stopped.");
    }

    public void a(String alert) {
        console = alert + "\n" + console;

        textAreaConsole.setText(console);
    }

    public void setLabelPopulationSize() {
        populationSize = (int) sliderPopulationSize.getValue();

        labelPopulationSize.setText("Populationsize ( " + populationSize + " )");
    }

    public void setLabelMutationPercentage() {
        mutationPercentage = (double) sliderMutationPercentage.getValue();
        DecimalFormat df = new DecimalFormat("0.00");

        labelMutationPercentage.setText("Mutationpercentage ( " + df.format(mutationPercentage) + "% )");
    }

    public void setLabelStopTime() {
        stopTime = (int) sliderStopTime.getValue();
        checkboxStopTime.setText("TIME ( " + stopTime + "SEC )");
    }

    public void setLabelStopNrGenerations() {
        stopNrGenerations = (int) sliderStopNrGenerations.getValue();

        checkboxStopNrGenerations.setText("Nr Of Generations ( " + stopNrGenerations + " )");
    }

    public void updateCheckBoxes() {
        if (checkboxStopInfinite.isSelected()) {
            checkboxStopTime.setDisable(true);
            checkboxStopNrGenerations.setDisable(true);
            sliderStopTime.setDisable(true);
            sliderStopNrGenerations.setDisable(true);
        } else {
            checkboxStopTime.setDisable(false);
            checkboxStopNrGenerations.setDisable(false);
            sliderStopTime.setDisable(false);
            sliderStopNrGenerations.setDisable(false);
        }

        if (checkboxStopTime.isSelected() || checkboxStopNrGenerations.isSelected()) {
            checkboxStopInfinite.setDisable(true);
        } else {
            checkboxStopInfinite.setDisable(false);
        }
    }

    public void setLabelOptimationParts() {
        gridPaneSettings.getChildren().remove(gridPaneOptimationParts);
        gridPaneOptimationParts = new GridPane();
        gridPaneSettings.add(gridPaneOptimationParts, 1, 20);

        gridPaneOptimationParts.setAlignment(Pos.CENTER);

        optimationNrParts = (int) sliderOptimationNrParts.getValue();
        labelOptimationParts.setText("Nr Of Parts ( " + optimationNrParts + " )");

        arrayListOptimationParts.clear();
        double part = (1 / Double.parseDouble("" + optimationNrParts));

        for (int i = 0; i < optimationNrParts; i++) {
            DecimalFormat df = new DecimalFormat("0.00");
            TextField tf = new TextField();
            tf.setPrefWidth(288);
            tf.setText(df.format(part) + "");

            tf.setAlignment(Pos.CENTER);

            //Setting an action for the Clear button
            tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    calculateRemaining();
                }
            });
            //Setting an action for the Clear button
            tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
                public void handle(KeyEvent event) {
                    calculateRemaining();
                }
            });
            //Setting an action for the Clear button
            tf.setOnKeyTyped(new EventHandler<KeyEvent>() {
                public void handle(KeyEvent event) {
                    calculateRemaining();
                }
            });

            arrayListOptimationParts.add(tf);
            gridPaneOptimationParts.addRow(20, arrayListOptimationParts.get(i));
        }

        calculateRemaining();
    }

    public void toggleBlockNr() {
        if (toggleBlockNr) {
            toggleBlockNr = false;
        } else {
            toggleBlockNr = true;
        }
    }

    public void toggleFullscreen() {
        application.toggleFullscreen();
    }

    boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void calculateRemaining() {
        double remaining = 0;
        double temp;

        DecimalFormat df = new DecimalFormat("0.00");
        for (int i = 0; i < arrayListOptimationParts.size(); i++) {
            arrayListOptimationParts.get(i).getStyleClass().remove("wrong-textfield");
            if (isDouble(arrayListOptimationParts.get(i).getText().replace(",", "."))) {
                temp = Double.parseDouble(arrayListOptimationParts.get(i).getText().replace(",", "."));

                remaining += temp;
            } else {
                arrayListOptimationParts.get(i).getStyleClass().add("wrong-textfield");
            }
        }

        remaining = 1 - remaining;

        labelOptimationRemaining.setText("Remaining ( " + df.format(remaining) + " )");
    }

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
