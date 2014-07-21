/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aspmetgui;

/**
 *
 * @author Gerco
 */
public class StopConditionsMarian {
    
    private int nrOfGenerations;

    
    private long runTime;
    private long stopTime;
    private long startTime;
    
    private boolean enableStopGenerations = false;
    private boolean enableStopTime = false;
    private boolean enableInfinite = false;

    public StopConditionsMarian(boolean enableStopGenerations, boolean enableStopTime, boolean enableInfinite, int nrOfGenerations, long runTime){
        this.enableStopGenerations = enableStopGenerations;
        this.enableStopTime = enableStopTime;
        this.enableInfinite = enableInfinite;
        
        this.nrOfGenerations = nrOfGenerations;
        this.runTime = runTime;
    }
    
    public StopConditionsMarian(int nrOfGenerations){
        this.enableStopGenerations = true;
        this.nrOfGenerations = nrOfGenerations;
        
    }
    
    public StopConditionsMarian(long runTime){
        this.enableStopTime = true;
        this.runTime = runTime;
    }
    
    public StopConditionsMarian(int nrOfGenerations, long runTime){
        this.enableStopGenerations = true;
        this.nrOfGenerations = nrOfGenerations;
        this.enableStopTime = true;
        this.runTime = runTime;
    }
    
    public StopConditionsMarian(){
        enableInfinite = true;
    }
    
    public void Start(){
        if(enableStopTime)
            stopTime =  runTime + System.currentTimeMillis();
        startTime = System.currentTimeMillis();
    }
    
    public boolean isStop(int currentGeneration){
        if(enableStopTime && stopTime < System.currentTimeMillis()){
            System.out.println("STOP NOW");
            return true; 
        }
        if(enableStopGenerations && currentGeneration >= nrOfGenerations){
            System.out.println("STOP NOW");
            return true; 
        }
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
