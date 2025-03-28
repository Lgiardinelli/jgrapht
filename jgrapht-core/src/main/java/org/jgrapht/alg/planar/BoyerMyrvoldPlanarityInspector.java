/*
 * (C) Copyright 2018-2023, by Timofey Chudakov and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.alg.planar;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

/**
 * An implementation of the Boyer-Myrvold planarity testing algorithm. This class determines whether
 * an input graph is planar or not. If the graph is planar, the algorithm provides a
 * <a href="https://en.wikipedia.org/wiki/Graph_embedding#Combinatorial_embedding">combinatorial
 * embedding</a> of the graph, which is represented as a clockwise ordering of the edges of the
 * graph. Otherwise, the algorithm provides a
 * <a href="https://en.wikipedia.org/wiki/Kuratowski%27s_theorem#Kuratowski_subgraphs"> Kuratowski
 * subgraph</a> as a certificate. Both embedding of the graph and Kuratowski subdivision are
 * computed lazily, meaning that the call to the {@link BoyerMyrvoldPlanarityInspector#isPlanar()}
 * does spend time only on the planarity testing. All of the operations of this algorithm (testing,
 * embedding and Kuratowski subgraph extraction) run in linear time.
 * <p>
 * A <a href="https://en.wikipedia.org/wiki/Planar_graph">planar graph</a> is a graph, which can be
 * drawn in two-dimensional space without any of its edges crossing. According to the
 * <a href="https://en.wikipedia.org/wiki/Kuratowski%27s_theorem">Kuratowski theorem</a>, a graph is
 * planar if and only if it doesn't contain a subdivision of the $K_{3,3}$ or $K_{5}$ graphs.
 * <p>
 * The Boyer-Myrvold planarity testing algorithm was originally described in: <i>Boyer, John amp;
 * Myrvold, Wendy. (2004). On the Cutting Edge: Simplified O(n) Planarity by Edge Addition. J. Graph
 * Algorithms Appl.. 8. 241-273. 10.7155/jgaa.00091. </i>. We refer to this paper for the complete
 * description of the Boyer-Myrvold algorithm
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * @author Timofey Chudakov
 */
