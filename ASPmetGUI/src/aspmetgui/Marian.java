package aspmetgui;

import aspmetgui.Exceptions.AlgorithmNotSet;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;

/**
 * 
 * @author Gerco & Peter
 * The Marian contains a problem. This problem is solvable by multiple algorithms.
 * The method getTask() returns a task that can solve the problem in a thread. 
 */
public class Marian {

    private Block floor = new Block(0, 1, 1, 0, 0);
   
    //Population is een ArrayList van Chromosomen. 
    //BlockCollection bevat alle blocks van een probleem.
    private ArrayList<Block> blockCollection = new ArrayList<>();

    //DependencyMatrix is een array van booleans welke beschrijft welke blokken benodigd zijn om een desbetreffende block te plaatsen.
    private Boolean[][] dependencyMatrix;

    //FysicalMatrix is een array met daarin de blokken hoe deze daadwerkelijk opgestapeld zijn.
    private Block[][] fysicalMatrix;

    private Chromosome firstMin;

    private int problemSize;

   
    private double mutationPercentage = 2.25;

    
    private int populationSize;

    
    private Double[] OptimilisationRatios;

    private long maxRunTime;

    
    public final static int MarianOptimised = 0; 
    public final static int MarianOrignal = 1; 
    private StopConditionsMarian stopCondition;
    
    private Population usePopulation = null;
    
    private Process process;
   
    private Integer AlgorithmEvaluation;

    
   

    /**
     * Create a problem to solve with the algorithm of Marian
     *
     * @param filename the file location that contains the blocks information,
     * id minX maxX minY maxY
     * @param populationSize the size of a population
     */
    public Marian(String filename, int populationSize, double mutation, int setAlgoritm, StopConditionsMarian stopCondition, Double[] OptimilisationRatios) {
        problemSize = getProblemSize(filename);
        this.populationSize = populationSize;
        this.mutationPercentage = mutation;

        this.stopCondition = stopCondition;
        this.OptimilisationRatios = OptimilisationRatios;

        floor = new Block(0, 0, problemSize, 0, 0);
        floor.setBuildNumber(0);
        this.fysicalMatrix = new Block[problemSize + 1][problemSize];

        // set floor into fyclicalMatrx at index
        for (int i = 0; i < problemSize; i++) {
            fysicalMatrix[0][i] = floor;
        }

        blockCollection.add(floor);

        ReadProblem(filename);

        convertToDependencyMatrix();
        // System.out.println("- Fysical Matrix -\n"+ToStringFysicalMatrix());
        //System.out.println("- Block Collection -\n"+ToStringBlockCollection());
        // System.out.println("- Dependency Matrix -\n"+ToStringDependencyMatrix());

    }

