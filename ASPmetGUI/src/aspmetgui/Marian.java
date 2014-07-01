package aspmetgui;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Random;


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
    private Double[] collection;


    /**
     * Create a problem to solve with the algorithm of Marian
     *
     * @param filename the file location that contains the blocks information,
     * id minX maxX minY maxY
     * @param populationSize the size of a population
     */
    public Marian(String filename, int populationSize, double mutation) {
        problemSize = getProblemSize(filename);
        this.populationSize = populationSize;
        this.mutationPercentage = mutation;
        System.out.println("ProblemSize = " + problemSize);

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

    //getProblemSize
    //  Beschrijving: getProblemSize leest het bestand in het opgegeven pad en berekend hieruit de groote van het probleem.
    //  Input: De locatie van het uit te lezen bestand.
    //  Output: problemSize als een Integer.
    //  Gemaakt door: Peter Tielbeek.
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

    //StringToIntArray
    //  Beschrijving: StringToIntArray loopt de door StringToBlock verkregen Stringarray door om deze vervolgens naar Integers te parsen en deze in een array terug te geven.
    //  Input: Een stringarray met daarin alleen cijfers
    //  Output: Een integerarray met daarin alleen Integers
    //  Gemaakt door: Gerco Versloot
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

    //BlockIntoFysicalMatrix
    //  Beschrijving: Zet een opgegeven block in de FysicalMatrix.
    //  Input: Een Block
    //  Output: -
    //  Gemaakt door: Gerco Versloot
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

    //initializeDependencyMatrix
    //  Beschrijving: initializeDependencyMatrix vult en genereerd de DependencyMatrix met een opgegeven waarde.
    //  Input: De grootte van de DependencyMatrix, De waarde waarmee deze volledig gevuld wordt.
    //  Output: -
    //  Gemaakt door: Gerco Versloot
    private void initializeDependencyMatrix(int size, boolean default_val) {
        dependencyMatrix = new Boolean[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                dependencyMatrix[i][j] = default_val;
            }

        }
    }

    //convertToDependencyMatrix
    //  Beschrijving: 
    //  Input: 
    //  Output:
    //  Gemaakt door: Gerco Versloot
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

    //ToStringBlockCollection
    //  Beschrijving: ToStringBlockCollection zet alle blokken netjes in een lijst om zo duidelijk te maken welke blokken er zijn.
    //  Input: -
    //  Output: Een string met daarin alle blokken.
    //  Gemaakt door: Gerco Versloot
    public String ToStringBlockCollection() {
        String returnString = new String();
        for (Block block : blockCollection) {
            returnString += block.ToString(", ") + "\n";
        }
        return returnString;
    }

    //ToStringDependencyMatrix
    //  Beschrijving: ToStringDependencyMatrix zet de gehele DependencyMatrix in een nette string zodat deze grafisch weer te geven is.
    //  Input: -
    //  Output: Een String met daarin de DependencyMatrix.
    //  Gemaakt door: Gerco Versloot
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
            pop.addChromosome(guidedSearchBetter(i));
        }
        return pop;
    }
    
    public Population generatePopulationBetter(int populationSize) {
        Population pop = new Population();
        for (int i = 0; i < populationSize; i++) {
            pop.addChromosome(guidedSearchBetter(i));
        }
        this.firstMin = pop.getMin();
        return pop;
    }

    /**
     * Generates a Chromosome based on the Marian approach: select random block
     * and place it, find new possibilities
     *
     * @param newId
     * @return a new Chromosome
     * @author Gerco Versloot
     */
    public Chromosome guidedSearch(int newId) {
        Chromosome newChrom = new Chromosome(newId);                // the new Chomosome to return filled with blocks
        Block[] doneStack = new Block[blockCollection.size()];      // stores all placed blocks (For speed)
        ArrayList<Block> possibleStack = new ArrayList<>();         // List of all blocks that are placable  
        possibleStack.add(floor);                                   // add floor to start of stack    
        Block currentBlock;

        while (possibleStack.size() > 0) {
            Random random = new Random();
            currentBlock = possibleStack.get(random.nextInt(possibleStack.size())); // select a random block
            doneStack[currentBlock.getID()] = currentBlock;                         // add block to the done stack
            possibleStack.remove(currentBlock);                                     // remove block from possible list
            newChrom.AddBlockToSequence(currentBlock);                              // add block to the Chomosome 

            possibleStack.addAll(getPossibleNextBlocks(currentBlock, doneStack));   // search for new possibilities and add to possible list
        }
        return newChrom;
    }

    /**
     * Generate the rest of a Chromosome, based on Marian guidedsearch approach;
     *
     * @param chr unfinished Chromosome
     * @return the finished Chromosome
     */
    public Chromosome guidedSearch(Chromosome chr) {
        Block[] doneStack = new Block[blockCollection.size()];      // stores all placed blocks (For speed)
        ArrayList<Block> possibleStack = new ArrayList<>();         // List of all blocks that are placable    
        Block currentBlock;

        //calculate all possibilities from the current Chromosome
        for (Block block : chr.getSequence()) {
            doneStack[block.getID()] = block;
            possibleStack.remove(block);
            possibleStack.addAll(getPossibleNextBlocks(block, doneStack));  // add to possibilities list
        }

        // calculate and add all possibilities from possibilities list  
        while (possibleStack.size() > 0) {
            Random random = new Random();
            currentBlock = possibleStack.get(random.nextInt(possibleStack.size())); // select a random block
            doneStack[currentBlock.getID()] = currentBlock;                         // add block to the done stack
            possibleStack.remove(currentBlock);                                     // remove block from possible list
            chr.AddBlockToSequence(currentBlock);                                   // add block to the Chomosome 

            possibleStack.addAll(getPossibleNextBlocks(currentBlock, doneStack));   // search for new possibilities and add to possible list
        }
        return chr;
    }

    //getPossibleNextBlocks
    //  Beschrijving: getPossibleNextBlocks
    //  Input: 
    //  Output:
    //  Gemaakt door: Gerco Versloot
    private ArrayList<Block> getPossibleNextBlocks(Block currentBlock, Block[] doneStack) {
        ArrayList<Block> possibleBlocks = new ArrayList<>();
        Block tempBlock;
        for (int i = 0; i < blockCollection.size(); i++) {
            if (dependencyMatrix[i][currentBlock.getID()]) {
                tempBlock = blockCollection.get(i);
                Boolean addBlock = true;
                for (Block block : tempBlock.getParents()) {
                    if (doneStack[block.getID()] == null) {
                        addBlock = false;
                        break;
                    }
                }
                if (addBlock) {
                    possibleBlocks.add(tempBlock);
                }
            }

        }
        return possibleBlocks;
    }
    
    public Chromosome guidedSearchBetter(int newId) {
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
            
            possibleStack.addAll(getPossibleNextBlocksBetter(currentBlock));   // search for new possibilities and add to possible list
            
        }
        return newChrom;
    }
    
    private ArrayList<Block> getPossibleNextBlocksBetter(Block currentBlock) {
        ArrayList<Block> possibleBlocks = new ArrayList<>();
        
        // loop though all the childeren 
        for (Block child : currentBlock.getChilds()) {
            Boolean addChild = true;
            //loop trough all the parrents to check if there are not placed
            for (Block childParrend : child.getParents()) {
                if(childParrend.getBuildNumber() != currentBlock.getBuildNumber()){
                    // parrent is not placed so dont add the child
                    addChild = false;
                }
            }
            if(addChild){
                possibleBlocks.add(child);
            }
        }
        return possibleBlocks;
    }
    
    

    //crossover --> is niet de werkelijke crossover, ik heb een verdubelaar gemaakt van een populatie, had ik even nodig :p
    public Population crossover(Population parents, int amountChilds) {
        for (int i = 0; i < amountChilds; i++) {
            parents.addChromosome(parents.getList().get(i));
        }
        return parents;
    }

    public Population crossOver(Population population) {
        //System.out.println("- Crossover - ");
        //System.out.println("    "+population.toString());
        
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
            parents.add(guidedSearchBetter(parents.size()+1));
        }

        
        for(int i = 0; i < parents.size(); i = i + 2){
            //System.out.println("Parent 1: "+parents.get(i).ToString());
            //System.out.println("Parent 2: "+parents.get(i+1).ToString());
            
            //C2
            //For the first pair of parent chromosomes randomly select the cut point (gene 2 to n-1)
            // random.nextInt(max - min) + min
            min = 2;
            max = getBlockCollection().size() - 1;
            
            cutpoint = random.nextInt(max - min) + min;
            //System.out.println("Cutpoint: " + cutpoint);
            
            //Fill left locusses
            for(int j = 0; j < cutpoint; j++){
               parent1LeftLocus.add(parents.get(i).getSequence().get(j));
               parent2LeftLocus.add(parents.get(i+1).getSequence().get(j));
            }
            
            //Fill right locusses
            for(int j = cutpoint; j < getBlockCollection().size(); j++){
                parent1RightLocus.add(parents.get(i).getSequence().get(j));
                parent2RightLocus.add(parents.get(i+1).getSequence().get(j));
            }
            
            //Print locusses
            //System.out.print("Parent 1 Left Locus: ");
            for(int j = 0; j < parent1LeftLocus.size(); j++){
                //System.out.print(parent1LeftLocus.get(j).getID() + ", ");
            }
            //System.out.println();
            
            //System.out.print("Parent 2 Left Locus: ");
            for(int j = 0; j < parent2LeftLocus.size(); j++){
                //System.out.print(parent2LeftLocus.get(j).getID() + ", ");
            }
            //System.out.println();
            
            //System.out.print("Parent 1 Right Locus: ");
            for(int j = 0; j < parent1RightLocus.size(); j++){
                //System.out.print(parent1RightLocus.get(j).getID() + ", ");
            }
            //System.out.println();
            
            //System.out.print("Parent 2 Right Locus: ");
            for(int j = 0; j < parent2RightLocus.size(); j++){
                //System.out.print(parent2RightLocus.get(j).getID() + ", ");
            }
            //System.out.println();
            
            
            //System.out.println();
            //System.out.println("Guided search parent 1");
            //System.out.println();

            //C3
            //For the first parent chromosome
            //C4
            //For first locus at the right hand side of the cut point
            //Determine, by guided search, the candidate vertices for the gene;
            //If the gene in the corresponding locus from the other parent is amongst candidates, THEN choose it, ELSE place any other candidate gene;
            
            for(int j = 0; j < parent2RightLocus.size(); j++){
                //Check for candidates
                possibleBlocks = getPossibleBlocksBetter2(parent1LeftLocus);
                
                //System.out.print("Parent1LeftLocus: ");
                for(int k = 0; k < parent1LeftLocus.size(); k++){
                    //System.out.print(parent1LeftLocus.get(k).getID() + ", ");
                }
                //System.out.println();
                //System.out.print("Parent2RightLocus: ");
                for(int k = 0; k < parent2RightLocus.size(); k++){
                    //System.out.print(parent2RightLocus.get(k).getID()+", ");
                }
                //System.out.println();
                //System.out.print("Possible blocks: ");
                for(int k = 0; k < possibleBlocks.size(); k++){
                    //System.out.print(possibleBlocks.get(k).getID()+", ");
                }
                //System.out.println();
                
                //Check if there are possible blocks
                if(possibleBlocks.size() > 0){
                    //System.out.println();
                    
                    if(possibleBlocks.contains(parent2RightLocus.get(0))){
                        //The gene is amongst the candidates so add it to the left locus of parent 1
                        parent1LeftLocus.add(parent2RightLocus.get(0));
                        //System.out.println("Gene "+parent2RightLocus.get(0).getID()+" is amongst the candidates "+parent2RightLocus.get(0).getID()+" is placed");
                        parent2RightLocus.remove(0);
                        j--;
                    } else {
                        //Choose random candidate
                        selection = random.nextInt(possibleBlocks.size());
                        //System.out.println("Gene "+parent2RightLocus.get(0).getID()+" is not amongst the candidates random block "+possibleBlocks.get(selection).getID()+" is placed");
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
            //System.out.println();
            //System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");

            
            //System.out.println();
            //System.out.println("Guided search parent 2");
            //System.out.println();
            
            
            for(int j = 0; j < parent1RightLocus.size(); j++){
                //Check for candidates
                possibleBlocks = getPossibleBlocksBetter2(parent2LeftLocus);
                
                //System.out.print("Parent2LeftLocus: ");
                for(int k = 0; k < parent2LeftLocus.size(); k++){
                    //System.out.print(parent2LeftLocus.get(k).getID() + ", ");
                }
                //System.out.println();
                //System.out.print("Parent1RightLocus: ");
                for(int k = 0; k < parent1RightLocus.size(); k++){
                    //System.out.print(parent1RightLocus.get(k).getID()+", ");
                }
                //System.out.println();
                //System.out.print("Possible blocks: ");
                for(int k = 0; k < possibleBlocks.size(); k++){
                    //System.out.print(possibleBlocks.get(k).getID()+", ");
                }
                //System.out.println();
                
                //Check if there are possible blocks
                if(possibleBlocks.size() > 0){
                    //System.out.println();
                    
                    if(possibleBlocks.contains(parent1RightLocus.get(0))){
                        //The gene is amongst the candidates so add it to the left locus of parent 1
                        parent2LeftLocus.add(parent1RightLocus.get(0));
                        //System.out.println("Gene "+parent1RightLocus.get(0).getID()+" is amongst the candidates "+parent1RightLocus.get(0).getID()+" is placed");
                        parent1RightLocus.remove(0);
                        j--;
                    } else {
                        //Choose random candidate
                        selection = random.nextInt(possibleBlocks.size());
                        //System.out.println("Gene "+parent1RightLocus.get(0).getID()+" is not amongst the candidates random block "+possibleBlocks.get(selection).getID()+" is placed");
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

            //System.out.println();
            //System.out.print("Parent1 before crossover: ");
            for(int k = 0; k < parents.get(i).getSequence().size(); k++){
                //System.out.print(parents.get(i).getSequence().get(k).getID() + ", ");
            }
            //System.out.println();
            //System.out.print("Parent1 Child:          : ");
            for(int k = 0; k < parent1LeftLocus.size(); k++){
                //System.out.print(parent1LeftLocus.get(k).getID() + ", ");
            }
            //System.out.println();
            //System.out.print("Parent2 before crossover: ");
            for(int k = 0; k < parents.get(i+1).getSequence().size(); k++){
                //System.out.print(parents.get(i+1).getSequence().get(k).getID() + ", ");
            }
            //System.out.println();
            //System.out.print("Parent2 Child:          : ");
            for(int k = 0; k < parent2LeftLocus.size(); k++){
                //System.out.print(parent2LeftLocus.get(k).getID() + ", ");
            }
            //System.out.println();
            //System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
            
            
            //Fill the population with the newly created childs
            population.addChromosome(new Chromosome(population.getSize(), parent1LeftLocus));
            population.addChromosome(new Chromosome(population.getSize(), parent2LeftLocus));
            
            //Clear locusses for next use
            parent1LeftLocus.clear();
            parent2LeftLocus.clear();
            parent1RightLocus.clear();
            parent2RightLocus.clear();
            //System.out.println();
        }
        
        //C5
        //Repeat step C4 for all loci to the end of the chromosome
        //C6 
        //Repeat seteps C4-5 for the second parent chromosome
        //C7
        //Repeat steps C2 - C6 for the remaining pairs of parent chromosomes
        //A population of feasible offspring (Children) chromosomes;     
        //System.out.println("New population of children!");
        //System.out.println(population.toString());
        
        return population;
    }

    /**
     * Searches for the possible candidates to be placed
     *
     * @author Peter Tielbeek
     * @param leftLocus is the leftLocus of a parent.
     * @return The possible candidates of blocks to be placed
     */
    public ArrayList<Block> getPossibleBlocks(ArrayList<Block> leftLocus) {
        ArrayList<Block> possibleBlocks = new ArrayList<>();
        ArrayList<Integer> bannedBlocks = new ArrayList<>();

        //Check for every placed block the blocks on top
        for (int i = 0; i < leftLocus.size(); i++) {
            //Check the dependencyMatrix vertically for Blocks wich can be placed
            for (int j = 0; j < dependencyMatrix.length; j++) {
                if (dependencyMatrix[j][leftLocus.get(i).getID()]) {
                    //Check for the needed blocks
                    for (int k = 0; k < dependencyMatrix.length; k++) {
                        if (dependencyMatrix[j][k]) {
                            for (int l = 0; l < leftLocus.size(); l++) {
                                //Check if the block is placed
                                if (leftLocus.get(l).getID() == k) {
                                    //Add to posible Blocks
                                    possibleBlocks.add(blockCollection.get(j));
                                    break;
                                }
                                //If the block needed block is not placed
                                if (l == leftLocus.size() - 1) {
                                    //Ban the block
                                    bannedBlocks.add(j);
                                }
                            }
                        }
                    }
                }
            }
        }

        //Remove Duplicates
        HashSet hs = new HashSet();
        hs.addAll(possibleBlocks);
        possibleBlocks.clear();
        possibleBlocks.addAll(hs);

        //Remove already placed blocks
        for (int i = 0; i < leftLocus.size(); i++) {
            for (int j = 0; j < possibleBlocks.size(); j++) {
                if (leftLocus.get(i).getID() == possibleBlocks.get(j).getID()) {
                    possibleBlocks.remove(j);
                    break;
                }
            }
        }

        //Remove banned blocks
        for (int i = 0; i < bannedBlocks.size(); i++) {
            for (int j = 0; j < possibleBlocks.size(); j++) {
                if (bannedBlocks.get(i).equals(possibleBlocks.get(j).getID())) {
                    possibleBlocks.remove(j);
                    break;
                }
            }
        }

        return possibleBlocks;
    }
    
    public ArrayList<Block> getPossibleBlocksBetter(ArrayList<Block> chromosoom) {
        ArrayList<Block> possibleBlocks = new ArrayList<>();
        HashSet<Block> childs = new HashSet<>();
        int buildNumber = floor.getBuildNumber() + 1;
        
        for (Block block : chromosoom) {
            block.setBuildNumber(buildNumber);
            childs.addAll(block.getChilds());
        }
        
        for (Block child : childs) {
            if(child.getBuildNumber() ==  buildNumber){
                continue;
            }
            for (Block parrent : child.getParents()) {   
                if(parrent.getBuildNumber() != buildNumber){
                    break;
                }       
            }
            possibleBlocks.add(child);
            
        }
        return possibleBlocks;
    }
    
    public ArrayList<Block> getPossibleBlocksBetter2(ArrayList<Block> chromosoom) {
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
    
    public Population getSelectionMarian(Population oldPop, int newPopSize){
        ArrayList<Double> ratio = new ArrayList<>();
        ratio.add(1.0);
        return getSelecetion(oldPop, newPopSize, ratio);
    }
    
    public Population getSelectionPandG(Population oldPop, int newPopSize){
        ArrayList<Double> ratio = new ArrayList<>(Arrays.asList(this.collection));

        return getSelecetion(oldPop, newPopSize, ratio);
    }
    
    public void setOptimizedSelectionRatio(Double[] collection){
        this.collection = collection;
    }
    
    
    public Population run(Population pop, int popSize){
        int gernerations = 0;
        while(pop.getMax() != pop.getMin()){
           System.out.println("Start crossover");
           pop = this.crossOver(pop);
           System.out.println("Start selection");
           pop = this.getSelectionPandG(pop, popSize);
           System.out.println("Start mutation");
           pop = this.pseudoMutation(pop);
           System.out.println("Gener " + gernerations + ": Max: " + pop.getMax().getCosts() + "  Min: " + pop.getMin().getCosts());
           System.out.println("Gener " + gernerations + ": Max: " + pop.getMax().getFitness() + "  Min(from first): " + (1.0-(1.0/((double)firstMin.getCosts()/(double)pop.getMin().getCosts())))*10 + "  AVG: " + pop.getAverageFittness() );
           gernerations++;
           System.out.println("---------------------------------------------------------------");
        }
        return pop;
    }
    
    public Population generations(Population pop, int popSize){
        pop = this.crossOver(pop);
        pop = this.getSelectionPandG(pop, popSize);
        pop = this.pseudoMutation(pop);
        return pop;
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

    public void run() {
        Population pop = generatePopulationBetter(populationSize);
        this.run(pop, populationSize);
        
    }
    
    public Chromosome getFirstMin() {
        return firstMin;
    }
    
    
    public int getLength(){
        return fysicalMatrix[0].length;
    }

}
