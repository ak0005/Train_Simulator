package sample;

import javafx.scene.paint.Color;

import java.util.*;


class vertex {
    Vector<edge> InList, OutList;
    Color color;
    private String name;
    private double xCoordinate;
    private double yCoordinate;
    private int degree;

    vertex(String name, Color c, double xCoordinate, double yCoordinate) {
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        InList = new Vector<edge>();
        OutList = new Vector<edge>();
        this.degree = 0;
        this.color = c;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double d) {
        xCoordinate = d;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double d) {
        yCoordinate = d;
    }

    void update(String name, double xCoordinate, double yCoordinate) {
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    int addInList(edge e) {
        InList.add(e);
        return (InList.size() - 1);
    }

    int addOutList(edge e) {
        OutList.add(e);
        degree++;
        return (OutList.size() - 1);
    }

    void updateInList(double weight, double fromx, double fromy, double tox, double toy, int i) {
        InList.get(i).update(fromx, fromy, tox, toy, weight);
    }

    void updateOutList(double weight, double fromx, double fromy, double tox, double toy, int i) {
        OutList.get(i).update(fromx, fromy, tox, toy, weight);
    }

    void deleteInList(int i) {
        InList.set(i, null);
    }

    void deleteOutList(int i) {
        OutList.set(i, null);
    }

    String print(int i) {
        return (OutList.get(i).toString());
    }

    String getName() {
        return (name);
    }

    public String toString() {
        if (color == null)
            return (name + " " + xCoordinate + " " + yCoordinate);
        else return (name + " " + xCoordinate + " " + yCoordinate + " " + color.toString());
    }

    Color getColor() {
        return color;
    }
}

class sortVertex implements Comparator<vertex> {
    public int compare(vertex a, vertex b) {
        if (a == null && b == null) {
            return (0);
        }
        if (a == null) return (-1);
        if (b == null) return (1);

        return (a.getName().compareTo(b.getName()));
    }
}


class edge {
    private String from, to;
    private double tox, toy;
    private double fromx, fromy;
    private double weight;

    edge(String from, String to, double fromx, double fromy, double tox, double toy, double weight) {
        this.to = to;
        this.from = from;
        this.weight = weight;
        this.fromx = fromx;
        this.fromy = fromy;
        this.tox = tox;
        this.toy = toy;
    }

    void update(double fromx, double fromy, double tox, double toy, double weight) {
        this.fromx = fromx;
        this.fromy = fromy;
        this.tox = tox;
        this.toy = toy;
        this.weight = weight;
    }

    String getTo() {
        return (to);
    }

    public void setTo(String s2) {
        to = s2;
    }

    String getFrom() {
        return (from);
    }

    public void setFrom(String s2) {
        from = s2;
    }

    public double getWeight() {
        return (weight);
    }

    public String toString() {
        return (from + " " + to + " " + weight);
    }

    public double getFromx() {
        return fromx;
    }

    public void setFromx(double a) {
        fromx = a;
    }

    public double getFromy() {
        return fromy;
    }

    public void setFromy(double b) {
        fromy = b;
    }

    public double getTox() {
        return tox;
    }

    public void setTox(double a) {
        tox = a;
    }

    public double getToy() {
        return toy;
    }

    public void setToy(double b) {
        toy = b;
    }
}

class sortEdge implements Comparator<edge> {
    public int compare(edge a, edge b) {
        if (a == null && b == null) {
            return (0);
        }
        if (a == null) return (-1);
        if (b == null) return (1);

        if (a.getFrom().equals(b.getFrom())) {
            return (a.getTo().compareTo(b.getTo()));
        } else return (a.getFrom().compareTo(b.getFrom()));
    }
}


public class graph {
    private int nodeCount;
    private HashMap<String, Integer> map;
    private HashMap<String, Pair> map2;

    private Vector<vertex> g;

    graph() {
        nodeCount = 0;
        g = new Vector<vertex>();
        map = new HashMap<String, Integer>();
        map2 = new HashMap<String, Pair>();
    }

    int getNodeCount() {
        return (nodeCount);
    }

    vertex addVertex(String s, Color c, double a, double b) throws custException {
        if (map.containsKey(s)) {
            throw (new custException("Vertex named " + s + " is already present"));
        }
        map.put(s, g.size());
        vertex v = new vertex(s, c, a, b);
        g.add(v);
        nodeCount++;
        return (v);
    }

