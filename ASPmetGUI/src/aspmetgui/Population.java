/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aspmetgui;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Gerco
 *
 */
public class Population {

    private ArrayList<Chromosome> list = new ArrayList<>();
    private Chromosome Max = new Chromosome(0);
    private Chromosome Min = new Chromosome(0);;
    private double TotalFitness;

    /**
     * Adds a Chromosome to the population list and checks if new Max or Min
     * @author Gerco Versloot
     * @param chr The Chromosome to add at the list
     */
    public void addChromosome(Chromosome chr) {
        if (chr.getCosts() > getMax().getCosts()) {
            setMax(chr);
        }
        if (chr.getCosts() < getMin().getCosts() || getMin().getCosts() == 0) {
            setMin(chr);
        }
        getList().add(chr);
    }
    
    public double getAverageFittness(){
        return TotalFitness/(double)list.size();
    } 

    /**
     * Finds the Minimal and Maximal size of all Chromosomes in the current population list
     * @author Gerco Versloot
     */
    private void calculateMaxMin() {
        for (Chromosome chr : list) {
            if (chr.getCosts() > getMax().getCosts()) {
                setMax(chr);
            }
            if (chr.getCosts() < getMin().getCosts() || getMin().getCosts() == 0 ) {
                setMin(chr);
            }
        }
    }
    
    public void calculateFitness(){
        double fitness;
        this.TotalFitness = 0;
        for (Chromosome chr : list) {
            fitness =  ( (double)getMin().getCosts() / (double)chr.getCosts());
            TotalFitness += fitness;
            chr.setFitness(fitness);
        }
    }
    
    /**
     * Shorts the current list based on fitness, 
     * fist in the list is the best fitness, 
     * the last in the list is the worst fitness
     */
    public void sortByFitness(){
        Collections.sort(this.list);
    }

    /**
     * @return a string of all Chromosomes in the population list
     * with an  new line as Saperator
     * @author Gerco Versloot
     */
    public String toString() {
        return toStringSaperator("\n");
    }

    /**
     * Chromosome id| bloks ids, Chromosome size
     * @param sap devider of between Chromosomes id 
     * @return a string of all Chromosomes in the population list
     * @author Gerco Versloot
     */
    public String toStringSaperator(String sap) {
        String returnString = new String();
        calculateFitness();
        int counter = 0;
        for (Chromosome Chromo : getList()) {
            returnString += counter++ + " [ " + Chromo.ToString()  + " ]" + sap;
        }
        return returnString;
    }

    /**
     * @return the population list
     */
    public ArrayList<Chromosome> getList() {
        calculateFitness();
        return list;
    }

    /**
     * @param list set the population list
     * Also automatically finds the new Max and Min of the provided list
     * @author Gerco Versloot
     */
    public void setList(ArrayList<Chromosome> list) {
        this.list = list;
        calculateMaxMin();
    }

    /**
     * @param selection the position of the chromosome in the array as an integer
     * @return The selected chromosome
     * @author Peter Tielbeek
     */
    public Chromosome getChromosome(Integer selection) {
        return list.get(selection);
    }
    /**
     * @param index is the position of the chromosome to be replaced
     * @param chromosome is the new chromosome to be put in the list at the index
     * @author Peter Tielbeek
     */
    public void setChromosome(int index, Chromosome chromosome){
        this.list.set(index, chromosome);
    }

    /**
     * @return the Maximal fitness of this population 
     * @author Gerco Versloot
     */
    public Chromosome getMax() {
        return Max;
    }

    /**
     * @param Max the Max to set
     * @author Gerco Versloot
     */
    private void setMax(Chromosome Max) {
        this.Max = Max;
    }

    /**
     * @return the Minimal fitness of this population 
     * @author Gerco Versloot
     */
    public Chromosome getMin() {
        return Min;
    }

    /**
     * @param Min the Min to set
     * @author Gerco Versloot
     */
    private void setMin(Chromosome Min) {
        this.Min = Min;
    }

    /**
        * @author Peter Tielbeek
        * @return An integer with the size of the population
     */
    public Integer getSize() {
        return list.size();
    }
    
    /**
     * @return the TotoalFitnees fitness of this population 
     * @author Gerco Versloot
     */
    public double getTotalFitness() {
        return TotalFitness;
    }
}
