package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.List;

public class TrainRep extends ParentRep {

    StoppageRep stoppage;
    Shape forward, backward;
    Color c;
    String id;
    XYChart.Series series;
    ObservableList<XYChart.Data<Integer, Integer>> list = FXCollections.observableArrayList();
    double c_weight = 0;
    int d_weight = 1;

    TrainRep(StoppageRep s, Color c, String idt, String time) throws custException {
        if (s.getTrain() != null)
            throw new custException("Two trains can't stand in same platform at same time");
        this.id = idt;
        this.c = c;
        this.stoppage = s;

        forward = createTrain(s.cx, s.cy, true);
        backward = createTrain(s.cx, s.cy, false);
        shape = forward;

        series = new XYChart.Series(list);
        series.setName(id);

        Controller.lineChart.getData().add(series);
        list.add(createDataPoint(time, c_weight, stoppage.id, stoppage.c));

        s.arrive(this);
    }

    private XYChart.Data<Integer, Integer> createDataPoint(String time, double c_weight, String id, Color c) {
        int timeInSec = timeInSec(time);
        XYChart.Data temp = new XYChart.Data(timeInSec, c_weight);
        temp.setNode(new DynamicLabel(time, id, c));
        return temp;
    }

    Shape createTrain(double x, double y, boolean val) {
        Rectangle r = new Rectangle(15, 15);
        javafx.scene.shape.Polygon p = new javafx.scene.shape.Polygon();
        if (val)
            p.getPoints().addAll(15.0, 15.0, 15.0, 0.0, 22.5, 7.5);
        else p.getPoints().addAll(0.0, 15.0, 0.0, 0.0, -7.5, 7.5);
        javafx.scene.shape.Shape union = Shape.union(r, p);
        union.setStroke(Color.BLACK);
        union.setFill(c);
        union.setTranslateX(x);
        union.setTranslateY(y);
        return union;
    }

    @Override
    void delete(graph g, List<ParentRep> l) throws custException {
        Controller.lineChart.getData().remove(series);
        stoppage.depart();
        l.remove(this);
    }

    @Override
    public String toString() {
        return ("Standing on :- " + stoppage.id + "\n With Color :- " + c + "\nid :- " + id);
    }

    void refresh() {
        shape.setTranslateX(stoppage.cx);
        shape.setTranslateY(stoppage.cy);
    }

    void depart(StoppageRep st, String time, Double weight) {
        list.add(createDataPoint(time, c_weight, stoppage.id, stoppage.c));
        stoppage.depart();
        stoppage = null;
        st.addArriving(this);
        c_weight += d_weight * weight;
    }

    void arrive(StoppageRep st, String time) throws custException {
        if (stoppage != null)
            throw new custException("Error train is already standing at " + stoppage.toString());
        stoppage = st;
        stoppage.arrive(this);
        list.add(createDataPoint(time, c_weight, stoppage.id, stoppage.c));
        shape.setTranslateX(stoppage.cx);
        shape.setTranslateY(stoppage.cy);
    }

    void changeDirection() {
        if (d_weight == 1) {
            backward.setTranslateX(stoppage.cx);
            backward.setTranslateY(stoppage.cy);
            shape = backward;
            d_weight = -1;
        } else {
            forward.setTranslateX(stoppage.cx);
            forward.setTranslateY(stoppage.cy);
            shape = forward;
            d_weight = 1;
        }
    }

    private int timeInSec(String s) {
        String arr[] = s.split(":", -2);
        int p1 = Integer.parseInt(arr[0]);
        int p2 = Integer.parseInt((arr[1]));
        int p3 = Integer.parseInt(arr[2]);
        return (p1 * 60 * 60 + p2 * 60 + p3);
    }
}
