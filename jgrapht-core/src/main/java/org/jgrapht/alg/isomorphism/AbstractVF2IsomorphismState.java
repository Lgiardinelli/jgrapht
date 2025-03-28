package org.jgrapht.alg.isomorphism;

import java.util.Comparator;

abstract class AbstractVF2IsomorphismState<V, E> extends VF2State<V, E> {

    // Compteurs pour les arêtes sortantes
    protected int termOutSucc1, termOutSucc2, termInSucc1, termInSucc2, newSucc1, newSucc2;
    // Compteurs pour les arêtes entrantes
    protected int termOutPred1, termOutPred2, termInPred1, termInPred2, newPred1, newPred2;

    public AbstractVF2IsomorphismState(
            GraphOrdering<V, E> g1, GraphOrdering<V, E> g2,
            Comparator<V> vertexComparator, Comparator<E> edgeComparator)
    {
        super(g1, g2, vertexComparator, edgeComparator);
    }

    public AbstractVF2IsomorphismState(VF2State<V, E> s) {
        super(s);
    }

    /**
     * Méthode template qui définit le squelette de la vérification d'une paire
     * et délègue la comparaison finale des compteurs aux sous-classes.
     */
    @Override
    public boolean isFeasiblePair() {
        // Vérifier la compatibilité sémantique des sommets
        if (!areCompatibleVertexes(addVertex1, addVertex2)) {
            return false;
        }

        // Réinitialiser les compteurs
        resetCounters();

        // Traiter les arêtes sortantes
        if (!processOutgoingEdges()) {
            return false;
        }

        // Traiter les arêtes entrantes
        if (!processIncomingEdges()) {
            return false;
        }

        // Comparer les compteurs selon la politique (isomorphisme complet ou sous-isomorphisme)
        return compareCounters();
    }

    /**
     * Réinitialise tous les compteurs.
     */
    protected void resetCounters() {
        termOutSucc1 = termOutSucc2 = termInSucc1 = termInSucc2 = newSucc1 = newSucc2 = 0;
        termOutPred1 = termOutPred2 = termInPred1 = termInPred2 = newPred1 = newPred2 = 0;
    }

    /**
     * Traite les arêtes sortantes pour addVertex1 et addVertex2.
     */
    protected boolean processOutgoingEdges() {
        final int[] outE1 = g1.getOutEdges(addVertex1);
        for (int i = 0; i < outE1.length; i++) {
            final int other1 = outE1[i];
            if (core1[other1] != NULL_NODE) {
                final int other2 = core1[other1];
                if (!g2.hasEdge(addVertex2, other2) ||
                        !areCompatibleEdges(addVertex1, other1, addVertex2, other2))
                {
                    return false;
                }
            } else {
                final int in1O1 = in1[other1];
                final int out1O1 = out1[other1];
                if ((in1O1 == 0) && (out1O1 == 0)) {
                    newSucc1++;
                } else {
                    if (in1O1 > 0) termInSucc1++;
                    if (out1O1 > 0) termOutSucc1++;
                }
            }
        }

        final int[] outE2 = g2.getOutEdges(addVertex2);
        for (int i = 0; i < outE2.length; i++) {
            final int other2 = outE2[i];
            if (core2[other2] != NULL_NODE) {
                final int other1 = core2[other2];
                if (!g1.hasEdge(addVertex1, other1)) {
                    return false;
                }
            } else {
                final int in2O2 = in2[other2];
                final int out2O2 = out2[other2];
                if ((in2O2 == 0) && (out2O2 == 0)) {
                    newSucc2++;
                } else {
                    if (in2O2 > 0) termInSucc2++;
                    if (out2O2 > 0) termOutSucc2++;
                }
            }
        }
        return true;
    }

    /**
     * Traite les arêtes entrantes pour addVertex1 et addVertex2.
     */
    protected boolean processIncomingEdges() {
        final int[] inE1 = g1.getInEdges(addVertex1);
        for (int i = 0; i < inE1.length; i++) {
            final int other1 = inE1[i];
            if (core1[other1] != NULL_NODE) {
                final int other2 = core1[other1];
                if (!g2.hasEdge(other2, addVertex2) ||
                        !areCompatibleEdges(other1, addVertex1, other2, addVertex2))
                {
                    return false;
                }
            } else {
                final int in1O1 = in1[other1];
                final int out1O1 = out1[other1];
                if ((in1O1 == 0) && (out1O1 == 0)) {
                    newPred1++;
                } else {
                    if (in1O1 > 0) termInPred1++;
                    if (out1O1 > 0) termOutPred1++;
                }
            }
        }

        final int[] inE2 = g2.getInEdges(addVertex2);
        for (int i = 0; i < inE2.length; i++) {
            final int other2 = inE2[i];
            if (core2[other2] != NULL_NODE) {
                final int other1 = core2[other2];
                if (!g1.hasEdge(other1, addVertex1)) {
                    return false;
                }
            } else {
                final int in2O2 = in2[other2];
                final int out2O2 = out2[other2];
                if ((in2O2 == 0) && (out2O2 == 0)) {
                    newPred2++;
                } else {
                    if (in2O2 > 0) termInPred2++;
                    if (out2O2 > 0) termOutPred2++;
                }
            }
        }
        return true;
    }

    /**
     * Méthode abstraite à implémenter par les sous-classes pour comparer les compteurs
     * en fonction des contraintes spécifiques de l'isomorphisme complet ou du sous-isomorphisme.
     */
    protected abstract boolean compareCounters();
}
