package org.protege.osgi.graph;

import org.osgi.framework.Bundle;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

public enum LayoutEnum {
    CIRCLE_LAYOUT("Circle Layout") {
        public Layout<Bundle, Edge> buildLayout(Graph<Bundle, Edge> graph) { 
            return new CircleLayout<Bundle, Edge>(graph); 
        }
    },
    DIRECTED_ACYCLIC_GRAPH_LAYOUT("Directed Acyclic Graph Layout") {
        public Layout<Bundle, Edge> buildLayout(Graph<Bundle, Edge> graph) { 
            return new DAGLayout<Bundle, Edge>(graph); 
        }        
    },
    FR_LAYOUT("Fruchterman-Reingold Layout") {
        public Layout<Bundle, Edge> buildLayout(Graph<Bundle, Edge> graph) { 
            return new FRLayout<Bundle, Edge>(graph); 
        }
    },
    MEYERS_LAYOUT("Meyer's Self-Organizing Layout") {
        public Layout<Bundle, Edge> buildLayout(Graph<Bundle, Edge> graph) { 
            return new ISOMLayout<Bundle, Edge>(graph); 
        }
    }
    ;
    
    private String name;
    private LayoutEnum(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public abstract Layout<Bundle, Edge> buildLayout(Graph<Bundle, Edge> graph);
    
    public static String[] getNames() {
        String[] names = new String[LayoutEnum.values().length];
        int counter = 0;
        for (LayoutEnum le : LayoutEnum.values()) {
            names[counter++] = le.getName();
        }
        return names;
    }
}
