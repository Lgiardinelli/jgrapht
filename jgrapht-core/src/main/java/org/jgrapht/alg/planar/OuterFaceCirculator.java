package org.jgrapht.alg.planar;

import java.util.Iterator;

public class OuterFaceCirculator<V, E> implements Iterator<Node<V, E>> {
    private Node<V, E> current;
    private Node<V, E> prev;

    public OuterFaceCirculator(Node<V, E> current, Node<V, E> prev) {
        this.current = current;
        this.prev = prev;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Node<V, E> next() {
        Node<V, E> t = current;
        current = current.nextOnOuterFace(prev);
        prev = t;
        return prev;
    }

    public Edge<V, E> edgeToNext() {
        Edge<V, E> edge = prev.getEmbedded().getFirst();
        Node<V, E> target = toExistingNode(current);
        Node<V, E> source = toExistingNode(prev);
        if (edge.getOpposite(source).equals(target)) {
            return edge;
        } else {
            return prev.getEmbedded().getLast();
        }
    }

    public Node<V, E> getCurrent() {
        return prev;
    }

    public Node<V, E> getPrev() {
        return prev.nextOnOuterFace(current);
    }

    private Node<V, E> toExistingNode(Node<V, E> node) {
        return node.isRootVertex() ? node.getParent() : node;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", prev.toString(false), current.toString(false));
    }
}
