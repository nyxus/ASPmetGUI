/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aspmetgui;

/**
 *
 * @author Gerco
 * The stop conditions/watch for a running Marian task.
 * Use the Start() method before starting the running the algorithm in a Marian task
 * Use isStop() to check if the task must stop
 */
public class StopConditionsMarian {;
    
    private int nrOfGenerations;
    private long runTime;
    private long stopTime;
    private long startTime;
    
    private boolean enableStopGenerations = false;
    private boolean enableStopTime = false;
    private boolean enableInfinite = false;
    
    /**
     * Constructor to set the stop conditions of a running Marian task to enable and set the stop conditions by: max generations, running time or Infinit 
     * @param enableStopGenerations Enable stopping by amount of generations 
     * @param enableStopTime Enable stopping after an amount of time
     * @param enableInfinite Enable run for infinity 
     * @param nrOfGenerations Set number of generations before stopping
     * @param runTime Set amount of time past before stopping 
     */
    public StopConditionsMarian(boolean enableStopGenerations, boolean enableStopTime, boolean enableInfinite, int nrOfGenerations, long runTime){
        this.enableStopGenerations = enableStopGenerations;
        this.enableStopTime = enableStopTime;
        this.enableInfinite = enableInfinite;
        
        this.nrOfGenerations = nrOfGenerations;
        this.runTime = runTime;
    }
    
    /**
     * Constructor to set the stop conditions of a running Marian task to set the stop condition by max of generations  
     * @param nrOfGenerations the number of max generations 
     */
    public StopConditionsMarian(int nrOfGenerations){
        this.enableStopGenerations = true;
        this.nrOfGenerations = nrOfGenerations;
        
    }
    
    /**
     * Constructor to set the stop conditions of a running Marian task to set the stop condition by a max of running time 
     * @param runTime 
     */
    public StopConditionsMarian(long runTime){
        this.enableStopTime = true;
        this.runTime = runTime;
    }
    
    /**
     * Constructor to set the stop conditions of a running Marian task to set the stop condition by max of generations and max running time
     * @param nrOfGenerations
     * @param runTime 
     */
    public StopConditionsMarian(int nrOfGenerations, long runTime){
        this.enableStopGenerations = true;
        this.nrOfGenerations = nrOfGenerations;
        this.enableStopTime = true;
        this.runTime = runTime;
    }
    
    /**
     * Constructor the run the Marian task forever
     */
    public StopConditionsMarian(){
        enableInfinite = true;
    }
    
    /**
     * Setup/enable stop conditions for a new running Marian task. Run this method alway before starting the Marian task. 
     * Used to set/reset all the stop conditions.
     */
    public void Start(){
        if(enableStopTime)
            // calcuate the stop time
            stopTime =  runTime + System.currentTimeMillis();
        // set start time
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Checks if the Marian task has to stop
     * @param currentGeneration the current generation of the algorithm
     * @return true: task has to stop, false: task can continue  
     */
    public boolean isStop(int currentGeneration){
        //check for stopping by time
        if(enableStopTime && stopTime < System.currentTimeMillis()){
            return true; 
        }
        //check stopping by generations
        if(enableStopGenerations && currentGeneration >= nrOfGenerations){
            return true; 
        }
        // Continue running
        return false;
    }
    
    public long getRunTime() {
        return runTime;
    }
   
    public boolean isEnableStopGenerations() {
        return enableStopGenerations;
    }

    public boolean isEnableStopTime() {
        return enableStopTime;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public int getNrOfGenerations() {
        return nrOfGenerations;
    }

    
}
