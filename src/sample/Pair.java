package sample;

public class Pair {
    int a, b;

    Pair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    int getKey() {
        return (a);
    }

    void setKey(int a) {
        this.a = a;
    }

    int getValue() {
        return (b);
    }

    void setValue(int b) {
        this.b = b;
    }
}
