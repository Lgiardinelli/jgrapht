package org.jgrapht.alg.connectivity;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.GraphConnectivityInspector;

import java.util.Set;

public class ConnectivityInspectorAdapter<V, E> implements GraphConnectivityInspector<V, E> {
    private final ConnectivityInspector<V, E> inspector;

    public ConnectivityInspectorAdapter(Graph<V, E> graph) {
        this.inspector = new ConnectivityInspector<>(graph);
    }

    @Override
    public boolean isConnected() {
        return inspector.isConnected();
    }

    @Override
    public Set<Set<V>> connectedSets() {
        return (Set<Set<V>>) inspector.connectedSets();
    }
}
