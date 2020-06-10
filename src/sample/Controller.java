package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

public class Controller {

    static private int leastTime = 60;
    static CustLineChart lineChart = new CustLineChart(new NumberAxis(), new NumberAxis(), leastTime);  //======
    HashSet<String> train = new HashSet<>();
    @FXML
    TextFlow textFlow;
    @FXML
    HBox Hbox;
    @FXML
    Label label2;
    @FXML
    CustAnchorPane custAnchorPane;
    @FXML
    Label time;
    @FXML
    ListView listView;
    String date;
    private Boolean isVertexMode = true;
    private Shape selected;
    private Color orig;

    public void initialize() {
        initClock();
        Hbox.getChildren().add(lineChart);
        HBox.setHgrow(lineChart, Priority.ALWAYS);
    }

    private void initClock() {
        String s;
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            time.setText(LocalDateTime.now().format(formatter));
            if (date == null) {
                date = LocalDateTime.now().format(formatter).substring(0, 10);
            } else if (!date.equals(LocalDateTime.now().format(formatter).substring(0, 10))) {
                date = LocalDateTime.now().format(formatter).substring(0, 10);
                Platform.runLater(() -> dateChanged());
            }

        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void dateChanged() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Current date is modified");
        alert.setContentText("Do you want to save chart before it get reset?");
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(okButton, noButton);
        alert.showAndWait().ifPresent(type -> {
            if (type.getText() == "Yes") {
                savePNG(new ActionEvent());
            }
        });

        alert.setContentText("How do you want to reset the chart before it get reset?");
        okButton = new ButtonType("Partial", ButtonBar.ButtonData.YES);
        noButton = new ButtonType("Complete", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(okButton, noButton);
        alert.showAndWait().ifPresent(type -> {
            if (type.getText() == "Partial") {
                partialReset();
            } else {
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
                alert2.setTitle("You Selected complete reset");
                alert2.setContentText("Do you want to save graph?");
                ButtonType okButton2 = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton2 = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert2.getButtonTypes().setAll(okButton2, noButton2);
                alert2.showAndWait().ifPresent(type2 -> {
                    if (type2.getText() == "Yes") {
                        save(new ActionEvent());
                    }
                });
                reset();
            }
        });
    }


    @FXML
    public void clicked(MouseEvent mouseEvent) {

        resetSelected();
        List<Node> l = custAnchorPane.getChildren();
        for (Node x : l) {
            if (x instanceof Line) {
                if (ptLineDist((Line) x, mouseEvent.getX(), mouseEvent.getY()) < 4) {
                    setSelected((Shape) x);
                    return;
                }
            } else if (x.getBoundsInParent().contains(mouseEvent.getX(), mouseEvent.getY())) {
                if (x instanceof Shape)
                    setSelected((Shape) x);
                return;
            }
        }
        if (isVertexMode) {
            Circle circle = null;
            try {
                circle = custAnchorPane.add(mouseEvent.getX(), mouseEvent.getY());
            } catch (custException e) {
                error(e.toString());
            }
            if (circle != null)
                setSelected(circle);
        }
    }

    @FXML
    public void Dragged(MouseEvent mouseEvent) {
        if (isVertexMode) {
            if (selected == null || !(selected instanceof Circle))
                return;
            custAnchorPane.edit((Circle) selected, mouseEvent.getX(), mouseEvent.getY());
            //((Circle)selected).setCenterX(mouseEvent.getX());
            //((Circle)selected).setCenterY(mouseEvent.getY());
        } else {
            if (selected == null || !(selected instanceof Line))
                return;
            custAnchorPane.editLine((Line) selected, mouseEvent.getX(), mouseEvent.getY());
            //((Line)selected).setEndX(mouseEvent.getX());
            //((Line)selected).setEndY(mouseEvent.getY());
        }
    }

    public void change(ActionEvent actionEvent) {
        resetSelected();
        if (isVertexMode) {
            isVertexMode = false;
            label2.setText("Track Mode");
        } else {
            isVertexMode = true;
            label2.setText("Stoppage Mode");
        }
    }

    public void pressed(MouseEvent mouseEvent) {
        if (!isVertexMode) {
            resetSelected();
            List<Node> l = custAnchorPane.getChildren();
            for (Node x : l) {
                if (x instanceof Circle) {
                    if (x.getBoundsInParent().contains(mouseEvent.getX(), mouseEvent.getY())) {
                        Line line = new Line(((Circle) x).getCenterX(), ((Circle) x).getCenterY(), ((Circle) x).getCenterX(), ((Circle) x).getCenterY());
                        //custAnchorPane.getChildren().add(line);
                        custAnchorPane.addLine(line);
                        setSelected(line);
                        return;
                    }
                }
            }
        }
    }

    public void Released(MouseEvent mouseEvent) {
        try {
            if (!isVertexMode && selected != null) {
                List<Node> l = custAnchorPane.getChildren();
                for (Node x : l) {
                    if (x instanceof Circle) {
                        if (x.getBoundsInParent().contains(mouseEvent.getX(), mouseEvent.getY())) {
                            if (((Line) selected).getStartX() == ((Circle) x).getCenterX() && ((Line) selected).getStartY() == ((Circle) x).getCenterY()) {
                                //removeElement(selected);//remove line
                                custAnchorPane.remove(selected);
                                resetSelected();
                                return;
                            }
                            custAnchorPane.editLine((Line) selected, ((Circle) x).getCenterX(), ((Circle) x).getCenterY());
                            custAnchorPane.add((Line) selected);
                            resetSelected();
                            return;
                        }
                    }
                }
                // removeElement(selected);//remove line
                custAnchorPane.remove(selected);
                resetSelected();
            } else resetSelected();
        } catch (custException e) {
            custAnchorPane.getChildren().remove(selected);
            resetSelected();
            error(e.toString());
        }
    }

    public void button(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE) {
            if ((!(selected instanceof Line)) && (!(selected instanceof Circle)))
                train.remove(custAnchorPane.getTrainId(selected));

            try {
                custAnchorPane.remove(selected);
            } catch (custException e) {
                error(e.toString());
            }
            resetSelected();

        } else if (keyEvent.getCode() == KeyCode.E) {
            textFlow.getChildren().clear();
            if (selected == null)
                textFlow.getChildren().add(new Text("No details found"));
            else textFlow.getChildren().add(new Text(custAnchorPane.detail(selected)));

        } else if (keyEvent.getCode() == KeyCode.D) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String time = adjustTime(LocalDateTime.now().format(formatter), leastTime);
            if ((!(selected instanceof Line)) && (!(selected instanceof Circle))) {
                try {
                    custAnchorPane.departTrain(selected, time);
                } catch (custException e) {
                    error(e.toString());
                }
            }

        } else if (keyEvent.getCode() == KeyCode.A) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String time = adjustTime(LocalDateTime.now().format(formatter), leastTime);
            if (selected instanceof Circle) {
                try {
                    custAnchorPane.arriveTrain(selected, time);
                } catch (custException e) {
                    error(e.toString());
                }
            }

        } else if (keyEvent.getCode() == KeyCode.R) {
            if ((!(selected instanceof Line)) && (!(selected instanceof Circle))) {
                custAnchorPane.reverseTrain(selected);
                resetSelected();
            }
        }
    }


    private void setSelected(Shape n) {
        if (n == null) return;
        selected = n;
        if (selected instanceof Circle) {
            orig = (Color) (selected).getFill();
            (selected).setFill(Color.RED);
        } else if (selected instanceof Line) {
            orig = (Color) (selected).getStroke();
            (selected).setStroke(Color.RED);
        } else {
            orig = (Color) (selected).getFill();
            (selected).setFill(Color.RED);
        }
    }

    private void resetSelected() {
        if (selected != null) {
            if (selected instanceof Circle) {
                (selected).setFill(orig);
                selected = null;
            } else if (selected instanceof Line) {
                (selected).setStroke(orig);
                selected = null;
            } else {
                (selected).setFill(orig);
                selected = null;
            }
        }
    }

    private void error(String s) {
        Alert a = new Alert(Alert.AlertType.ERROR, s);
        a.show();
    }

    public void addTrain(ActionEvent actionEvent) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            ObservableList selectedIndices = listView.getSelectionModel().getSelectedIndices();
            if (selectedIndices.isEmpty())
                throw new custException("Please Select a train from list");
            if (custAnchorPane.addTrain((String) listView.getItems().get((int) selectedIndices.get(0)), adjustTime(LocalDateTime.now().format(formatter), leastTime)))
                listView.getItems().remove((int) selectedIndices.get(0));
        } catch (custException e) {
            error(e.toString());
        }
    }

    public void createTrain(ActionEvent actionEvent) {
        JPanel myPanel = new JPanel();
        JTextField field = new JTextField(5);
        myPanel.add(new JLabel("Enter the id of the train :"));
        myPanel.add(field);
        String id;

        try {
            int result = JOptionPane.showConfirmDialog(null, myPanel, "Enter the id's", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                id = field.getText();
                if (id != null && (!train.contains(id))) {
                    train.add(id);
                    listView.getItems().add(id);
                } else throw new custException("unable to get valid train id");
            }
        } catch (custException e) {
            error(e.toString());
        }
    }

    private String adjustTime(String s, int leastTime) {
        return s;

    }

    public void save(ActionEvent actionEvent) {
        String s = custAnchorPane.print();
        s += "\n" + train.size();
        for (String x : train) {
            s += "\n" + x;
        }

        FileChooser fileChooser = new FileChooser();
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(Main.stage);

        if (file != null) {
            saveTextToFile(s, file);
        }
    }

    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            error("Error in writing one the file");
            // Logger.getLogger(SaveFileWithFileChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void load(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showOpenDialog(Main.stage);

        if (file != null) {
            reset();
            read(file);
        }
    }

    void read(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int v = Integer.parseInt(reader.readLine());
            while ((v--) > 0) {
                line = reader.readLine();
                if (line == null)
                    throw new Exception();
                custAnchorPane.addStoppage(line);
            }

            int e = Integer.parseInt(reader.readLine());
            while ((e--) > 0) {
                line = reader.readLine();
                if (line == null)
                    throw new Exception();
                custAnchorPane.addTrack(line);
            }

            int t = Integer.parseInt(reader.readLine());
            while ((t--) > 0) {
                line = reader.readLine();
                if (line != null && (!train.contains(line))) {
                    train.add(line);
                    listView.getItems().add(line);
                } else throw new Exception();
            }

        } catch (Exception e) {
            reset();
            e.printStackTrace();
        }
    }

    private void reset() {
        custAnchorPane.reset();
        train.clear();
        listView.getItems().clear();
        Hbox.getChildren().remove(lineChart);
        lineChart.cancel();
        lineChart = new CustLineChart(new NumberAxis(), new NumberAxis(), leastTime);
        Hbox.getChildren().add(lineChart);
        HBox.setHgrow(lineChart, Priority.ALWAYS);
    }

    private void partialReset() {
        lineChart.setTitle("Train Chart of " + date);
        for (Object x : lineChart.getData()) {
            if (x instanceof XYChart.Series) {
                ((XYChart.Data) ((XYChart.Series) x).getData().get(((XYChart.Series) x).getData().size() - 1)).setXValue(0);
                ((XYChart.Series) x).getData().remove(0, ((XYChart.Series) x).getData().size() - 1);
            }
        }
    }

    @FXML
    void savePNG(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter for text files
        fileChooser.setInitialFileName(lineChart.getTitle());
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(Main.stage);

        if (file != null) {
            WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate(ActionEvent actionEvent) {
        Main.stage.close();
    }

    public void reset2(ActionEvent actionEvent) {
        reset();
    }

    public void reset1(ActionEvent actionEvent) {
        lineChart.setTitle("Train Chart of " + date);
        for (Object x : lineChart.getData()) {
            if (x instanceof XYChart.Series) {
                ((XYChart.Series) x).getData().remove(0, ((XYChart.Series) x).getData().size() - 1);
            }
        }
    }

    double ptLineDist(Line l, double x, double y) {
        double x1 = l.getStartX(), y1 = l.getStartY(), x2 = l.getEndX(), y2 = l.getEndY();
        double A = x - x1; // position of point rel one end of line
        double B = y - y1;
        double C = x2 - x1; // vector along line
        double D = y2 - y1;
        double E = -D; // orthogonal vector
        double F = C;
        double dot = A * E + B * F;
        double len_sq = E * E + F * F;
        if (dot == 0) {
            if (Math.min(x1, x2) <= x && Math.max(x1, x2) >= x && Math.min(y1, y2) <= y && Math.max(y1, y2) >= y) {
                return 0;
            } else return 1e18;
        }
        return dot * dot / len_sq;
    }
}

