/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aspmetgui;

import aspmetgui.Exceptions.AlgorithmNotSet;
import static aspmetgui.Marian.MarianOptimised;
import static aspmetgui.Marian.MarianOrignal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;

/**
 *
 * @author Gerco
 */
public class TaskManager extends Task<Task> {
    
    private Marian marian;
   
    private LineChart lineChartMinMax;
    
    private LineChart lineChartMinMaxOptimized;
    
    private LineChart lineChartFitness;
    
    private LineChart lineChartFitnessOptimized;
   
    private ArrayList<Integer> runs;
    
    private MainScreenController controller;
     
    public TaskManager(Marian marian, MainScreenController controller, ArrayList<Integer> runs, LineChart lineChartMinMax, LineChart lineChartMinMaxOptimized, LineChart lineChartFitness, LineChart lineChartFitnessOptimized ){
        this.marian = marian;
        this.runs = runs;
        this.lineChartFitness = lineChartFitness;
        this.lineChartFitnessOptimized = lineChartFitnessOptimized;
        this.lineChartMinMax = lineChartMinMax;
        this.lineChartMinMaxOptimized = lineChartMinMaxOptimized;
        this.controller = controller;
        
    }
    
    public Task startMarian(int algorithm){
        Task marianTask = marian.getTask(algorithm);
        
         marianTask.valueProperty().addListener(new ChangeListener< ArrayList<ObservableList<XYChart.Series<String, Double>>> >() {
            @Override
            public void changed(ObservableValue<? extends ArrayList<ObservableList<XYChart.Series<String, Double>>>> observable, ArrayList<ObservableList<XYChart.Series<String, Double>>> oldValue, ArrayList<ObservableList<XYChart.Series<String, Double>>> newValue) {
                switch(algorithm){
                    case Marian.MarianOrignal:
                        lineChartFitness.setData(newValue.get(0));
                        lineChartMinMax.setData(newValue.get(1));
                        break;
                    case Marian.MarianOptimised:
                        ObservableList fitness = lineChartFitness.getData();
                        ObservableList minMax = lineChartMinMax.getData();
                        fitness.addAll(newValue.get(0).get(0), newValue.get(0).get(1));
                        minMax.addAll(newValue.get(1).get(0), newValue.get(1).get(1));

                        
                        //lineChartFitness.getData().addAll(newValue.get(0).get(0), newValue.get(0).get(1));
                        
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
        myThread.start();
        Marian.Process marianProcess = marian.getProcess();
        while(myThread.isAlive()){
            if(marianProcess != null){
                updateProgress(marianProcess.getProcess(), marianProcess.getTotalWork());
            }
            if (Thread.currentThread().isInterrupted()) {
                myThread.interrupt();
                return  marianTask;
            }
        }
        
        return marianTask;
    }

    @Override
    protected Task call() throws AlgorithmNotSet  {
        Task currentTask = new Task() {

            @Override
            protected Object call() throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        System.out.println("Start run Task manager");
        for (Integer Algorithm : runs) {   
            switch(Algorithm){
                case MarianOrignal:
                    currentTask = startMarian(Marian.MarianOrignal);
                    break;
                case MarianOptimised:
                    currentTask = startMarian(Marian.MarianOptimised);
                    break;
                default:
                    throw new AlgorithmNotSet();
            }
        }         
        return currentTask;
    }
    
}
