/**
 * 
 */
package org.protege.osgi.graph;

import org.apache.commons.collections15.Predicate;
import org.osgi.framework.Bundle;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;

public class OSGiEdgeArrowPredicate implements Predicate<Context<Graph<Bundle,Edge>,Edge>> {

    public boolean evaluate(Context<Graph<Bundle, Edge>, Edge> arg0) {
        return false;
    }
    
}