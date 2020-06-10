package sample;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import javax.swing.*;
import java.util.List;
import java.util.Vector;

public class CustAnchorPane extends AnchorPane {
    static int a = 0;
    List<ParentRep> shapes = new Vector<ParentRep>();
    graph g = new graph();
    Line l;
    boolean isDirty = false;
    Label label = null;

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (label == null) {
            for (Node x : this.getChildren()) {
                if (x instanceof Label) {
                    label = (Label) x;
                    break;
                }
            }
        }
        if (isDirty) {
            Vector<Shape> train = new Vector<>();
            Vector<Shape> stoppage = new Vector<>();
            Vector<Shape> track = new Vector<>();

            this.getChildren().clear();
            for (ParentRep x : shapes) {
                if (x instanceof TrackRep)
                    track.add(x.shape);
                else if (x instanceof StoppageRep)
                    stoppage.add(x.shape);
                else train.add(x.shape);
            }


            for (Shape s : train)
                this.getChildren().add(s);
            for (Shape s : track)
                this.getChildren().add(s);
            for (Shape s : stoppage)
                this.getChildren().add(s);
            this.getChildren().add(label);

            isDirty = false;
        }
    }

    Circle add(double x, double y) throws custException {
        String id;
        boolean type = true;
        Color c2 = null;

        try {
            id = JOptionPane.showInputDialog("Enter the id for stoppage u r trying to create at {" + x + "," + y + "}");
            if (id == null) throw new Exception();
        } catch (Exception e) {
            throw new custException("unable to find valid id");
        }
        try {
            String lst[] = {"Platform", "Other"};
            String temp = (String) JOptionPane.showInputDialog(null, "Pick the type of stoppage", "Choose the type", JOptionPane.PLAIN_MESSAGE, null, lst, lst[0]);
            if (temp.equals("Other"))
                type = false;
        } catch (Exception e) {
            throw new custException("Unable to get the type of stoppage");
        }

        if (type)
            try {
                java.awt.Color temp = JColorChooser.showDialog(null, "Choose a color", java.awt.Color.RED);
                if (temp == null)
                    throw new Exception();
                c2 = Color.rgb(temp.getRed(), temp.getGreen(), temp.getBlue(), (double) temp.getAlpha() / 255);
            } catch (Exception e) {
                throw new custException("Unable to get valid color");
            }

        StoppageRep st = new StoppageRep(x, y, id, c2, g, type);
        this.getChildren().add(st.shape);
        shapes.add(st);
        return (Circle) st.shape;
    }

    void add(Line l) throws custException {
        String from = "", to = "";
        for (ParentRep x : shapes) {
            if (x instanceof StoppageRep) {
                StoppageRep x2 = (StoppageRep) x;
                if (x2.cx == l.getStartX() && x2.cy == l.getStartY())
                    from = x2.id;
                else if (x2.cx == l.getEndX() && x2.cy == l.getEndY())
                    to = x2.id;
            }
        }
        JTextField fieldw = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Enter the weight for the track u r trying to create between {" + from + "," + to + "}"));
        myPanel.add(fieldw);

        double weight;
        try {
            int result = JOptionPane.showConfirmDialog(null, myPanel, "Enter the id's", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                weight = Double.parseDouble(fieldw.getText());
            } else throw new Exception();

        } catch (Exception e) {
            throw new custException("unable to find valid weight");
        }
        TrackRep st = new TrackRep(l, from, to, weight, g);
        shapes.add(st);
    }


    void edit(Circle c, double x, double y) {
        for (ParentRep z : shapes)
            if (z.shape.equals(c)) {
                ((StoppageRep) z).edit(x, y, shapes);
                return;
            }
        isDirty = true;
    }


    void editLine(Line l, double x, double y) {
        l.setEndX(x);
        l.setEndY(y);
    }

    void addLine(Line l) {
        this.getChildren().add(l);
    }

    void remove(Node s) throws custException {
        for (ParentRep z : shapes)
            if (z.shape.equals(s)) {
                z.delete(g, shapes);
                break;
            }
        this.getChildren().remove(s);
        isDirty = true;
    }

    String detail(Node selected) {
        for (ParentRep x : shapes) {
            if (x.shape.equals(selected)) {
                return (x.toString());
            }
        }
        return ("No Details found");
    }

    boolean addTrain(String idt, String time) throws custException {
        String ids;
        StoppageRep st = null;
        Color c2;
        JTextField idsf = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Enter the id for first stoppage of train (" + idt + ") :"));
        myPanel.add(idsf);

        try {
            int result = JOptionPane.showConfirmDialog(null, myPanel, "Enter the id's", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                ids = idsf.getText();
            } else return false;

            for (ParentRep x : shapes) {
                if (x instanceof StoppageRep) {
                    if (((StoppageRep) x).id.equals(ids)) {
                        st = (StoppageRep) x;
                        break;
                    }
                }
            }
            if (st == null)
                throw new Exception();

        } catch (Exception e) {
            throw new custException("unable to get valid id");
        }

        try {
            java.awt.Color c = JColorChooser.showDialog(null, "Choose a color", java.awt.Color.RED);
            if (c == null)
                throw new Exception();
            c2 = Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), (double) c.getAlpha() / 255);
        } catch (Exception e) {
            throw new custException("Unable to get valid color");
        }

        TrainRep tr = new TrainRep(st, c2, idt, time);
        shapes.add(tr);
        this.getChildren().add(tr.shape);
        isDirty = true;
        layoutChildren();

        return (true);
    }

    void departTrain(Shape selected, String time) throws custException {
        TrainRep trainRep = null;
        for (ParentRep x : shapes) {
            if (x.shape.equals(selected)) {
                trainRep = (TrainRep) x;
            }
        }
        if (trainRep == null) throw new custException("Train not found");

        String[] lst = new String[trainRep.stoppage.v.OutList.size()];
        int i = 0;
        for (edge e : trainRep.stoppage.v.OutList) {
            lst[i++] = e.getTo();
        }
        if (i == 0)
            throw new custException("no place to go");

        String temp = (String) JOptionPane.showInputDialog(null, "Pick the destination", "MOVE", JOptionPane.PLAIN_MESSAGE, null, lst, lst[0]);
        if (temp == null) return;

        StoppageRep st = null;
        TrackRep tr = null;

        for (ParentRep x : shapes) {
            if (x instanceof StoppageRep) {
                if (((StoppageRep) x).id.equals(temp)) {
                    st = ((StoppageRep) x);
                }
            } else if (x instanceof TrackRep) {
                if (trainRep.stoppage.id.equals(((TrackRep) x).from) && temp.equals(((TrackRep) x).to))
                    tr = (TrackRep) x;
            }
        }
        trainRep.depart(st, time, tr.e.getWeight());

        shapes.remove(trainRep);
        isDirty = true;
        layoutChildren();
        return;
    }

    void reverseTrain(Shape selected) {
        for (ParentRep p : shapes) {
            if (p.shape.equals(selected)) {
                ((TrainRep) p).changeDirection();
                isDirty = true;
                layoutChildren();
                return;
            }
        }
    }

    void arriveTrain(Shape selected, String time) throws custException {
        StoppageRep stoppageRep = null;
        for (ParentRep x : shapes) {
            if (x.shape.equals(selected)) {
                stoppageRep = (StoppageRep) x;
            }
        }
        if (stoppageRep == null) throw new custException("stoppage not found");

        if (stoppageRep.getTrain() != null)
            throw new custException("two train can't stand at same stoppage at same time ");

        String[] lst = new String[stoppageRep.arriving.size()];
        int i = 0;
        for (TrainRep t : stoppageRep.arriving) {
            lst[i++] = t.id;
        }
        if (i == 0)
            throw new custException("no trains waiting for arrival");

        String temp = (String) JOptionPane.showInputDialog(null, "Pick the train", "MOVE", JOptionPane.PLAIN_MESSAGE, null, lst, lst[0]);
        if (temp == null) return;

        TrainRep trainRep = null;
        for (TrainRep t : stoppageRep.arriving) {
            if (t.id.equals(temp)) {
                trainRep = t;
                break;
            }
        }

        trainRep.arrive(stoppageRep, time);

        shapes.add(trainRep);
        isDirty = true;
        layoutChildren();
    }

    public String getTrainId(Shape selected) {
        for (ParentRep p : shapes)
            if (p.shape.equals(selected)) return ((TrainRep) p).id;
        return null;
    }

    String print() {
        return g.print();
    }

    void addStoppage(String line) throws Exception {
        String arr[] = line.split(" ", -1);
        Color c = null;
        if (arr.length == 4)
            c = Color.web(arr[3]);
        StoppageRep st = new StoppageRep(Double.parseDouble(arr[1]), Double.parseDouble(arr[2]), arr[0], c, g, (arr.length == 4));
        this.getChildren().add(st.shape);
        shapes.add(st);
    }

    void addTrack(String line) throws custException {
        String arr[] = line.split(" ", -1);
        double weight = Double.parseDouble(arr[2]);

        l = new Line();
        TrackRep st = new TrackRep(l, arr[0], arr[1], weight, g);
        this.getChildren().add(st.shape);
        shapes.add(st);
    }

    void reset() {
        g = new graph();
        shapes.clear();
        isDirty = true;
        layoutChildren();
    }
}