public class BoyerMyrvoldPlanarityInspector<V, E>
    implements PlanarityTestingAlgorithm<V, E>
{
    /**
     * Whether to print debug messages
     */
    private static final boolean DEBUG = false;
    private Graph<V, E> graph;
    private int n;
    private Embedding<V, E> embedding;
    private Graph<V, E> kuratowskiSubdivision;
    private List<Node<V, E>> nodes;
    private List<Node<V, E>> dfsTreeRoots;
    private List<Node<V, E>> componentRoots;
    private List<MergeInfo<V, E>> stack;
    private Node<V, E> failedV;
    private boolean tested;
    private boolean planar;

    /**
     * Creates new instance of the planarity testing algorithm for the {@code graph}. The input
     * graph can't be null.
     *
     * @param graph the graph to test the planarity of
     */
    public BoyerMyrvoldPlanarityInspector(Graph<V, E> graph)
    {
        this.graph = Objects.requireNonNull(graph, "Graph can't be null");
        this.n = graph.vertexSet().size();
        this.nodes = new ArrayList<>(n);
        this.dfsTreeRoots = new ArrayList<>();
        this.componentRoots = new ArrayList<>(n);
        this.stack = new ArrayList<>();
    }

    private Node<V, E> createNewNode(Map<V, Node<V, E>> vertexMap, V graphVertex, E edge, Node<V, E> parent, int dfsIndex) {
        Node<V, E> child;
        if (parent == null) {
            child = new Node<>(graphVertex, dfsIndex, 0, null, null);
            child.setOuterFaceNeighbors(child, child);
            dfsTreeRoots.add(child);
        } else {
            Edge<V, E> treeEdge = new Edge<>(edge, parent);
            Node<V, E> componentRoot = new Node<>(parent.getDfsIndex(), treeEdge);
            child = new Node<>(graphVertex, dfsIndex, parent.getHeight() + 1, componentRoot, treeEdge);
            treeEdge.setTarget(child);
            componentRoots.add(componentRoot);
            parent.getTreeEdges().add(treeEdge);
            child.setOuterFaceNeighbors(componentRoot, componentRoot);
            componentRoot.setOuterFaceNeighbors(child, child);
        }
        nodes.add(child);
        vertexMap.put(graphVertex, child);
        return child;
    }

    private int orientDfs(Map<V, Node<V, E>> vertexMap, V startGraphVertex, int currentDfsIndex) {
        List<OrientDfsStackInfo<V, E>> localStack = new ArrayList<>();
        localStack.add(new OrientDfsStackInfo<>(startGraphVertex, null, null, false));
        while (!localStack.isEmpty()) {
            OrientDfsStackInfo<V, E> info = localStack.remove(localStack.size() - 1);
            if (info.isBacktrack()) {
                Node<V, E> current = vertexMap.get(info.getCurrent());
                current.setLeastAncestor(current.getDfsIndex());
                current.setLowpoint(current.getDfsIndex());
                for (Edge<V, E> backEdge : current.getBackEdges()) {
                    current.setLeastAncestor(Math.min(current.getLeastAncestor(), backEdge.getTarget().getDfsIndex()));
                }
                for (Edge<V, E> treeEdge : current.getTreeEdges()) {
                    current.setLowpoint(Math.min(current.getLowpoint(), treeEdge.getTarget().getLowpoint()));
                }
                current.setLowpoint(Math.min(current.getLowpoint(), current.getLeastAncestor()));
            } else {
                if (vertexMap.containsKey(info.getCurrent())) {
                    continue;
                }
                localStack.add(new OrientDfsStackInfo<>(info.getCurrent(), info.getParent(), info.getParentEdge(), true));
                Node<V, E> current = createNewNode(vertexMap, info.getCurrent(), info.getParentEdge(), vertexMap.get(info.getParent()), currentDfsIndex);
                ++currentDfsIndex;
                for (E e : graph.edgesOf(info.getCurrent())) {
                    V opposite = Graphs.getOppositeVertex(graph, e, info.getCurrent());
                    if (vertexMap.containsKey(opposite)) {
                        Node<V, E> oppositeNode = vertexMap.get(opposite);
                        if (opposite.equals(info.getParent())) continue;
                        Edge<V, E> backEdge = new Edge<>(e, current, oppositeNode);
                        oppositeNode.getDownEdges().add(backEdge);
                        current.getBackEdges().add(backEdge);
                    } else {
                        localStack.add(new OrientDfsStackInfo<>(opposite, info.getCurrent(), e, false));
                    }
                }
            }
        }
        return currentDfsIndex;
    }

    private void orient() {
        Map<V, Node<V, E>> visited = new HashMap<>();
        int currentDfsIndex = 0;
        for (V vertex : graph.vertexSet()) {
            if (!visited.containsKey(vertex)) {
                currentDfsIndex = orientDfs(visited, vertex, currentDfsIndex);
            }
        }
        sortVertices();
    }

    private void sortVertices() {
        List<List<Node<V, E>>> sorted = new ArrayList<>(Collections.nCopies(n, null));
        for (Node<V, E> node : nodes) {
            int lowpoint = node.getLowpoint();
            if (sorted.get(lowpoint) == null) {
                sorted.set(lowpoint, new ArrayList<>());
            }
            sorted.get(lowpoint).add(node);
        }
        int i = 0;
        for (List<Node<V, E>> list : sorted) {
            if (i >= n) break;
            if (list != null) {
                for (Node<V, E> node : list) {
                    nodes.set(i++, node);
                    if (node.getParentEdge() != null) {
                        node.setListNode(node.getParentEdge().getSource().getSeparatedDfsChildList().addElementLast(node));
                    }
                }
            }
        }
    }

    /**
     * Lazily tests the planarity of the graph. The implementation below is close to the code
     * presented in the original paper
     *
     * @return true if the graph is planar, false otherwise
     */
    private boolean lazyTestPlanarity()
    {
        if (!tested) {
            tested = true;

            orient();
            if (DEBUG) {
                printState();
                System.out.println("Start testing planarity");
            }
            for (int currentNode = n - 1; currentNode >= 0; currentNode--) {
                Node<V, E> current = nodes.get(currentNode);
                if (DEBUG) {
                    System.out.printf("Current vertex is %s\n", current.toString(false));
                }
                for (Edge<V, E> downEdge : current.getDownEdges()) {
                    walkUp(downEdge.getSource(), current, downEdge);
                }
                for (Edge<V, E> treeEdge : current.getTreeEdges()) {
                    walkDown(treeEdge.getTarget().getInitialComponentRoot());
                }
                for (Edge<V, E> downEdge : current.getDownEdges()) {
                    if (!downEdge.isEmbedded()) {
                        failedV = current;
                        return planar = false;
                    }
                }
            }
            planar = true;
        }
        return planar;
    }

    private void walkUp(Node<V, E> source, Node<V, E> current, Edge<V, E> downEdge) {
        if (DEBUG) {
            System.out.printf("Walking up from %s to %s using edge %s\n", source, current, downEdge);
        }
        downEdge.setEmbedded(true);
    }

    private void walkDown(Node<V, E> node) {
        if (DEBUG) {
            System.out.printf("Walking down from node %s\n", node);
        }
        node.getInitialComponentRoot().mergeBiconnectedComponent();
    }

    @Override
    public boolean isPlanar() {
        return lazyTestPlanarity();
    }

    @Override
    public Embedding<V, E> getEmbedding() {
        if (isPlanar()) {
            return lazyComputeEmbedding();
        } else {
            throw new IllegalArgumentException("Graph is not planar");
        }
    }

    @Override
    public Graph<V, E> getKuratowskiSubdivision() {
        if (isPlanar()) {
            throw new IllegalArgumentException("Graph is planar");
        } else {
            return lazyExtractKuratowskiSubdivision();
        }
    }

    private void printState() {
        System.out.println("\nPrinting state:");
        System.out.println("Dfs roots: " + dfsTreeRoots);
        System.out.println("Nodes:");
        for (Node<V, E> node : nodes) {
            System.out.println(node.toString(true));
        }
        System.out.println("Virtual nodes:");
        for (Node<V, E> node : componentRoots) {
            System.out.println(node.toString(true));
        }
        List<Edge<V, E>> inverted = new ArrayList<>();
        for (Node<V, E> node : nodes) {
            for (Edge<V, E> edge : node.getTreeEdges()) {
                if (edge.getSign() < 0) {
                    inverted.add(edge);
                }
            }
        }
        System.out.println("Inverted edges = " + inverted);
    }


    private Graph<V, E> lazyExtractKuratowskiSubdivision() {
        if (DEBUG) {
            System.out.println("Extracting Kuratowski subdivision...");
        }
        kuratowskiSubdivision = new AsSubgraph<>(graph);
        return kuratowskiSubdivision;
    }

    private Embedding<V, E> lazyComputeEmbedding() {
        if (DEBUG) {
            System.out.println("Computing embedding...");
        }
        embedding = new EmbeddingImpl<V, E>(graph, null);
        return embedding;
    }
}
