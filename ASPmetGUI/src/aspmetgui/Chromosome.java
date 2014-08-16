package aspmetgui;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * The Chromosome contains a sequence of blocks
 * @author Gerco
 */
public class Chromosome implements Comparable<Chromosome> {

    private ArrayList<Block> sequence = new ArrayList<>();
    private int id;

    
    private double fitness; 
    private int Costs = 0;
    private boolean last = false;
    
    private Block prevBlock;
    
    /**
     * Create empty chromosome 
     * @param ID id of the chromosome 
     */
    public Chromosome(int ID){
        this.id = ID;
    }
    
    /**
     * Create Chromosome with list
     * @param ID id of the chromosome
     * @param blocks arraylist of blocks to create chromosome 
     */
    public Chromosome(int ID, ArrayList<Block> blocks){
        this.id = ID;
        this.sequence.addAll(blocks);
        CalculateSize();
    }
    
    /**
     * Add a block at the end of the chromosome 
     * @param newBlock The block that will be added
     */
    public void AddBlockToSequence( Block newBlock){
       if(sequence.isEmpty()){
           prevBlock = newBlock;
       }
       // calcualte new block size
       Costs += Math.abs(newBlock.getID() - prevBlock.getID());
       this.sequence.add(newBlock);
       prevBlock = newBlock;
    } 
    /**
     * Calculate the size of the chromosome
     */
    private void CalculateSize(){
        prevBlock = sequence.get(0);
        
        for(Block currentBlock : sequence) {
            Costs += Math.abs(currentBlock.getID() - prevBlock.getID());
            prevBlock = currentBlock;   
        }
        
        
    }
    
    /**
     * Get a section of the chromosome 
     * @param Min starting index
     * @param Max last index
     * @return arraylist of block from Min to Max 
     */
    public ArrayList<Block> GetSelection(int Min,int Max){
        ArrayList<Block> selection = new ArrayList();
        for (Iterator<Block> it = sequence.listIterator(Min); it.hasNext() && Max - Min  >= 0 ;) {
            selection.add(it.next());
            Max--;   
        }
        for (int i = Min; i < Max ; i++) {
            selection.add(sequence.get(i));
        }
        return selection;
    }
    
    public String ToString(){
        return this.ToStringChromosome(", ");
    }
    
    public String ToStringChromosome(String devider){
        String output = new String();
        output = "id:" + this.id + "| ";
        int size = this.sequence.size();
        for (int i = 0; i < sequence.size(); i++) { 
            output += this.sequence.get(i).getID();
            if (--size != 0) {
               output += devider;
            }
        }
        output += devider + "size: " + this.Costs; 
        output += devider + "fitness: " + this.fitness; 
        return output; 
    }
    
    /**
     * Compares this Chromosome object with an other Chromosome object based on both fitnesses  
     * @param t the Chromosome to compare to
     * @return an interger with the difference between the objects, 0: is equal, 1: t is better, -1: t is worse
     */
    @Override
    public int compareTo(Chromosome t) {
         return Double.compare(t.getFitness(), this.getFitness());
    }
    
    public ArrayList<Block> getSequence() {
        return sequence;
    }

    public void setSequence(ArrayList<Block> sequence) {
        this.sequence = sequence;
    }
    
    public Block GetBlocBykIndex(int index){
        return this.sequence.get(index);
    }
    
    public void AddBlockArrayToSequence(ArrayList<Block> Blocks){
        this.sequence.addAll(Blocks);
    }
    
    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * @return the Size
     */
    public int getCosts() {
        /*if(!sequence.isEmpty() && !last) {
            Costs += sequence.get(sequence.size()-1).getID();
            last = true;
        }*/
        return Costs;
    }
    
    public int getId() {
        return id;
    }

    public Block getPrevBlock() {
        return prevBlock;
    }
    
    public int size(){
        return this.sequence.size();
    }


}
