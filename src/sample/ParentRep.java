package sample;

import javafx.scene.shape.Shape;

import java.util.List;

abstract class ParentRep {
    Shape shape;

    abstract void delete(graph g, List<ParentRep> l) throws custException;

    abstract public String toString();
}
