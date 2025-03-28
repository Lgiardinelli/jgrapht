/*
 * (C) Copyright 2015-2023, by Fabian Späh and Contributors.
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
package org.jgrapht.alg.isomorphism;

import java.util.*;

class VF2SubgraphIsomorphismState<V, E> extends AbstractVF2IsomorphismState<V, E> {

    public VF2SubgraphIsomorphismState(
        GraphOrdering<V, E> g1, GraphOrdering<V, E> g2, Comparator<V> vertexComparator,
        Comparator<E> edgeComparator)
    {
        super(g1, g2, vertexComparator, edgeComparator);
    }

    public VF2SubgraphIsomorphismState(VF2State<V, E> s)
    {
        super(s);
    }

    /**
     * @return true, if the already matched vertices of graph1 plus the first vertex of nextPair are
     *         subgraph isomorphic to the already matched vertices of graph2 and the second one
     *         vertex of nextPair.
     */
    @Override
    protected boolean compareCounters() {
        return (termInSucc1 >= termInSucc2) && (termOutSucc1 >= termOutSucc2) && (newSucc1 >= newSucc2)
                && (termInPred1 >= termInPred2) && (termOutPred1 >= termOutPred2) && (newPred1 >= newPred2);
    }
}
