package com.bsu.klimansky.model;

/**
 * Created by Anton Klimansky on 5/19/2017.
 */
public class Edge {
    public int p1;
    public int p2;

    public Edge(int p1, int p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (p1 != edge.p1) return false;
        return p2 == edge.p2;
    }

    @Override
    public int hashCode() {
        int result = p1;
        result = 31 * result + p2;
        return result;
    }
}
