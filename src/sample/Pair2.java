package sample;

public class Pair2 implements Comparable<Pair2> {
    private int a;
    private double b;

    Pair2(int a, double b) {
        this.a = a;
        this.b = b;
    }

    int getKey() {
        return (a);
    }

    void setKey(int a) {
        this.a = a;
    }

    double getValue() {
        return (b);
    }

    void setValue(double b) {
        this.b = b;
    }

    public int compareTo(Pair2 v) {
        return (int) ((b - v.getValue()) * 10000);
    }

}
