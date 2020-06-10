package sample;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

class DynamicLabel extends StackPane {
    DynamicLabel(String time, String id, Color c) {
        final Label label2 = createDataThresholdLabel(id, c);
        final Label label1 = createDataThresholdLabel(time, c);
        //this.setBackground(new Background(c, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setStyle("-fx-background-color: #" + c.toString().substring(2, 8));
        setPrefSize(12, 12);

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getChildren().remove(label1);
                getChildren().remove(label2);
                getChildren().add(label1);
                setCursor(Cursor.NONE);
                toFront();
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getChildren().remove(label1);
                getChildren().remove(label2);
            }
        });
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getChildren().remove(label1);
                getChildren().remove(label2);
                getChildren().add(label2);
                setCursor(Cursor.NONE);
                toFront();
            }
        });
    }

    private Label createDataThresholdLabel(String string, Color c) {
        final Label label = new Label(string + "");
        label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
        label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        label.setTextFill(c);
        //label.setStyle("-fx-border-color: RED;");

        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return label;
    }
}
