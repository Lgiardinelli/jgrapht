package org.jgrapht.alg.planar;

import org.jgrapht.Graph;
import java.util.List;
import java.util.Map;

/**
 * Interface pour représenter un embedding combinatoire.
 */
public interface Embedding<V, E> {
    /**
     * Retourne la map qui associe chaque sommet à la liste ordonnée de ses arêtes.
     */
    Map<V, List<E>> getEmbedding();
    
    /**
     * Retourne le graphe original associé à cet embedding.
     */
    Graph<V, E> getGraph();
}
