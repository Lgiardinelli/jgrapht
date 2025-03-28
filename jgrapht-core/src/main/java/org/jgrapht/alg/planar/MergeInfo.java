package org.jgrapht.alg.planar;

public class MergeInfo<V, E> {
    public Node<V, E> parent;
    public Node<V, E> parentNext;
    public Node<V, E> child;
    public Node<V, E> childPrev;
    public int vIn;
    public int vOut;

    public MergeInfo(Node<V, E> parent, Node<V, E> parentNext, Node<V, E> child, Node<V, E> childPrev, int vIn, int vOut) {
        this.parent = parent;
        this.parentNext = parentNext;
        this.child = child;
        this.childPrev = childPrev;
        this.vIn = vIn;
        this.vOut = vOut;
    }

    public boolean isInverted() {
        return vIn != vOut;
    }

    @Override
    public String toString() {
        return String.format("Parent dir = {%s -> %s}, child_dir = {%s -> %s}, inverted = %b, vIn = %d, vOut = %d",
                parent.toString(false), parentNext.toString(false),
                childPrev.toString(false), child.toString(false), isInverted(), vIn, vOut);
    }
}
