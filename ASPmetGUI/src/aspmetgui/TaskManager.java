/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aspmetgui;

import aspmetgui.Exceptions.AlgorithmNotSet;
import static aspmetgui.Marian.MarianOptimised;
import static aspmetgui.Marian.MarianOrignal;
import aspmetgui.TaskManager.TaskUpdate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ProgressBar;

/**
 * 
 * @author Gerco Versloot
 * Acts like a worker to start/stop multiple Marian tasks cycles. 
 * This class is a task so it runs in a different thread.
 * The Marian tasks are started in different threads. 
 */
public class TaskManager extends Task<TaskUpdate> {

    private Marian marian;

    private LineChart lineChartMinMax;

    private LineChart lineChartFitness;
    private XYChart.Series<String, Integer> runOriginal;

    private ArrayList<Integer> Algorithms;

    private MainScreenController controller;

    private int cycles;

    /**
     * Constructor to setup cycles of Marian tasks
     * @param marian The marian problem
     * @param controller The mainscreen controller where the user can interact with (GUI)
     * @param cycles Amount of cycles, a cycle is one run of Marian problem with multiple algorithms 
     * @param Algorithms The algorithms types that solves the Marian problem,  the index is the priority of the algorithm (Lower is more important)  
     * @param lineChartMinMax The linechart the draw the Min and Max chromosoom values
     * @param lineChartFitness  The linechart the draw the chromosoom fitness values
     */
    public TaskManager(Marian marian, MainScreenController controller, int cycles, ArrayList<Integer> Algorithms, LineChart lineChartMinMax, LineChart lineChartFitness ) {
        this.marian = marian;
        this.Algorithms = Algorithms;
        this.lineChartFitness = lineChartFitness;
        this.lineChartMinMax = lineChartMinMax;
        this.controller = controller;
        this.cycles = cycles;

    }
    
    /**
     * Start a Marian task (Solve a Marian problem)
     * @param algorithm Select the algorithm which solves the problem
     * @return the thread which was running the Marian task
     */
    public Thread startMarian(int algorithm) {
        
        Task marianTask = marian.getTask(algorithm);
        
        // set output of the Marian task
        marianTask.valueProperty().addListener(new ChangeListener< ArrayList<ObservableList<XYChart.Series<String, Double>>>>() {
            @Override
            public void changed(ObservableValue<? extends ArrayList<ObservableList<XYChart.Series<String, Double>>>> observable, ArrayList<ObservableList<XYChart.Series<String, Double>>> oldValue, ArrayList<ObservableList<XYChart.Series<String, Double>>> newValue) {
                // Check what kind of output the Marian task provides
                switch (algorithm) {
                    case Marian.MarianOrignal:
                        lineChartFitness.setData(newValue.get(0));
                        lineChartMinMax.setData(newValue.get(1));
                        
                        break;
                    case Marian.MarianOptimised:
                        ObservableList fitness = lineChartFitness.getData();
                        ObservableList minMax = lineChartMinMax.getData();
                        fitness.addAll(newValue.get(0).get(0), newValue.get(0).get(1));
                        minMax.addAll(newValue.get(1).get(0), newValue.get(1).get(1));

                        lineChartFitness.setData(fitness);
                        lineChartMinMax.setData(minMax);
                        break;
                    default:
                        try {
                            throw new AlgorithmNotSet();
                        } catch (AlgorithmNotSet ex) {
                            Logger.getLogger(TaskManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }

            }
        });

        // Set the messenger from the Marian task
        marianTask.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                controller.a(newValue);
            }
        });
        
        // Insialize new thread with the marian task
        Thread myThread = new Thread(marianTask);
        // Stop thread when this thread stops
        myThread.setDaemon(true);
        // Set the name of the thread
        myThread.setName("Marian calculaton");
        // Start calcuating 
        myThread.start();
        
