package org.jgrapht.alg.planar;

import org.jgrapht.Graph;
import java.util.List;
import java.util.Map;

/**
 * Implémentation minimale de l'interface Embedding.
 */
public class EmbeddingImpl<V, E> implements Embedding<V, E> {
    private final Graph<V, E> graph;
    private final Map<V, List<E>> embeddingMap;
    
    public EmbeddingImpl(Graph<V, E> graph, Map<V, List<E>> embeddingMap) {
        this.graph = graph;
        this.embeddingMap = embeddingMap;
    }
    
    @Override
    public Map<V, List<E>> getEmbedding() {
        return embeddingMap;
    }
    
    @Override
    public Graph<V, E> getGraph() {
        return graph;
    }
}
