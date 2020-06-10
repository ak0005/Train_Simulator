package sample;

public class custException extends Exception {
    String s;

    custException(String s) {
        this.s = s;
    }

    public String toString() {
        return (s);
    }
}