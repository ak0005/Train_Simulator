package sample;

import javafx.scene.shape.Line;

import java.util.List;

public class TrackRep extends ParentRep {
    double sx, sy, ex, ey;
    String to, from;
    edge e;

    TrackRep(Line l, String from, String to, double weight, graph g) throws custException {
        shape = l;
        e = g.addEdge(from, to, weight);
        sx = e.getFromx();
        sy = e.getFromy();
        ex = e.getTox();
        ey = e.getToy();
        this.from = from;
        this.to = to;
        editS(sx, sy);
        editE(ex, ey);
    }

    @Override
    void delete(graph g, List<ParentRep> l) throws custException {
        l.remove(this);
        g.deleteEdge(from, to);
        e = null;
    }

    @Override
    public String toString() {
        return ("from " + from + "  at\n" + "X Coordinate :- " + sx + "\nY Coordinate :- " + sy + "\n\n To " + to + " at\n" + "X Coordinate :- " + ex + "\nY Coordinate :- " + ey + "\n\nWeight:- " + e.getWeight());
    }

    void editS(double x, double y) {
        sx = x;
        sy = y;
        ((Line) shape).setStartX(x);
        ((Line) shape).setStartY(y);
        e.setFromx(x);
        e.setFromy(y);
    }

    void editE(double x, double y) {
        ex = x;
        ey = y;
        ((Line) shape).setEndX(x);
        ((Line) shape).setEndY(y);
        e.setTox(x);
        e.setToy(y);

    }
}
