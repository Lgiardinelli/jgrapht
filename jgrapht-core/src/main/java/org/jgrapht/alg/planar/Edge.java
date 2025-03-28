package org.jgrapht.alg.planar;

public class Edge<V, E> {
    private E graphEdge;
    private Node<V, E> source;
    private Node<V, E> target;
    private int sign;
    private boolean embedded;
    private boolean shortCircuit;

    public Edge(Node<V, E> source, Node<V, E> target) {
        this(null, source, target);
        this.shortCircuit = true;
        this.embedded = true;
    }

    public Edge(E graphEdge, Node<V, E> source) {
        this(graphEdge, source, null);
    }

    public Edge(E graphEdge, Node<V, E> source, Node<V, E> target) {
        this.graphEdge = graphEdge;
        this.source = source;
        this.target = target;
        this.sign = 1;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void setTarget(Node<V, E> target) {
        this.target = target;
    }

    public Node<V, E> getSource() {
        return source;
    }

    public Node<V, E> getTarget() {
        return target;
    }

    public int getSign() {
        return sign;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public E getGraphEdge() {
        return graphEdge;
    }

    public Node<V, E> getOpposite(Node<V, E> node) {
        return source.equals(node) ? target : source;
    }

    @Override
    public String toString() {
        String formatString = shortCircuit ? "%s ~ %s" : "%s -> %s";
        return String.format(formatString, source.toString(false), target.toString(false));
    }
}
