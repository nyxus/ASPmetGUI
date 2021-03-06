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
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
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
    private int optimationNrParts = 3;
    private int cycles = 2;

    private ArrayList<TextField> arrayListOptimationParts = new ArrayList<>();
    private ArrayList<String> filepaths = new ArrayList<>();

    private String console = "";
    private String directory;

    private Thread taskManagerThread;
    private int amountMarianRuns = 0;
    private ObservableList<XYChart.Series<String, Integer>> runList;
    private Series<String, Integer> runOriginal;
    private Series<String, Integer> runOptimised;
    private boolean running = false;

    @FXML
    Parent root;

    @FXML
    private Label labelPopulationSize;
    @FXML
    private Label labelMutationPercentage;
    @FXML
    private Label labelOptimationParts;
    @FXML
    private Label labelCycles;
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
    private LineChart newlineChartMinMax;

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
     * initialize Initializes all the javafx components before the components
     * are shown
     *
     * @author Peter Tielbeek.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Initialize sliders to default value
        sliderPopulationSize.setValue(populationSize);
        sliderMutationPercentage.setValue(mutationPercentage);
        sliderStopTime.setValue(stopTime);
        sliderStopNrGenerations.setValue(stopNrGenerations);
        sliderOptimationNrParts.setValue(optimationNrParts);
        sliderCycles.setValue(cycles);

        //Initialize labels to default value
        labelPopulationSize.setText("Populationsize ( " + populationSize + " )");
        labelMutationPercentage.setText("Mutationpercentage ( " + mutationPercentage + "% )");
        labelOptimationParts.setText("Nr Of Parts ( " + optimationNrParts + " )");
        labelCycles.setText("Nr Of Cycles ( " + cycles + " )");

        //Initialize checkboxes to default value
        checkboxStopTime.setText("Time ( " + stopTime + "SEC )");
        checkboxStopNrGenerations.setText("Nr Of Generations ( " + mutationPercentage + " )");

        //Initialize canvas
        initializeCanvas();

        //Initialize textboxes of the optimation algorithm
        initializeOptimationParts();
        


    }
    
    /**
     * specifyDirectory shows a directorychooser where the user can select a
     * directory where the problems could be found. Then it will set the
     * directory as the default directory in the application.
     *
     * @author Peter Tielbeek.
     */
    public void specifyDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setTitle("Please choose the directory containing the problems");
        String path = getClass().getResource("").getPath();
        System.out.print(path.substring(0, path.length() - 25));
        chooser.setInitialDirectory(new File(path.substring(6, path.length() - 26)));
        File file = chooser.showDialog(stage);

        setDirectory(file.getAbsolutePath());
    }

    /**
     * initializeCanvas initializes the canvas for first use.
     *
     * @author Peter Tielbeek.
     */
    public void initializeCanvas() {
        GraphicsContext gc = canvasProblemGraphical.getGraphicsContext2D();
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        String defaultText = "Please run a problem first";
        double characterWidth = 5;

        gc.setFill(Color.BLACK);
        gc.fillText("Please run a problem first", canvasWidth / 2 - ((defaultText.length() / 2) * characterWidth), canvasHeight / 2);
    }

    /**
     * searchDirectory searches the specified directory for problems and adds
     * them to the choicebox in the settings menu.
     *
     * @param directory a string with a filepath
     * @author Peter Tielbeek.
     */
    public void searchDirectory(String directory) {
        ArrayList<String> filenames = new ArrayList<>();

        File dir = new File(directory);

        String[] children = dir.list();

        if (children == null) {
            a("Either dir does not exist or is not a directory");
        } else {
            choiceBoxProblems.getItems().clear();
            filepaths.clear();
            for (String filename : children) {
                if (isProblem(directory + "\\" + filename)) {
                    choiceBoxProblems.getItems().add(filename);
                    filepaths.add(directory + "\\" + filename);
                }
            }
            if (choiceBoxProblems.getItems().size() == 0) {
                choiceBoxProblems.getItems().add("No problems found");
            }
            choiceBoxProblems.getSelectionModel().selectFirst();
        }
    }

    /**
     * isNumeric checks if a string is numeric.
     *
     * @param string a String.
     * @return returns true if a string is numeric.
     * @author Peter Tielbeek.
     */
    public boolean isNumeric(String string) {
        try {
            double d = Double.parseDouble(string);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * isProblem
     *
     * @param filepath a String.
     * @return returns true if a string is numeric.
     * @author Peter Tielbeek.
     */
    public boolean isProblem(String filepath) {
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

    /**
     * runMarian starts the Marian algorithm with the given settings.
     *
     * @author Peter Tielbeek and Gerco Versloot.
     */
    public void runMarian() {
        // check if task already running
        if (running) {
            a("Application is aready running");
        } else if (choiceBoxProblems.getSelectionModel().getSelectedItem() == "No problems found") {
            a("No problems problem selected");
        } else {
            populationSize = (int) sliderPopulationSize.getValue();
            int nrOfGenerations = (int) Math.round(sliderStopNrGenerations.getValue());
            long runTime = Math.round(sliderStopTime.getValue()) * 1000;
            System.out.println("time:" + runTime);
            mutationPercentage = (double) sliderMutationPercentage.getValue();
            System.out.println("populationSize: " + populationSize);
            System.out.println("mutationPercentage: " + mutationPercentage);
            
            NumberAxis xaxisFit = new NumberAxis("Generations", 0, nrOfGenerations, 10);
            NumberAxis yaxisFit = new NumberAxis("Fitness", 0.5, 1, 10);
            xaxisFit.autoRangingProperty().set(true);
            yaxisFit.autoRangingProperty().set(true);
            lineChartFitness = new LineChart(xaxisFit,yaxisFit);
            lineChartFitness.autosize();
            tabFitness.setContent(lineChartFitness);

            NumberAxis xaxis = new NumberAxis("Generations", 0, nrOfGenerations, 10);
            xaxis.autoRangingProperty().set(true);
            NumberAxis yaxis = new NumberAxis("Costs", 0, 100, 10);
            yaxis.autoRangingProperty().set(true);
            lineChartMinMax = new LineChart(xaxis,yaxis);
            lineChartMinMax.autosize();
            tabMinMaxMarian.setContent(lineChartMinMax);
            
            a(filepaths.get(choiceBoxProblems.getSelectionModel().getSelectedIndex()));
            String filename = filepaths.get(choiceBoxProblems.getSelectionModel().getSelectedIndex());

            StopConditionsMarian stopConditions = new StopConditionsMarian(checkboxStopNrGenerations.isSelected(), checkboxStopTime.isSelected(), checkboxStopInfinite.isSelected(), nrOfGenerations, runTime);

            Marian marian = new Marian(filename, populationSize, mutationPercentage, stopConditions, getOptimizedSelectionMarian());
            Population newPop = marian.generatePopulation(populationSize);

            marian.setStartPopulation(newPop);

            lineChartFitness.getData().clear();
            //        lineChartFitnessOptimized.getData().clear();
            lineChartMinMax.getData().clear();
            //lineChartCompare.getData().clear();

            ArrayList<Integer> Algorithms = new ArrayList<>();

            if (this.checkboxMarian.isSelected()) {
                Algorithms.add(Marian.MarianOrignal);
            }
            if (this.checkboxMarianWithOptimization.isSelected()) {
                Algorithms.add(Marian.MarianOptimised);
            }

            TaskManager taskManager = new TaskManager(marian, this, (int) Math.round(sliderCycles.getValue()), Algorithms, lineChartMinMax, lineChartFitness);

            taskManager.valueProperty().addListener(new ChangeListener<TaskManager.TaskUpdate>() {
                @Override
                public void changed(ObservableValue<? extends TaskManager.TaskUpdate> observable, TaskManager.TaskUpdate oldValue, TaskManager.TaskUpdate newValue) {
                    switch (newValue.getUpdateType()) {
                        case TaskManager.TaskUpdate.TaskNotInialised:
                            break;
                        case TaskManager.TaskUpdate.TaskNextCycle:
                           lineChartFitness.getData().clear();
                           lineChartMinMax.getData().clear();
                            break;
                        case Marian.MarianOrignal:
                        case Marian.MarianOptimised:
                        case TaskManager.TaskUpdate.TaskEnd:
                            ObservableList compare = lineChartCompare.getData();
                            compare.addAll(newValue.getList().get(0), newValue.getList().get(1));
                            running = false;
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

            if (Algorithms.size() > 0) {
                running = true;
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

            } else {
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

    }

    /**
     * getOptimizedSelectionMarian gets the values from the optimized selection
     * textboxes and returns them.
     *
     * @return an array with doubles relative to the textboxes from left to
     * right.
     * @author Peter Tielbeek.
     */
    public Double[] getOptimizedSelectionMarian() {
        Double[] collection = new Double[arrayListOptimationParts.size()];

        for (int i = 0; i < arrayListOptimationParts.size(); i++) {
            Double retrievedDouble = Double.parseDouble(arrayListOptimationParts.get(i).getText().replace(",", "."));

            collection[i] = retrievedDouble;
        }

        return collection;
    }

    /**
     * clearCharts clears the charts and stops the algorithm if it's running.
     *
     * @author Gerco Versloot.
     */
    public void clearCharts() {
        stopOperation();
        lineChartFitness.getData().clear();
        lineChartMinMax.getData().clear();
        lineChartCompare.getData().clear();
        a("Clear graphs");
    }

    /**
     * stopOperation stops any threads running.
     *
     * @author Gerco Versloot.
     */
    public void stopOperation() {
        taskManagerThread.interrupt();
        running = false;
        a("Application stopped.");
    }

    /**
     * a will write the specified string to the console
     *
     * @param alert as a string with a message to write to the console
     * @author Peter Tielbeek.
     */
    public void a(String alert) {
        console = alert + "\n" + console;

        textAreaConsole.setText(console);
    }

    /**
     * setLabelCycles will retrieve the value from the sliderCycles and sets it
     * for further use in the algorithm. It will also set the label in the GUI
     * with the selected value.
     *
     * @author Peter Tielbeek.
     */
    public void setLabelCycles() {
        cycles = (int) sliderCycles.getValue();
        sliderCycles.setValue(cycles);

        labelCycles.setText("Nr Of Cycles ( " + cycles + " )");
    }

    /**
     * setLabelPopulationSize will retrieve the value from the
     * sliderPopulationSize and sets it for further use in the algorithm. It
     * will also set the label in the GUI with the selected value.
     *
     * @author Peter Tielbeek.
     */
    public void setLabelPopulationSize() {
        populationSize = (int) sliderPopulationSize.getValue();
        sliderPopulationSize.setValue(populationSize);

        labelPopulationSize.setText("Populationsize ( " + populationSize + " )");
    }

    /**
     * setLabelMutationPercentage will retrieve the value from the
     * sliderMutationPercentage and sets it for further use in the algorithm. It
     * will also set the label in the GUI with the selected value.
     *
     * @author Peter Tielbeek.
     */
    public void setLabelMutationPercentage() {
        mutationPercentage = (double) sliderMutationPercentage.getValue();
        DecimalFormat df = new DecimalFormat("0.00");

        labelMutationPercentage.setText("Mutationpercentage ( " + df.format(mutationPercentage) + "% )");
    }

    /**
     * setLabelStopTime will retrieve the value from the sliderStopTime and sets
     * it for further use in the algorithm. It will also set the label in the
     * GUI with the selected value.
     *
     * @author Peter Tielbeek.
     */
    public void setLabelStopTime() {
        stopTime = (int) sliderStopTime.getValue();
        sliderStopTime.setValue(stopTime);

        checkboxStopTime.setText("TIME ( " + stopTime + "SEC )");
    }

    /**
     * setLabelStopNrGenerations will retrieve the value from the
     * sliderStopNrGenerations and sets it for further use in the algorithm. It
     * will also set the label in the GUI with the selected value.
     *
     * @author Peter Tielbeek.
     */
    public void setLabelStopNrGenerations() {
        stopNrGenerations = (int) sliderStopNrGenerations.getValue();
        sliderStopNrGenerations.setValue(stopNrGenerations);

        checkboxStopNrGenerations.setText("Nr Of Generations ( " + stopNrGenerations + " )");
    }

    /**
     * updateCheckBoxes will update the checkboxes and disables/enables them if
     * a conflicting checkbox is selected.
     *
     * @author Peter Tielbeek.
     */
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

    /**
     * initializeOptimationParts will update the optimation textboxes with a
     * default value.
     *
     * @author Peter Tielbeek.
     */
    public void initializeOptimationParts() {
        setLabelOptimationParts();
        arrayListOptimationParts.get(0).setText("0.7");
        arrayListOptimationParts.get(1).setText("0.2");
        arrayListOptimationParts.get(2).setText("0.1");
    }

    /**
     * setLabelOptimationParts will get the value from the sliderOptimationParts
     * and generates textboxes matching the value. It calculates the default
     * value and adds eventlisteners to the textboxes.
     *
     * @author Peter Tielbeek.
     */
    public void setLabelOptimationParts() {
        gridPaneSettings.getChildren().remove(gridPaneOptimationParts);
        gridPaneOptimationParts = new GridPane();
        gridPaneSettings.add(gridPaneOptimationParts, 1, 22);

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

            tf.setAlignment(Pos.TOP_CENTER);

            //Setting an action for the Clear button
            tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    calculateRemaining();
                }
            });
            //Setting an action for the Clear button
            tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    calculateRemaining();
                }
            });
            //Setting an action for the Clear button
            tf.setOnKeyTyped(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    calculateRemaining();
                }
            });

            arrayListOptimationParts.add(tf);
            gridPaneOptimationParts.addRow(20, arrayListOptimationParts.get(i));
        }

        calculateRemaining();
    }

    /**
     * toggleBlockNr will toggle the blocknumbers in the canvas.
     *
     * @author Peter Tielbeek.
     */
    public void toggleBlockNr() {
        if (toggleBlockNr) {
            toggleBlockNr = false;
        } else {
            toggleBlockNr = true;
        }
    }

    /**
     * toggleFullscreen will toggle the application to fullscreen or windowed
     * mode.
     *
     * @author Peter Tielbeek.
     */
    public void toggleFullscreen() {
        application.toggleFullscreen();
    }

    /**
     * isDouble will check if the specified string is a double
     *
     * @param str a string
     * @return returns true if the specified string is a double
     * @author Peter Tielbeek.
     */
    boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * calculateRemaining will calculate the remaining value from the optimation
     * textboxes (has to be 1 (100%)).
     *
     * @author Peter Tielbeek.
     */
    public void calculateRemaining() {
        double remaining = 0;
        double temp;

        DecimalFormat df = new DecimalFormat("0.00");
        for (TextField arrayListOptimationPart : arrayListOptimationParts) {
            arrayListOptimationPart.getStyleClass().remove("wrong-textfield");
            if (isDouble(arrayListOptimationPart.getText().replace(",", "."))) {
                temp = Double.parseDouble(arrayListOptimationPart.getText().replace(",", "."));
                remaining += temp;
            } else {
                arrayListOptimationPart.getStyleClass().add("wrong-textfield");
            }
        }

        remaining = 1 - remaining;

        labelOptimationRemaining.setText("Remaining ( " + df.format(remaining) + " )");
    }

    /**
     * stopProgram will stop the program from running.
     *
     * @author Peter Tielbeek.
     */
    public void stopProgram() {
        System.exit(0);
    }

    /**
     * setApp will be ran from the class ASP so the controller has acces to the
     * application and it can manipulate it.
     *
     * @param application an ASP application
     * @author Peter Tielbeek.
     */
    public void setApp(ASP application) {
        this.application = application;
    }

    /**
     * setDirectory will be ran from the class ASP so the MainScreenController
     * gets the selected default directory.
     *
     * @param directory a string with a directory.
     * @author Peter Tielbeek.
     */
    public void setDirectory(String directory) {
        this.directory = directory;
        a("Problem directory set to " + directory);
        searchDirectory(directory);
    }
}