    void deleteVertex(String s) throws custException {
        if (!map.containsKey(s))
            throw (new custException("No vertex " + s + " found to delete"));

        vertex v = g.get(map.get(s));

        for (edge e : v.InList) {
            if (e != null)
                deleteEdge(e.getFrom(), e.getTo());
        }
        for (edge e : v.OutList) {
            if (e != null)
                deleteEdge(e.getFrom(), e.getTo());
        }

        g.set(map.get(s), null);
        map.remove(s);
        nodeCount--;
    }

    void modifyVertex(String s, String s2, double a, double b) throws custException {
        if (!map.containsKey(s))
            throw (new custException("No vertex " + s + " found to modify"));
        if (map.containsKey(s2) && (!s2.equals(s)))
            throw (new custException("Vertex named " + s2 + " is already present"));

        int k = map.get(s);
        g.get(k).update(s2, a, b);
        map.remove(s);
        map.put(s2, k);
        vertex v = g.get(k);
        for (edge e : v.OutList) {
            if (e != null) {
                e.setFromx((int) a);
                e.setFromy((int) b);
                e.setFrom(s2);
            }
        }
        for (edge e : v.InList) {
            if (e != null) {
                e.setTox((int) a);
                e.setToy((int) b);
                e.setTo(s2);
            }
        }
    }

    String searchVertex(String s) throws custException {
        if (map.containsKey(s))
            return (g.get(map.get(s)).toString());
        return (null);
    }

    edge addEdge(String from, String to, double weight) throws custException {
        String temp = from + " " + to;
        if (map2.containsKey(temp)) {
            throw (new custException("Edge between " + from + " and " + to + " is already present, use modify"));
        }
        if ((!map.containsKey(to)) || (!map.containsKey(from))) {
            throw (new custException("either of both of " + to + " and " + from + " vertex is not found"));
        }
        int a = map.get(from), b = map.get(to);
        vertex a2 = g.get(a), b2 = g.get(b);
        edge e = new edge(from, to, (int) a2.getxCoordinate(), (int) a2.getyCoordinate(), (int) b2.getxCoordinate(), (int) b2.getyCoordinate(), weight);
        a = a2.addOutList(e);
        b = b2.addInList(e);
        map2.put(temp, new Pair(a, b));
        return e;
    }

    void modifyEdge(String from, String to, double weight) throws custException {
        String temp = from + " " + to;
        if (!map2.containsKey(temp)) {
            throw (new custException("No edge between " + from + " and " + to + " is already present"));
        }
        if ((!map.containsKey(to)) || (!map.containsKey(from))) {
            throw (new custException("either of both of " + to + " and " + from + " vertex is not found (check if their names got changed or not"));
        }

        int a = map.get(from), b = map.get(to);
        vertex a2 = g.get(a), b2 = g.get(b);
        Pair p = map2.get(temp);
        a2.updateOutList(weight, (int) a2.getxCoordinate(), (int) a2.getyCoordinate(), (int) b2.getxCoordinate(), (int) b2.getyCoordinate(), p.getKey());
        b2.updateInList(weight, (int) a2.getxCoordinate(), (int) a2.getyCoordinate(), (int) b2.getxCoordinate(), (int) b2.getyCoordinate(), p.getValue());

    }


    void deleteEdge(String from, String to) throws custException {
        String temp = from + " " + to;
        if (!map2.containsKey(temp)) {
            throw (new custException("No edge between " + from + " and " + to + " is already present, use modify"));
        }
        if ((!map.containsKey(to)) || (!map.containsKey(from))) {
            throw (new custException("either of both of " + to + " and " + from + " vertex is not found (check if their names got changed or not"));
        }

        int a = map.get(from), b = map.get(to);
        vertex a2 = g.get(a), b2 = g.get(b);
        Pair p = map2.get(temp);
        a2.deleteOutList(p.getKey());
        b2.deleteInList(p.getValue());
        map2.remove(temp);
    }

    String searchEdge(String from, String to) {
        String temp = from + " " + to;
        if (!map2.containsKey(temp)) {
            return (null);
        }
        int a = map.get(from);
        vertex a2 = g.get(a);
        Pair p = map2.get(temp);
        return (a2.print(p.getKey()));
    }

