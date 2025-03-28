package org.jgrapht.alg.planar;

import org.jgrapht.util.TypeUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import org.jgrapht.util.DoublyLinkedList;

public class Node<V, E> {
    private V graphVertex;
    private boolean rootVertex;
    private int dfsIndex;
    private int height;
    private int lowpoint;
    private int leastAncestor;
    private int visited;
    private int backEdgeFlag;
    private int boundaryHeight;
    private boolean marked;
    private Edge<V, E> parentEdge;
    private Edge<V, E> edgeToEmbed;
    private Node<V, E> initialComponentRoot;
    private Node<V, E>[] outerFaceNeighbors;
    private DoublyLinkedList<Node<V, E>> separatedDfsChildList;
    private DoublyLinkedList<Node<V, E>> pertinentRoots;
    private List<Edge<V, E>> treeEdges;
    private List<Edge<V, E>> downEdges;
    private List<Edge<V, E>> backEdges;
    private DoublyLinkedList.ListNode<Node<V, E>> listNode;
    private DoublyLinkedList<Edge<V, E>> embedded;

    public Node(V graphVertex, int dfsIndex, int height, Node<V, E> initialComponentRoot, Edge<V, E> parentEdge) {
        this.graphVertex = graphVertex;
        this.dfsIndex = dfsIndex;
        this.parentEdge = parentEdge;
        this.rootVertex = false;
        this.height = height;
        this.embedded = new DoublyLinkedList<>();
        if (parentEdge != null) {
            embedded.add(parentEdge);
        }
        this.visited = this.backEdgeFlag = 0;
        if (!rootVertex) {
            separatedDfsChildList = new DoublyLinkedList<>();
            pertinentRoots = new DoublyLinkedList<>();
            treeEdges = new ArrayList<>();
            downEdges = new ArrayList<>();
            backEdges = new ArrayList<>();
        }
        outerFaceNeighbors = TypeUtil.uncheckedCast(Array.newInstance(Node.class, 2));
    }

    public Node(int dfsIndex, Edge<V, E> parentEdge) {
        this(null, dfsIndex, 0, null, parentEdge);
        this.rootVertex = true;
    }

    public void mergeBiconnectedComponent() {
        System.out.println("Merging biconnected component for node: " + this);
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }

    public V getGraphVertex() {
        return graphVertex;
    }

    public int getDfsIndex() {
        return dfsIndex;
    }

    public int getHeight() {
        return height;
    }

    public int getLowpoint() {
        return lowpoint;
    }

    public void setLowpoint(int lowpoint) {
        this.lowpoint = lowpoint;
    }

    public int getLeastAncestor() {
        return leastAncestor;
    }

    public void setLeastAncestor(int leastAncestor) {
        this.leastAncestor = leastAncestor;
    }

    public Edge<V, E> getParentEdge() {
        return parentEdge;
    }

    public List<Edge<V, E>> getTreeEdges() {
        return treeEdges;
    }

    public List<Edge<V, E>> getDownEdges() {
        return downEdges;
    }

    public List<Edge<V, E>> getBackEdges() {
        return backEdges;
    }

    public DoublyLinkedList<Node<V, E>> getSeparatedDfsChildList() {
        return separatedDfsChildList;
    }

    public void setListNode(DoublyLinkedList.ListNode<Node<V, E>> listNode) {
        this.listNode = listNode;
    }

    public DoublyLinkedList<Edge<V, E>> getEmbedded() {
        return embedded;
    }

    public Node<V, E> getInitialComponentRoot() {
        return initialComponentRoot;
    }

    public void setOuterFaceNeighbors(Node<V, E> first, Node<V, E> second) {
        this.outerFaceNeighbors[0] = first;
        this.outerFaceNeighbors[1] = second;
    }

    // Méthodes pour manipuler la frontière, remplacer des voisins, etc.
    public void swapNeighbors() {
        Node<V, E> t = outerFaceNeighbors[0];
        outerFaceNeighbors[0] = outerFaceNeighbors[1];
        outerFaceNeighbors[1] = t;
    }

    public void substitute(Node<V, E> node, Node<V, E> newNeighbor) {
        if (outerFaceNeighbors[0] == node) {
            outerFaceNeighbors[0] = newNeighbor;
        } else {
            outerFaceNeighbors[1] = newNeighbor;
        }
    }

    public Node<V, E> nextOnOuterFace(Node<V, E> prev) {
        if (outerFaceNeighbors[0] == prev) {
            return outerFaceNeighbors[1];
        } else {
            return outerFaceNeighbors[0];
        }
    }

    public void embedBackEdge(Edge<V, E> edge, Node<V, E> prev) {
        if (prev.isRootVertex()) {
            prev = prev.getParent();
        }
        Edge<V, E> firstEdge = embedded.getFirst();
        if (firstEdge.getOpposite(this) == prev) {
            embedded.addFirst(edge);
        } else {
            embedded.addLast(edge);
        }
    }

    public boolean isRootVertex() {
        return rootVertex;
    }

    public Node<V, E> getParent() {
        return parentEdge == null ? null : parentEdge.getSource();
    }

    public String toString(boolean full) {
        if (!full) {
            if (rootVertex && parentEdge != null) {
                return String.format("%s^%s", parentEdge.getSource().getGraphVertex(), parentEdge.getTarget().getGraphVertex());
            }
            return graphVertex.toString();
        } else {
            return "Node: " + graphVertex + " [dfs=" + dfsIndex + ", height=" + height + "]";
        }
    }

    @Override
    public String toString() {
        return toString(false);
    }
}
