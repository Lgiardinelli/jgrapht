package org.jgrapht.alg.planar;

public class OrientDfsStackInfo<V, E> {
    private V current;
    private V parent;
    private E parentEdge;
    private boolean backtrack;

    public OrientDfsStackInfo(V current, V parent, E parentEdge, boolean backtrack) {
        this.current = current;
        this.parent = parent;
        this.parentEdge = parentEdge;
        this.backtrack = backtrack;
    }

    public V getCurrent() {
        return current;
    }

    public V getParent() {
        return parent;
    }

    public E getParentEdge() {
        return parentEdge;
    }

    public boolean isBacktrack() {
        return backtrack;
    }
}
