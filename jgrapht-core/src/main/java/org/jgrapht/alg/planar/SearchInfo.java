package org.jgrapht.alg.planar;

public class SearchInfo<V, E> {
    public Node<V, E> current;
    public Edge<V, E> prevEdge;
    public boolean backtrack;

    public SearchInfo(Node<V, E> current, Edge<V, E> prevEdge, boolean backtrack) {
        this.current = current;
        this.prevEdge = prevEdge;
        this.backtrack = backtrack;
    }
}