        // Get the current process of the running Marian task
        Marian.Process marianProcess = marian.getProcess();
        // While the thread is running (still calcuationg), so wait till the cacluations are finished
        while (myThread.isAlive()) {
            if (marianProcess != null) {
                // update the progres to the GUI
                updateProgress(marianProcess.getProcess(), marianProcess.getTotalWork());
            }
            
            // If user stops the calcuation
            if (Thread.currentThread().isInterrupted()) {
                // stop the calcuation
                myThread.interrupt();
                return myThread;
            }
        }
        return myThread;
    }

    /**
     * Start the cycles of solving Marian problems
     * @return Update(s) of the result of all the cycles and the status of this task
     * @throws InterruptedException
     */
    @Override
    protected TaskUpdate call() throws InterruptedException {
        
        // Obserable list and series for the compare lineChart
        ObservableList<XYChart.Series<String, Integer>> obListCompare = FXCollections.observableArrayList(new ArrayList());
        XYChart.Series<String, Integer> runOriginal = new XYChart.Series<String, Integer>();
        XYChart.Series<String, Integer> runOptimised = new XYChart.Series<String, Integer>();
        
        DecimalFormat dfFitness = new DecimalFormat("0000");
        
        // set names for the series
        runOriginal.setName("Marian original: " + marian.getPopulationSize() + ", " + String.format("%.2f", marian.getMutationPercentage()) );
        runOptimised.setName("Marian optimised: " + marian.getPopulationSize() + ", " + String.format("%.2f", marian.getMutationPercentage())); 
        obListCompare.addAll(runOriginal, runOptimised);
        
        Thread currentTask;
        TaskUpdate update = new TaskUpdate(TaskUpdate.TaskNotInialised);
        // Run amount of cycels
        for (int i = 1; i <= cycles; i++) {
          // foread cycle run amount of algorithms to solve the problem
          for (Integer Algorithm : Algorithms) {
                switch (Algorithm) {
                    case Marian.MarianOrignal:
                        updateMessage("Cycle " + i + " Marian original");
                        currentTask = startMarian(Marian.MarianOrignal); 
                        currentTask.join(); // Wait till the Marian task is finished
                        
                        // add the results to the comare observable list
                        XYChart.Data<String, Integer> nodeCompareOrignal = new XYChart.Data(Integer.toString(i),  marian.getAlgorithmEvaluation());
                        nodeCompareOrignal.setNode(new HoverNode(i, dfFitness.format(marian.getAlgorithmEvaluation())));
                        runOriginal.getData().add(nodeCompareOrignal);
                        update.setType(Marian.MarianOrignal);
                        break;
                    case Marian.MarianOptimised:
                        updateMessage("Cycle " + i + " Marian optimised");
                        currentTask = startMarian(Marian.MarianOptimised);
                        currentTask.join();// Wait till the Marian task is finished
                        
                        // add the results to the comare observable list
                        XYChart.Data<String, Integer> nodeCompareOptimised = new XYChart.Data(Integer.toString(i),  marian.getAlgorithmEvaluation());
                        nodeCompareOptimised.setNode(new HoverNode(i, dfFitness.format(marian.getAlgorithmEvaluation())));
                        runOptimised.getData().add(nodeCompareOptimised);
                        update.setType(Marian.MarianOptimised);
                        
                        break;
                    default:
                        try {
                            throw new AlgorithmNotSet();
                        } catch (AlgorithmNotSet ex) {
                            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }   
            }
            // check if the user stoped the current calcuations 
            if (Thread.currentThread().isInterrupted()) {
                update.setList(obListCompare);
                updateProgress(1, 1);
                return new TaskUpdate(TaskUpdate.TaskEnd, obListCompare);
            }
            // set new first population for the next cycle
            marian.setUsePopulation(marian.generatePopulation(marian.getPopulationSize()));
        }
        update.setList(obListCompare);
        return new TaskUpdate(TaskUpdate.TaskEnd, obListCompare);
    }
    
    /**
     * TaskUpdate is used to provided update about the running TaskManager task, 
     * If available, this class also provides the compare algorithms observable list 
     */
    class TaskUpdate{
        
        private int updateType;
        private ObservableList<XYChart.Series<String, Integer>> list;

        
        public final static int TaskNextCycle = 2;
        public final static int TaskEnd = 3;
        public final static int TaskNotInialised = 100;

        /**
         * Constructor to provide a update message about the running TaskManager
         * @param type Current update type
         * @param list The observable list to compare the algorithms 
         */
        public TaskUpdate(int type, ObservableList<XYChart.Series<String, Integer>> list ){
            this.updateType = type;
            this.list = list;
        }
        
        public ObservableList<Series<String, Integer>> getList() {
            return list;
        }
        
        public void setList(ObservableList<Series<String, Integer>> list) {
            this.list = list;
        }
        
        public void setType(int type){
            this.updateType = type;
        }
        
        public TaskUpdate(int type){
            this.updateType = type;
        }
        
        public int getUpdateType() {
            return updateType;
        }
        
    }

}
