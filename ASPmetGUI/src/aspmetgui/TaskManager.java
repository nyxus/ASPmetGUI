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
 * @author Gerco
 */
public class TaskManager extends Task<TaskUpdate> {

    private Marian marian;

    private LineChart lineChartMinMax;

    private LineChart lineChartFitness;
    private LineChart lineChartCompare ;
    private XYChart.Series<String, Integer> runOriginal;

    private ArrayList<Integer> Algorithms;

    private MainScreenController controller;

    private int cycles;

    public TaskManager(Marian marian, MainScreenController controller, int cycles, ArrayList<Integer> Algorithms, LineChart lineChartMinMax, LineChart lineChartFitness, LineChart lineChartCompare ) {
        this.marian = marian;
        this.Algorithms = Algorithms;
        this.lineChartFitness = lineChartFitness;
        this.lineChartMinMax = lineChartMinMax;
       // this.runOriginal = runOriginal;
        this.lineChartCompare = lineChartCompare;
        this.controller = controller;

        this.cycles = cycles;
        
       

    }
    
    public Thread startMarian(int algorithm) {
        Task marianTask = marian.getTask(algorithm);
        marianTask.valueProperty().addListener(new ChangeListener< ArrayList<ObservableList<XYChart.Series<String, Double>>>>() {
            @Override
            public void changed(ObservableValue<? extends ArrayList<ObservableList<XYChart.Series<String, Double>>>> observable, ArrayList<ObservableList<XYChart.Series<String, Double>>> oldValue, ArrayList<ObservableList<XYChart.Series<String, Double>>> newValue) {
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

        marianTask.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                controller.a(newValue);
            }
        });

        Thread myThread = new Thread(marianTask);
        myThread.setDaemon(true);
        myThread.setName("Marian calculaton");
        myThread.start();
        Marian.Process marianProcess = marian.getProcess();
        System.out.println("            in task for wait: " + algorithm);
        while (myThread.isAlive()) {
            if (marianProcess != null) {
                updateProgress(marianProcess.getProcess(), marianProcess.getTotalWork());
            }
            if (Thread.currentThread().isInterrupted()) {
                myThread.interrupt();
                return myThread;
            }
        }
        return myThread;
    }

    @Override
    protected TaskUpdate call() throws InterruptedException {
        ObservableList<XYChart.Series<String, Integer>> obListCompare = FXCollections.observableArrayList(new ArrayList());
        
        XYChart.Series<String, Integer> runOriginal = new XYChart.Series<String, Integer>();
        XYChart.Series<String, Integer> runOptimised = new XYChart.Series<String, Integer>();
        
        DecimalFormat dfFitness = new DecimalFormat("0000");
        
        runOriginal.setName("Marian original: " + marian.getPopulationSize() + ", " + String.format("%.2f", marian.getMutationPercentage()) );
        runOptimised.setName("Marian optimised: " + marian.getPopulationSize() + ", " + String.format("%.2f", marian.getMutationPercentage())); 
        obListCompare.addAll(runOriginal, runOptimised);
        
        Thread currentTask;
        System.out.println("Get Chart data");
  
        System.out.println("Start run Task manager, cycles: " + cycles + " alg count: " + Arrays.toString(Algorithms.toArray()));
                TaskUpdate update = new TaskUpdate(TaskUpdate.TaskNotInialised);
        for (int i = 0; i < cycles; i++) {
            System.out.println("    Start cycle: " + i);
            for (Integer Algorithm : Algorithms) {
                System.out.println("        alg voor run: " + Algorithm);
                switch (Algorithm) {
                    case Marian.MarianOrignal:
                        currentTask = startMarian(Marian.MarianOrignal); 
                        currentTask.join();
                        XYChart.Data<String, Integer> nodeCompareOrignal = new XYChart.Data(Integer.toString(i),  marian.getAlgorithmEvaluation());
                        nodeCompareOrignal.setNode(new HoverNode(i, dfFitness.format(marian.getAlgorithmEvaluation())));
                        runOriginal.getData().add(nodeCompareOrignal);
                        update.setType(Marian.MarianOrignal);
                        break;
                    case Marian.MarianOptimised:
                        currentTask = startMarian(Marian.MarianOptimised);
                        currentTask.join();
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
            if (Thread.currentThread().isInterrupted()) {
                update.setList(obListCompare);
                return new TaskUpdate(TaskUpdate.TaskEnd, obListCompare);
            }
            marian.setUsePopulation(marian.generatePopulationBetter(marian.getPopulationSize()));
        System.out.println("--------------------------------------------------------------------");
        }
        update.setList(obListCompare);
        return new TaskUpdate(TaskUpdate.TaskEnd, obListCompare);
    }
    
    class TaskUpdate{
        
        private int updateType;
        private ObservableList<XYChart.Series<String, Integer>> list;

        
        public final static int TaskNextCycle = 2;
        public final static int TaskEnd = 3;
        public final static int TaskNotInialised = 100;

        public int getUpdateType() {
            return updateType;
        }
        
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
        
    }

}
