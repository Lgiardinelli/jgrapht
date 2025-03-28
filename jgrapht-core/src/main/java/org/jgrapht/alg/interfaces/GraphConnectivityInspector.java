package org.jgrapht.alg.interfaces;

import java.util.Set;

public interface GraphConnectivityInspector<V, E> {

    boolean isConnected();

    Set<Set<V>> connectedSets();
}
