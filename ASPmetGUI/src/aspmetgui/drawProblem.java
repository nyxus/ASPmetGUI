package aspmetgui;

import java.util.ArrayList;
import java.util.Random;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * A task to calculate and draw a problem onto a canvas
 * @author Gerco Versloot
 */
public class drawProblem extends Task<Canvas> {
    
    private Canvas canvasProblemGraphical;
    private Marian marian;
    private boolean toggleBlockNr;
    
    /**
     * Constructor to setup the properties of problem
     * @param canvasProblemGraphical the canvas to draw the problem on to. 
     * @param marian the problem that has to be drawn 
     * @param toggleBlockNr enable or disable block numbers
     */
    public drawProblem(Canvas canvasProblemGraphical, Marian marian, boolean toggleBlockNr){
        this.canvasProblemGraphical = canvasProblemGraphical;
        this.marian = marian;
        this.toggleBlockNr = toggleBlockNr;
    }
 
    /**
     * Starts the task to calculate and draw the problem
     * @return the canvas which is the problem drawn on
     */
    protected Canvas call() {
        System.out.println("Drawing problem building");
        canvasProblemGraphical = new Canvas(canvasProblemGraphical.getWidth(), canvasProblemGraphical.getHeight());

        GraphicsContext gc = canvasProblemGraphical.getGraphicsContext2D();
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        ArrayList<Block> blockCollection = marian.getBlockCollection();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
        gc.fill();
        gc.setStroke(Color.BLUE);

        int id;
        double minx;
        double maxx;
        double miny;
        double maxy;
        double height;
        double width;
        Random random = new Random();

        double gridSize = (canvasWidth / (marian.getLength()));

        for (Block block : blockCollection) {
            minx = block.getMinX();
            maxx = block.getMaxX();
            miny = block.getMinY();
            maxy = block.getMaxY();
            width = block.getWidth();
            height = block.getHeight();
            id = block.getID();

            double x = minx * gridSize;
            double y = canvasHeight - height * gridSize - (miny * gridSize);
            double breedte = width * gridSize;
            double hoogte = height * gridSize;
            int randomcolor;

            int min = 200;
            int max = 250;
            randomcolor = random.nextInt(max - min) + min;

            gc.setFill(Color.rgb(randomcolor, randomcolor, randomcolor));
            gc.fillRect(x, y, breedte - 1, hoogte - 1);

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeLine(x, y, x + breedte, y);
            gc.strokeLine(x, y, x, y + hoogte);
            gc.strokeLine(x, y + hoogte, x + breedte, y + hoogte);
            gc.strokeLine(x + breedte, y, x + breedte, y + hoogte);

            gc.setStroke(Color.WHITE);
            gc.stroke();

            if (toggleBlockNr) {
                gc.setFill(Color.BLACK);
                String text = id + "";
                if (marian.getFysicalMatrix().length < 20) {
                    gc.setFont(Font.font("BebasNeue", 15));
                    if (text.length() == 1) {
                        gc.fillText(Integer.toString(id), (x + breedte / 2) - 4, (y + hoogte / 2) + 5);
                    } else if(text.length() == 2){
                        gc.fillText(Integer.toString(id), (x + breedte / 2)- 6, (y + hoogte / 2) + 5);                                
                    } else {
                        gc.fillText(Integer.toString(id), (x + breedte / 2)- 10, (y + hoogte / 2) + 5);                                                                
                    }
                } else {
                    gc.setFont(Font.font("BebasNeue", 8));
                    if (text.length() == 1) {
                        gc.fillText(Integer.toString(id), (x + breedte / 2) - 4, (y + hoogte / 2)+3);
                    } else if (text.length() == 2) {
                        gc.fillText(Integer.toString(id), (x + breedte / 2) - 4, (y + hoogte / 2)+3);
                    } else {
                        gc.fillText(Integer.toString(id), (x + breedte / 2) - 5, (y + hoogte / 2)+3);
                    }
                }
            }
            updateValue(canvasProblemGraphical);

        }
        return canvasProblemGraphical;
    }
    
}
