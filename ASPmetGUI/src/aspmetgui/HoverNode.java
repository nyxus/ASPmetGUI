/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aspmetgui;

import java.text.DecimalFormat;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * A node which displays a value on hover, but is otherwise empty
 * @author LAPTOPPT
 */
public class HoverNode extends StackPane {

    public HoverNode(int priorValue, String value) {
        setPrefSize(8, 8);
        
        final Label label = createDataThresholdLabel(priorValue, value);
        
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getChildren().setAll(label);
                setCursor(Cursor.CROSSHAIR);
                toFront();
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getChildren().clear();
                setCursor(Cursor.DEFAULT);
            }
        });
    }
    
    private Label createDataThresholdLabel(int priorValue, String value) {
        final Label label = new Label(value + "");
        label.getStyleClass().addAll("default-color", "chart-line-symbol", "chart-series-line");
        label.setStyle("-fx-font-size: 10; -fx-font-weight: bold;");
        label.setTranslateY(25);

        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return label;
    }
}
