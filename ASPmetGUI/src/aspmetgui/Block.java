/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aspmetgui;

import java.util.ArrayList;

/**
 * The block with all the information about the block in a problem
 * @author Gerco en Peter
 */
public class Block {
    private int ID;
    private int MinX;
    private int MaxX;
    private int MinY;
    private int MaxY;
    private int Hight;
    private int Width;
    private ArrayList<Block> Parents;
    private ArrayList<Block> Childs;

   
    private Block[] Siblings;
    private int buildNumber;
    
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    /**
     * Constructor for a new block, without parents, Childs or Siblings  
     * @param ID id of the block
     * @param MinX Minimal X position 
     * @param MaxX Maximal X position
     * @param MinY Minimal Y position
     * @param MaxY Maximal Y position
     */
    public Block(int ID, int MinX, int MaxX, int MinY, int MaxY) {
        this.ID = ID;
        this.MinX = MinX;
        this.MaxX = MaxX;
        this.MinY = MinY;
        this.MaxY = MaxY;
        
        int y = this.MaxY-this.MinY;
        
        int x = MaxX-this.MinX;
        
        if(x < 1){
            this.Width = 1;
        } else {
            this.Width = x+1;
        }
        
        if(y < 1){
            this.Hight = 1;
        } else {
            this.Hight = y+1;
        }

        Parents = new ArrayList<>();
        Childs = new ArrayList<>();
        Siblings = new Block[2] ;
    }
    
    /**
     * Constructor for a new block, with parents and Siblings
     * @param ID id of the block
     * @param MinX Minimal X position 
     * @param MaxX Maximal X position
     * @param MinY Minimal Y position
     * @param MaxY Maximal Y position
     * @param Parents The parents of this block (Blocks that are need before this block can be placed)
     * @param Siblings The left [0], and the right [1] sibling 
     */
    public Block(int ID, int MinX, int MaxX, int MinY, int MaxY, ArrayList<Block> Parents, Block[] Siblings) {
        this.ID = ID;
        this.MinX = MinX;
        this.MaxX = MaxX;
        this.MinY = MinY;
        this.MaxY = MaxY;
        this.Parents = Parents;
        this.Siblings = Siblings;
    }
    
    /**
     * Add a parents, Blocks that are need before this block can be placed
     * @param blocks Arraylist of blocks that are parents of this block
     */
    public void AddParents(ArrayList<Block> blocks){
        for (Block block1 : blocks) {
            this.Parents.add(block1);
        }
    }    
    
    /**
     * Add one parent, a block that is needed before this block can be placed 
     * @param block the parents
     */
    public void AddParent(Block block){
        this.Parents.add(block);
    }    
    
    /**
     * Add a child, a block that can be placed after this block is placed
     * @param child the block that is the child
     */
    public void addChild(Block child){
        this.Childs.add(child);
    }
    
    /**
     * Output this block to the console
     * @param separator the divider of the information
     */
    public void PrintBlock(String separator){
        System.out.println(toString(separator));
    }
    
    /**
     * Creates a string with all the information about this block
     * @param separator the divider of the information
     * @return the sting with all the information
     */
    public String toString(String separator){
       String returnString = new String();
       
       returnString  = ID + ": ";
       returnString  += MaxX + separator;
       returnString  += MinX + separator;
       returnString  += MaxY + separator;
       returnString  += MinY + separator;
       returnString  += "Parents: [";
        for (Block block : Parents) {
            returnString  += block.getID() + separator;
        }
       returnString  += "]"+ separator;
       returnString  += "Childs: [";
        for (Block block : Childs) {
            returnString  += block.getID() + separator;
        }
       returnString  += "]"+ separator;;
       returnString  += "Siblings: [";
        for (Block block : Siblings) {
            if(block != null){
                returnString  += block.getID() + separator;
            }
        }
       returnString  += "]"+ separator;;
       
       return returnString;
    }
    
   public int getWidth(){
        return this.Width;
    }
        
    public int getHeight(){
        return this.Hight;
    }
    
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMinX() {
        return MinX;
    }

    public void setMinX(int MinX) {
        this.MinX = MinX;
    }

    public int getMaxX() {
        return MaxX;
    }

    public void setMaxX(int MaxX) {
        this.MaxX = MaxX;
    }

    public int getMinY() {
        return MinY;
    }

    public void setMinY(int MinY) {
        this.MinY = MinY;
    }

    public int getMaxY() {
        return MaxY;
    }

    public void setMaxY(int MaxY) {
        this.MaxY = MaxY;
    }

    public ArrayList<Block> getParents() {
        return Parents;
    }

    public void setParents(ArrayList<Block> Parents) {
        this.Parents = Parents;
    }

    public Block[] getSiblings() {
        return Siblings;
    }
    
    public void setSiblings(Block[] block){
        this.Siblings = block;
    } 
    
    public void setSibling(int side, Block sibling){
        switch(side){
            case LEFT:
                this.Siblings[Block.LEFT] =  sibling;
                break;
            case RIGHT:
                this.Siblings[Block.RIGHT] =  sibling;
                break; 
        }
    }
    
    public Block getSibling(int side){
        switch(side){
            case LEFT:
                return this.Siblings[Block.LEFT];
            case RIGHT:
                return this.Siblings[Block.RIGHT];
        }
        return null;
    }
    
    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }
    
     public ArrayList<Block> getChilds() {
        return Childs;
    }

    public void setChilds(ArrayList<Block> Childs) {
        this.Childs = Childs;
    }
}
