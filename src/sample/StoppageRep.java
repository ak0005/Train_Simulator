package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class StoppageRep extends ParentRep {
    double cx, cy;
    String id;
    vertex v;
    Vector<TrainRep> arriving = new Vector<>();
    Color c;
    private TrainRep train = null;
    private boolean type;

    StoppageRep(double x, double y, String id, Color c2, graph g, boolean type) throws custException {
        shape = new Circle(7.5);
        ;
        cx = x;
        cy = y;
        ((Circle) shape).setCenterX(cx);
        ((Circle) shape).setCenterY(cy);
        v = g.addVertex(id, c2, x, y);
        this.id = id;
        this.type = type;
        this.c = c2;
        if (!type) {
            c = Color.DARKGRAY;
        }
        shape.setFill(c);
    }

    void edit(double x, double y, List<ParentRep> shapes) {
        for (ParentRep s : shapes) {
            if (s instanceof TrackRep) {
                if (((TrackRep) s).from.equals(v.getName())) {
                    ((TrackRep) s).editS(x, y);
                } else if (((TrackRep) s).to.equals(v.getName())) {
                    ((TrackRep) s).editE(x, y);
                }
            }
        }
        cx = x;
        cy = y;
        ((Circle) (shape)).setCenterX(x);
        ((Circle) (shape)).setCenterY(y);
        v.setxCoordinate(x);
        v.setyCoordinate(y);
        if (train != null)
            train.refresh();
    }

    @Override
    void delete(graph g, List<ParentRep> shapes) throws custException {
        int i = 0;
        for (ParentRep s : shapes) {
            if (s instanceof TrackRep) {
                if (((TrackRep) s).from.equals(v.getName()) || ((TrackRep) s).to.equals(v.getName())) {
                    shapes.set(i, null);
                }
            }
            i++;
        }
        shapes.removeAll(Collections.singleton(null));
        shapes.remove(this);
        g.deleteVertex(v.getName());
    }

    @Override
    public String toString() {
        return ("X Coordinate :- " + cx + "\nY Coordinate :- " + cy + "\nID :- " + id);
    }

    void arrive(TrainRep trainRep) {
        arriving.remove(trainRep);
        train = trainRep;
    }

    void depart() {
        train = null;
    }

    TrainRep getTrain() {
        return train;
    }

    void addArriving(TrainRep trainRep) {
        arriving.add(trainRep);
    }
}