    /**
     * getProblemSize leest het bestand in het opgegeven pad en berekend hieruit de groote van het probleem.
     * @param filename locatie van het uit te lezen bestand.
     * @return problemSize als een Integer
     * @author Peter Tielbeek.
     */
    public int getProblemSize(String filename) {
        int problemSize = 0;
        int compare = 0;
        String splitarray[];

        try {
            //Open bestand op de opgegeven locatie
            FileInputStream fstream = new FileInputStream(filename);

            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //Lees bestand per regel
            while ((strLine = br.readLine()) != null) {
                splitarray = strLine.split(" ");

                compare = Integer.parseInt(splitarray[2]);

                if (compare > problemSize) {
                    problemSize = compare;
                }
            }

            //Close the input stream
            in.close();
            return problemSize + 1;
        } catch (Exception e) {
            //Catch exception if any
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Reads a problem file and converts a block object, adds the block to the
     * blockColletion and converts to the fysical matrix, . The fysical matrix
     * is a visual representation of the file
     *
     * @param file the file location that contains the problem
     * @author Gerco Versloot
     */
    public void ReadProblem(String file) {
        try {
            //Opens the file from the file location
            FileInputStream fstream = new FileInputStream(file);

            // Read the data from the file into a BufferedReader
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            Block tempBlock;

            // Loop through each line untill there a no more lines
            while ((strLine = br.readLine()) != null) {

                // converts line to a block
                tempBlock = StringToBlock(strLine);

                // add block to the blockCollection
                blockCollection.add(tempBlock.getID(), tempBlock);

                // add Block to Fysical matrix
                BlockIntoFysicalMatrix(tempBlock);
            }

            //Close the input stream
            in.close();

        } catch (Exception e) {
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Converts a string (a line of the problem file) to a block object, only
     * numbers in the string are allowed
     *
     * @param inputBlock string format: id minX maxX minY maxY, only numbers
     * @return a block object
     * @author Gerco Versloot
     */
    public Block StringToBlock(String inputBlock) {
        String[] tempStringBlock;
        Integer[] tempIntBlock;

        // Split the string to a string array, split is done at a white space
        tempStringBlock = inputBlock.split(" ");

        // Convert the string array to a interger array, only possible if the strings are numbers
        tempIntBlock = StringToIntArray(tempStringBlock);

        // Create a new block object wiht the numbers from the interger array
        Block returnBlock = new Block(tempIntBlock[0], tempIntBlock[1], tempIntBlock[2], tempIntBlock[3], tempIntBlock[4]);

        return returnBlock;
    }

    /**
     * Converts a string array with numbers to a intergers array with the same
     * numbers
     *
     * @param stringArray the array with numbers to convert to intergers
     * @return int array with only intergers
     * @author Gerco Versloot
     */
    static Integer[] StringToIntArray(String[] stringArray) {
        Integer[] intArray = new Integer[stringArray.length];

        //Loop throug all string array the elements 
        for (int i = 0; i < stringArray.length; i++) {
            try {
                // Try to convert single string number to a interger
                intArray[i] = Integer.parseInt(stringArray[i]);
            } catch (NumberFormatException nfe) {
            };
        }

        return intArray;
    }

    /**
     * Add a block to its position in the FysiacalMatrix 
     * @param block A block from the problem
     * @author Gerco Versloot
     */
    private void BlockIntoFysicalMatrix(Block block) {        
        // add blocks
        for (int y = block.getMinY(); y <= block.getMaxY(); y++) {
            for (int x = block.getMinX(); x <= block.getMaxX(); x++) {
                this.fysicalMatrix[y + 1][x] = block;
            }
        }
    }

    //ToStringFysicalMatrix
    //  Beschrijving: ToStringFysicalMatrix zet de volledige FysicalMatrix om in een string.
    //  Input: -
    //  Output: De volledige FysicalMatrix in een String.
    //  Gemaakt door: Gerco Versloot
    /**
     * Converts the FycicalMatrix to a visual representation as a String  
     * @return The visual representation of the FycicalMatrix
     */
    public String ToStringFysicalMatrix() {
        String returnString = new String();
        int counter = 0;
        for (Block[] blockArray : this.fysicalMatrix) {
            returnString += "Row " + counter + ": ";
            for (Block block : blockArray) {
                returnString += block.getID() + " ";
            }
            returnString += "\n";
            counter++;
        }
        return returnString;
    }

    /**
     *  Creates and fill the DependencyMatrix with a default value
     * @param size The x and y sizes of the DependencyMatrix
     * @param default_val The default value of all the elements 
     */
    private void initializeDependencyMatrix(int size, boolean default_val) {
        dependencyMatrix = new Boolean[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                dependencyMatrix[i][j] = default_val;
            }

        }
    }

    /**
     *  Converts the fysicalMatrix to the DependencyMatrix
     */
    private void convertToDependencyMatrix() {
        Block currentBlock, prevBlock, currentDepencency, prevDepencency;
        prevDepencency = null;
        prevBlock = fysicalMatrix[0][0]; // set first previous block as the current first block 

        initializeDependencyMatrix(blockCollection.size(), false);

        prevDepencency = blockCollection.get(0); // get floor
        for (int y = 1; y < fysicalMatrix.length; y++) {
            for (int x = 0; x < fysicalMatrix[y].length; x++) {
                currentBlock = fysicalMatrix[y][x];
                currentDepencency = fysicalMatrix[y - 1][x];

                // check if it is not the same block
                if (prevBlock != currentBlock) {
                    prevDepencency = null;
                    if (prevBlock != null) {
                        prevBlock.setSibling(Block.RIGHT, currentBlock);
                        currentBlock.setSibling(Block.LEFT, prevBlock);
                    }
                }

                if (prevDepencency != currentDepencency && currentDepencency != currentBlock) {

                    dependencyMatrix[currentBlock.getID()][currentDepencency.getID()] = true; // set depencency of the current block
                    currentBlock.AddParent(currentDepencency);
                    currentDepencency.addChild(currentBlock);                     
                }

                prevBlock = currentBlock;
                prevDepencency = currentDepencency;
            }
            prevBlock = null;
        }
    }

    /**
     * A representation of the block collection
     * @return A string with the content of the block collection
     */
    public String ToStringBlockCollection() {
        String returnString = new String();
        for (Block block : blockCollection) {
            returnString += block.ToString(", ") + "\n";
        }
        return returnString;
    }

    /**
     * Converts the DependencyMatrix into a readable string
     * @return A string with the content of the DependencyMatrix
     */
    public String ToStringDependencyMatrix() {
        String returnString = new String();
        returnString = "   /";
        for (int i = 0; i < dependencyMatrix[0].length; i++) {
            returnString += i + " ";
        }
        returnString += "\n";
        for (int y = 0; y < dependencyMatrix.length; y++) {
            if (y < 10) {
                returnString += " " + y + "| ";
            } else {
                returnString += y + "| ";
            }
            for (int x = 0; x < dependencyMatrix[y].length; x++) {
                if (dependencyMatrix[y][x]) {
                    returnString += "1 ";

                } else {
                    returnString += "0 ";
                }
            }
            returnString += "\n";
        }

        return returnString;

    }

     /**
     * Creates a population based on the current Marian problem
     *
     * @param populationSize the size of the new population
     * @return the new population
     * @author Gerco Versloot
     */
    public Population generatePopulation(int populationSize) {
        Population pop = new Population();
        for (int i = 0; i < populationSize; i++) {
            pop.addChromosome(guidedSearch(i));
        }
        this.firstMin = pop.getMin();
        return pop;
    }
    
    /**
     * Create a Chromosome based on the based on the Marian approach 
     * @param newId The id of the new Chromosome
     * @return the Chromosome
     */
    public Chromosome guidedSearch(int newId) {
        Chromosome newChrom = new Chromosome(newId);                // the new Chomosome to return filled with blocks
        ArrayList<Block> possibleStack = new ArrayList<>();         // List of all blocks that are placable  
        possibleStack.add(floor);                                   // add floor to start of stack    
        Block currentBlock;
        
        // check if the buildNumber is not overflowing the Integer databtype
        int buildCounter = this.generateBuildNumber();
        
        while (possibleStack.size() > 0) {
            Random random = new Random();
            currentBlock = possibleStack.get(random.nextInt(possibleStack.size())); // select a random block
            possibleStack.remove(currentBlock);                                     // remove block from possible list
            newChrom.AddBlockToSequence(currentBlock);                              // add block to the Chomosome 
            currentBlock.setBuildNumber(buildCounter);
            
            possibleStack.addAll(getPossibleNextBlocks(currentBlock));   // search for new possibilities and add to possible list
            
        }
        return newChrom;
    }
    
    /**
     * Finds all the placable child of the block
     * @param currentBlock the block which childs has to be checked
     * @return arraylist of childs that are placable
     */
    private ArrayList<Block> getPossibleNextBlocks(Block currentBlock) {
        ArrayList<Block> possibleBlocks = new ArrayList<>();
        
        // loop though all the childeren 
        childs:
        for (Block child : currentBlock.getChilds()) {
            Boolean addChild = true;
            //loop trough all the parrents to check if there are not placed
            for (Block childParrend : child.getParents()) {
                //Check if all the parrents of the child are placed
                if(childParrend.getBuildNumber() != currentBlock.getBuildNumber()){
                    // parrent is not placed so dont add the child
                    addChild = false;
                    continue childs;
                }
            }
            if(addChild){
                possibleBlocks.add(child);
            }
        }
        return possibleBlocks;
    }
    
    public Population crossOver(Population population) {
        ArrayList<Chromosome> temppopulation = new ArrayList<>(population.getList());
        ArrayList<Chromosome> parents = new ArrayList<>();
        Population childPopulation = new Population();
        
        ArrayList<Block> parent1LeftLocus = new ArrayList<>();
        ArrayList<Block> parent2LeftLocus = new ArrayList<>();
        ArrayList<Block> parent1RightLocus = new ArrayList<>();
        ArrayList<Block> parent2RightLocus = new ArrayList<>();
        ArrayList<Block> possibleBlocks;
        
        Random random = new Random();
        
        int selection;
        int cutpoint;
        int min;
        int max;

        //input
        //A population of faesible parent chromosomes from the current generation and the model of the product as shown in the previous section.
        //C1
        //Randomly select pairs of parent chromosomes
        for (int i = 0; i < population.getSize(); i++) {
            selection = random.nextInt(temppopulation.size());
            parents.add(temppopulation.get(selection));
            temppopulation.remove(selection);
        }
        if(parents.size() % 2 != 0){
            parents.add(guidedSearch(parents.size()+1));
        }

        
        for(int i = 0; i < parents.size(); i = i + 2){
            Chromosome frstParrent = parents.get(i);
            Chromosome scndParrent = parents.get(i+1);
            min = 2;
            max = getBlockCollection().size() - 1;
            
            cutpoint = random.nextInt(max - min) + min;
            
            //Fill left locusses
            parent1LeftLocus.addAll(frstParrent.GetSelection(0, cutpoint));
            parent2LeftLocus.addAll(scndParrent.GetSelection(0, cutpoint));

            
            parent1RightLocus.addAll(frstParrent.GetSelection(cutpoint+1, frstParrent.size()));
            parent2RightLocus.addAll(scndParrent.GetSelection(cutpoint+1, scndParrent.size()));
            
            for(int j = 0; j < parent2RightLocus.size(); j++){
                //Check for candidates
                possibleBlocks = getPossibleBlocks(parent1LeftLocus);
                
                
                //Check if there are possible blocks
                if(possibleBlocks.size() > 0){
                    
                    if(possibleBlocks.contains(parent2RightLocus.get(0))){
                        //The gene is amongst the candidates so add it to the left locus of parent 1
                        parent1LeftLocus.add(parent2RightLocus.get(0));
                        parent2RightLocus.remove(0);
                        j--;
                    } else {
                        //Choose random candidate
                       selection = random.nextInt(possibleBlocks.size());
                       for(int k = 0; k < possibleBlocks.size(); k++){
                            if(selection == k){
                                parent1LeftLocus.add(possibleBlocks.get(k));                            
                            }
                        }
                        parent2RightLocus.remove(0);
                        j--;
                    } 
                } 
            }
            
            for(int j = 0; j < parent1RightLocus.size(); j++){
                //Check for candidates
                possibleBlocks = getPossibleBlocks(parent2LeftLocus);
                
                //Check if there are possible blocks
                if(possibleBlocks.size() > 0){

                    
                    if(possibleBlocks.contains(parent1RightLocus.get(0))){
                        //The gene is amongst the candidates so add it to the left locus of parent 1
                        parent2LeftLocus.add(parent1RightLocus.get(0));
                        parent1RightLocus.remove(0);
                        j--;
                    } else {
                        //Choose random candidate
                        selection = random.nextInt(possibleBlocks.size());
                        for(int k = 0; k < possibleBlocks.size(); k++){
                            if(selection == k){
                                parent2LeftLocus.add(possibleBlocks.get(k));                            
                            }
                        }
                        parent1RightLocus.remove(0);
                        j--;
                    } 
                }
            }

            
            //Fill the population with the newly created childs
            population.addChromosome(new Chromosome(population.getSize(), parent1LeftLocus));
            population.addChromosome(new Chromosome(population.getSize(), parent2LeftLocus));
            
            //Clear locusses for next use
            parent1LeftLocus.clear();
            parent2LeftLocus.clear();
            parent1RightLocus.clear();
            parent2RightLocus.clear();
           
        }

        return population;
    }
   
    /**
     * Finds all placable blocks of a not completed Chromosome
     * @param chromosoom Arraylist of block 
     * @return Arraylist of placable blocks
     */
    public ArrayList<Block> getPossibleBlocks(ArrayList<Block> chromosoom) {
        HashSet<Block> possibleBlocks = new HashSet<>();
        int buildNumber = generateBuildNumber();
        ListIterator<Block> li = chromosoom.listIterator(chromosoom.size());
        
        while (li.hasPrevious()) {
            Block block = li.previous();
            block.setBuildNumber(buildNumber);
            childs:
            for (Block child : block.getChilds()) {
                if(child.getBuildNumber() == buildNumber){
                    continue;
                }
                for (Block parrent : child.getParents()) {
                    if(parrent.getBuildNumber() != buildNumber){
                        continue childs;
                    }
                    
                }
                possibleBlocks.add(child);
            }
        }
        
        return new ArrayList(possibleBlocks);
    }
    
    /**
     * Calculates a new buildNumber
     * @return new Buildnumber
     */
    private int generateBuildNumber(){
        return (floor.getBuildNumber() >= Integer.MAX_VALUE)? 0 : floor.getBuildNumber()  + 1;
    }

   /**
     * generates a new population based on fitness of a other population. In
     * general this selection will select the best and some worst Chromosomes
     *
     * @param oldPop The input population to generate a new selected population
     * from
     * @param newPopSize The size of the new population
     * @return The new population
     */
    private Population getSelecetion(Population oldPop, int newPopSize, ArrayList<Double> selectionRatio) {
        Population newPop = new Population();
        int selectAmount = 0;
        double selectRangeMin = 0;
        double selectRangeMax = 0;
        double prevTotalFtnss = 0;
        PriorityQueue<Double> randFitnss = new PriorityQueue<>();
        Random r = new Random();
        
        oldPop.sortByFitness();

        // Add random doubles to a sored queue
        // This random doubels will select the Chromosomes based on its fitness     
        for (double ratio : selectionRatio) {
           selectAmount =(int)Math.round(((double)newPopSize) * ratio);
           selectRangeMax +=  oldPop.getTotalFitness() / selectionRatio.size();
            for (int i = 0; i < selectAmount; i++) {
               randFitnss.add( selectRangeMin + (selectRangeMax - selectRangeMin) * r.nextDouble() ); 
            }
           selectRangeMin = selectRangeMax;
           if(randFitnss.size() > newPopSize){
               break;
           }
        }

        /* Loop through all old pouplation and if the fitnss matcheses with 
         * the fitness, add it to the new pouplation
         */
        for (Iterator<Chromosome> it = oldPop.getList().iterator(); it.hasNext();) {
            Chromosome curChr = it.next();

            while ((prevTotalFtnss + curChr.getFitness()) >= randFitnss.element()) {
                // if: prevous chomesome fitnes < random selection fitness <= current chromesome fitness
                if (prevTotalFtnss < randFitnss.element() && randFitnss.element() <= (prevTotalFtnss + curChr.getFitness())) {
                    newPop.addChromosome(curChr);
                    randFitnss.remove();
                    if (randFitnss.isEmpty()) {
                        return newPop;
                    }
                }
            }
            prevTotalFtnss += curChr.getFitness();
        }
        
        return newPop;
        
    }
    
   /**
     * generates a new population based on fitness of a other population. In
     * general this selection will select the best and some worst Chromosomes
     *
     * @param population is the to be mutated population
     * @return the mutated population
     */
    public Population pseudoMutation(Population population){
        int selection;
        Random random = new Random();
        
        //Calculate the number mutations to be performed
        double part = (mutationPercentage / 100) * population.getSize();
        
        //Get the rounded number of mutations
        int numberOfMutations = (int) Math.round(part);
        
        for(int i = 0; i < numberOfMutations; i++){
            //Select a random chromosome
            selection = random.nextInt(population.getSize());
            
            //Create a new chromosome
            Chromosome newChromosome = guidedSearch(population.getChromosome(selection).getId());
            
            //set the newly created chromosome at the randomly selected index
            population.setChromosome(selection, newChromosome);
        }    
        
        return population;
    }
    
    /**
     * Generates a new populuation based on the selection algorithm of Marian   
     * @param oldPop the population where the new population selects from 
     * @param newPopSize the size of the new population
     * @return a new population 
     */
    public Population getSelectionMarian(Population oldPop, int newPopSize){
        ArrayList<Double> ratio = new ArrayList<>();
        ratio.add(1.0);
        return getSelecetion(oldPop, newPopSize, ratio);
    }
    
    /**
     * Generates a new populuation based on the selection algorithm of Marian with an optimization   
     * @param oldPop the population where the new population selects from 
     * @param newPopSize the size of the new population
     * @return a new population 
     */
    public Population getSelectionPandG(Population oldPop, int newPopSize){
        ArrayList<Double> ratio = new ArrayList<>(Arrays.asList(this.OptimilisationRatios));

        return getSelecetion(oldPop, newPopSize, ratio);
    }
    
    /**
     * Sets the selection ratio needed for the Marian optimized algorithm  
     * @param ratio Double array with the optimaliation settings 
     */
    public void setOptimizedSelectionRatio(Double[] ratio){
        this.OptimilisationRatios = ratio;
    }

    /**
     * Setups a Task which can solve a Marian problem based on a selected algorithm
     * @param algoritm the number of the algorithm to solve the problem
     * @return Task with a correct setting and ready to start
     */
    public Task< ArrayList<ObservableList<XYChart.Series<String, Double>>> > getTask(int algoritm){
        AlgorithmEvaluation = 0;
        ArrayList< ObservableList<XYChart.Series<String, Double>> > partialResults = new ArrayList<>();
        //final Marian  = this;
        
        
        if (stopCondition.isEnableStopTime()) {
            process = new Marian.Process(stopCondition.getRunTime()/1000);
        }else if(stopCondition.isEnableStopGenerations()){
            process = new Marian.Process(stopCondition.getNrOfGenerations());
        }

        
        Task < ArrayList<ObservableList<XYChart.Series<String, Double>>> > marianTask = new Task<ArrayList<ObservableList<XYChart.Series<String, Double>>>>() {
            @Override
            protected ArrayList<ObservableList<XYChart.Series<String, Double>>> call() throws Exception {
                XYChart.Series<String, Double> minCostSeries = new XYChart.Series<>();
                XYChart.Series<String, Double> avgCostSeries = new XYChart.Series<>();
                XYChart.Series<String, Double> maxCostSeries = new XYChart.Series<>();
                XYChart.Series<String, Double> maxFitnessSeries = new XYChart.Series<>();
                XYChart.Series<String, Double> AvgFitnessSeries = new XYChart.Series<>();
                
                String lineNameAdditon = "";
                if(algoritm == Marian.MarianOptimised){
                    lineNameAdditon += ", Optimised";
                }
                minCostSeries.setName("Min Costs"+ lineNameAdditon);
                avgCostSeries.setName("Avg Costs"+ lineNameAdditon);
                maxCostSeries.setName("Max Costs"+ lineNameAdditon);
                maxFitnessSeries.setName("Max Fitness"+ lineNameAdditon);
                AvgFitnessSeries.setName("Average Fitness"+ lineNameAdditon); 
                
                // set 
                partialResults.add(0 ,FXCollections.observableArrayList(new ArrayList()));
                partialResults.get(0).addAll(maxFitnessSeries, AvgFitnessSeries);
                
                partialResults.add(1 ,FXCollections.observableArrayList(new ArrayList()));
                partialResults.get(1).addAll(maxCostSeries, minCostSeries, avgCostSeries);
                setOptimizedSelectionRatio(OptimilisationRatios);
                int generations = 0;
            
                Population pop;

                if(usePopulation == null){
                    pop = generatePopulation(populationSize);
                }else{
                    pop = usePopulation;
                }

                stopCondition.Start();
                while(!stopCondition.isStop(generations)){
                    if (Thread.currentThread().isInterrupted()) {

                        updateProgress(1, 1);
                        return  partialResults;
                    }
                    
       
                    
                    if (stopCondition.isEnableStopTime()) {
                        process.setProcess( ((System.currentTimeMillis() - stopCondition.getStartTime())/1000) );   
                        updateProgress(((System.currentTimeMillis() - stopCondition.getStartTime())/1000), (stopCondition.getRunTime()/1000));
                    }else if(stopCondition.isEnableStopGenerations()){
                        process.setProcess( generations );
                        updateProgress(generations+1, stopCondition.getNrOfGenerations());
                    }
                     pop = crossOver(pop);


                    switch(algoritm){
                        case MarianOrignal:
                            pop = getSelectionMarian(pop, populationSize);
                            break;
                        case MarianOptimised:
                            pop = getSelectionPandG(pop, populationSize);
                            break;
                        default:
                            try {
                                throw new AlgorithmNotSet();
                            } catch (AlgorithmNotSet ex) {
                                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    }

                    pop = pseudoMutation(pop); 
                    
                    double max = pop.getMax().getFitness();
                    double minCosts = pop.getMin().getCosts();
                    double maxCosts = pop.getMax().getCosts();
                    double avgCosts = (pop.getMin().getCosts() + pop.getMax().getCosts()) / 2;
                    double avg =  pop.getAverageFittness();
                    int gen = generations;
                                        
                    Platform.runLater(new Runnable() {
                             
                        @Override public void run() {
                            
                            DecimalFormat dfFitness = new DecimalFormat("0000");
                            DecimalFormat dfCost = new DecimalFormat("0.0000");
                            /*
                            XYChart.Data<Number, Double> maxNode = new XYChart.Data(gen, max); 
                            XYChart.Data<Number, Double> avgFitnessNode = new XYChart.Data(gen, avg); 
                            XYChart.Data<Number, Double> maxCostNode = new XYChart.Data(gen, maxCosts); 
                            XYChart.Data<Number, Double> minCostNode = new XYChart.Data(gen, minCosts); 
                            XYChart.Data<Number, Double> avgCostNode = new XYChart.Data(gen, avgCosts); 
                            
                            maxNode.setNode(new HoverNode(gen, dfCost.format(max)));
                            avgFitnessNode.setNode(new HoverNode(gen, dfCost.format(avg)));
                            maxCostNode.setNode(new HoverNode(gen, dfFitness.format(maxCosts)));
                            avgCostNode.setNode(new HoverNode(gen, dfFitness.format(avgCosts)));
                            minCostNode.setNode(new HoverNode(gen, dfFitness.format(minCosts)));
                           
                            partialResults.get(0).get(0).getData().add(maxNode);
                            partialResults.get(0).get(1).getData().add(avgFitnessNode);
                            partialResults.get(1).get(0).getData().add(maxCostNode);
                            partialResults.get(1).get(1).getData().add(minCostNode);
                            partialResults.get(1).get(2).getData().add(avgCostNode);
                            */
                            
                            
                            XYChart.Data<Number, Double> maxCostNode = new XYChart.Data(gen, maxCosts); 
                            XYChart.Data<Number, Double> minCostNode = new XYChart.Data(gen, minCosts); 
                            XYChart.Data<Number, Double> avgCostNode = new XYChart.Data(gen, avgCosts); 
                            XYChart.Data<Number, Double> maxNode = new XYChart.Data(gen, max); 
                            XYChart.Data<Number, Double> avgFitnessNode = new XYChart.Data(gen, avg); 
                            
                            maxCostNode.setNode(new HoverNode(gen, dfFitness.format(maxCosts)));
                            minCostNode.setNode(new HoverNode(gen, dfFitness.format(minCosts)));
                            avgCostNode.setNode(new HoverNode(gen, dfFitness.format(avgCosts)));
                            maxNode.setNode(new HoverNode(gen, dfFitness.format(max)));
                            avgFitnessNode.setNode(new HoverNode(gen, dfFitness.format(avg)));
                            
                            partialResults.get(1).get(0).getData().add(maxCostNode);
                            partialResults.get(1).get(1).getData().add(minCostNode);
                            partialResults.get(1).get(2).getData().add(avgCostNode);
                            partialResults.get(0).get(0).getData().add(maxNode);
                            partialResults.get(0).get(1).getData().add(avgFitnessNode);
                                                        
                            updateValue(partialResults);
                            
                            updateValue(partialResults);
                        }
                    });
                    
                    
                    AlgorithmEvaluation += pop.getMin().getCosts();
                    generations++;
                }
                return partialResults;
            }
        };
         return marianTask;
    }
    
    //getFloor
    //  Beschrijving: getFloor returned de floor.
    //  Input: -
    //  Output: De floor.
    //  Gemaakt door: Gerco Versloot 
    public Block getFloor() {
        return floor;
    }

    //setFloor
    //  Beschrijving: setFloor slaat het opgegeven Block op als floor.
    //  Input: Een Block.
    //  Output: - 
    //  Gemaakt door: Gerco Versloot 
    public void setFloor(Block floor) {
        this.floor = floor;
    }

    //getBlockCollection
    //  Beschrijving: getBlockCollection returned de BlockCollection.
    //  Input: -
    //  Output: De Blockcollection.
    //  Gemaakt door: Gerco Versloot 
    public ArrayList<Block> getBlockCollection() {
        return blockCollection;
    }

    //setBlockCollection
    //  Beschrijving: setBlockCollection slaat de opgegeven ArrayList van Blocken op als BlockCollection.
    //  Input:  Een BlockCollection.
    //  Output: - 
    //  Gemaakt door: Gerco Versloot 
    public void setBlockCollection(ArrayList<Block> blockCollection) {
        this.blockCollection = blockCollection;
    }

    //getDependencyMatrix
    //  Beschrijving: getDependencyMatrix returned de DependencyMatrix.
    //  Input: - 
    //  Output: Een DependencyMatrix.
    //  Gemaakt door: Gerco Versloot 
    public Boolean[][] getDependencyMatrix() {
        return dependencyMatrix;
    }

    //setDependencyMatrix
    //  Beschrijving: setDependencyMatrix slaat de opgegeven Booleanarray op als DependencyMatrix.
    //  Input: Een DependencyMatrix als booleanarray.
    //  Output: -
    //  Gemaakt door: Gerco Versloot 
    public void setDependencyMatrix(Boolean[][] dependencyMatrix) {
        this.dependencyMatrix = dependencyMatrix;
    }

    //getFysicalMatrix
    //  Beschrijving: getFysicalMatrix returned de FysicalMatrix.
    //  Input: -
    //  Output: De FysicalMatrix.
    //  Gemaakt door: Gerco Versloot 
    public Block[][] getFysicalMatrix() {
        return fysicalMatrix;
    }

    //setFysicalMatrix
    //  Beschrijving: setFysicalMatrix slaat de opgegeven Blockarray op als FysicalMatrix.
    //  Input: Een FysicalMatrix als Blockarray.
    //  Output: -
    //  Gemaakt door: Gerco Versloot 
    public void setFysicalMatrix(Block[][] fysicalMatrix) {
        this.fysicalMatrix = fysicalMatrix;
    }
    
    public Chromosome getFirstMin() {
        return firstMin;
    }
    
    
    public int getLength(){
        return fysicalMatrix[0].length;
    }
    
    public Population getUsePopulation() {
        return usePopulation;
    }

    public void setUsePopulation(Population usePopulation) {
        this.usePopulation = usePopulation;
    }

    public long getMaxRunTime() {
        return maxRunTime;
    }

    public void setMaxRunTime(long maxRunTime) {
        this.maxRunTime = maxRunTime;
    }
    
    public Double[] getOptimilisationRatios() {
        return OptimilisationRatios;
    }

    public void setOptimilisationRatios(Double[] OptimilisationRatios) {
        this.OptimilisationRatios = OptimilisationRatios;
    }
    
      public StopConditionsMarian getStopCondition() {
        return stopCondition;
    }
      
    public Process getProcess() {
        return process;
     }
    
    public Integer getAlgorithmEvaluation() {
        return AlgorithmEvaluation;
    }
    
    public int getPopulationSize() {
        return populationSize;
    }
    public double getMutationPercentage() {
        return mutationPercentage;
    }
    
     public int getProblemSize() {
        return problemSize;
    }
    
     /**
      * Subclass that contains the progress of the running Marian task
      */
    class Process{
        private double process;

        private double totalWork;
         
        /**
         * 
         * @param totalWork Amount of work that has to be finished
         */
         public Process(double totalWork){
             this.totalWork = totalWork;
         }
         
        public double getProcess() {
            return process;
        }

        public void setProcess(double process) {
            this.process = process;
        }

        public double getTotalWork() {
            return totalWork;
        }

        public void setTotalWork(double totalWork) {
            this.totalWork = totalWork;
        }
 
     }  
}