    void sort() {
        map.clear();
        map2.clear();
        for (vertex x : g) {
            if (x != null) {
                Collections.sort(x.InList, new sortEdge());
                Collections.sort(x.OutList, new sortEdge());
            }
        }
        Collections.sort(g, new sortVertex());
        int k = 0, i = 0;
        for (vertex x : g) {
            if (x == null) {
                i++;
                continue;
            }
            map.put(x.getName(), k++);

            int k2 = 0, i2 = 0;
            for (edge e : x.InList) {
                if (e == null) {
                    i2++;
                    continue;
                }
                String temp = e.getFrom() + " " + e.getTo();

                if (map2.containsKey(temp)) {
                    map2.get(temp).setValue(k2++);
                } else
                    map2.put(e.getFrom() + " " + e.getTo(), new Pair(0, k2++));
            }

            while ((i2--) > 0) {
                x.InList.remove(0);
            }

            k2 = 0;
            i2 = 0;
            for (edge e : x.OutList) {
                if (e == null) {
                    i2++;
                    continue;
                }
                String temp = e.getFrom() + " " + e.getTo();

                if (map2.containsKey(temp)) {
                    map2.get(temp).setKey(k2++);
                } else
                    map2.put(e.getFrom() + " " + e.getTo(), new Pair(k2++, 0));
            }

            while ((i2--) > 0) {
                x.OutList.remove(0);
            }
        }

        while ((i--) > 0) {
            g.remove(0);
        }
    }

    String print() {
        sort();
        String s = "" + nodeCount;
        TreeSet<edge> t = new TreeSet<edge>(new sortEdge());
        for (vertex v : g) {
            s = s + "\n" + v.toString();
            for (edge e : v.InList) {
                t.add(e);
            }
            for (edge e : v.OutList) {
                t.add(e);
            }
        }
        s += "\n" + t.size();
        for (edge e : t)
            s += "\n" + e.toString();
        return (s);
    }

    HashSet<Object> getGraph() {
        sort();
        HashSet<Object> h = new HashSet<Object>();
        for (vertex v : g) {
            if (v == null) continue;
            h.add(v);
            for (edge e : v.InList) {
                if (e == null) continue;
                h.add(e);
            }
            for (edge e : v.OutList) {
                if (e == null) continue;
                h.add(e);
            }
        }
        return h;
    }

    String dijk(String start, String dest) throws Exception {
        //print();
        if (!map.containsKey(start))
            throw (new custException("No vertices named " + start + " is found"));
        if (!map.containsKey(dest))
            throw (new custException("No vertices named " + dest + " is found"));

        sort();

        int v = map.get(start);
        double costArr[] = new double[nodeCount + 2];
        int parent[] = new int[nodeCount + 2];
        boolean vis[] = new boolean[nodeCount + 2];

        Arrays.fill(costArr, 1e100);

        costArr[v] = 0;
        parent[v] = -1;
        parent[map.get(dest)] = -1;

        PriorityQueue<Pair2> queue = new PriorityQueue<Pair2>();
        queue.add(new Pair2(v, costArr[v]));

        while (queue.size() != 0) {

            Pair2 temp = queue.remove();
            System.out.println(g.get(temp.getKey()).getName() + "+");
            if (vis[temp.getKey()] != false) continue;
            vis[temp.getKey()] = true;

            int v2 = temp.getKey();
            ;
            if (g.get(v2).getName().equals(dest))
                break;
            System.out.println(g.get(temp.getKey()).getName() + "-");

            for (edge e : g.get(v2).OutList) {
                int k = map.get(e.getTo());

                if (vis[k] == false) {
                    System.out.println(g.get(k).getName() + "_");
                    if (costArr[k] - e.getWeight() - temp.getValue() > 0) {
                        System.out.println(g.get(k).getName() + "__");
                        parent[k] = v2;
                        costArr[k] = e.getWeight() + temp.getValue();
                        queue.add(new Pair2(k, costArr[k]));
                    }
                }
            }

        }

        int k = map.get(dest);
        if (parent[k] == -1)
            return ("No path exist");
        String s = "";
        while (k != -1) {
            s = g.get(k).getName() + " -> " + s;
            k = parent[k];
        }
        return (s);
    }
}
