package org.protege.osgi.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphBuilder {
    private BundleContext context;
    private String packageName;
    
    public GraphBuilder(BundleContext context, String packageName)  {
        this.context = context;
        this.packageName = packageName;
    }
    
    public DirectedGraph<Bundle, Edge> getGraph() {
        DirectedSparseGraph<Bundle, Edge> graph = new DirectedSparseGraph<Bundle, Edge>();
        for (Bundle exporting : context.getBundles()) {
            graph.addVertex(exporting);
            if (isResolved(exporting)) {
            	Map<Bundle, Set<String>> map = new HashMap<Bundle, Set<String>>();
            	addSimpleRequirements(exporting, map);
            	addBundleRequirements(exporting, map);
            	for (Entry<Bundle, Set<String>> entry : map.entrySet()) {
            		graph.addEdge(new Edge(exporting,entry.getKey(), entry.getValue()), 
            				new Pair<Bundle>(exporting, entry.getKey()));
            	}
            }
        }
        return graph;
    }
    
    private boolean isResolved(Bundle bundle) {
    	int state = bundle.getState();
    	return state != Bundle.UNINSTALLED && state != Bundle.INSTALLED;
    }
    
    private void addSimpleRequirements(Bundle exporting, Map<Bundle, Set<String>> map) {
        BundleWiring wiring = exporting.adapt(BundleWiring.class);
        if (wiring == null) {
        	return;
        }
        List<BundleWire> exportedPackages = wiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE);
        if (exportedPackages != null) {
        	for (BundleWire export : exportedPackages) {
        		Bundle importing = export.getRequirerWiring().getBundle();
        		String packge = (String) export.getCapability().getAttributes().get(BundleRevision.PACKAGE_NAMESPACE);
        		if (importing.equals(exporting)) {
        			continue;
        		}
        		addWire(packge, importing, map);
        	}
        }
    }
    
    private void addBundleRequirements(Bundle exporting, Map<Bundle, Set<String>> map) {
        BundleWiring wiring = exporting.adapt(BundleWiring.class);
        List<BundleCapability> allPackages = wiring.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);
        List<BundleWire> selfExports = wiring.getProvidedWires(BundleRevision.BUNDLE_NAMESPACE);
        if (selfExports != null) {
        	for (BundleWire selfExport : selfExports) {
        		Bundle importing = selfExport.getRequirerWiring().getBundle();
        		for (BundleCapability implicitWire : allPackages) {
        			String packge = (String) implicitWire.getAttributes().get(BundleRevision.PACKAGE_NAMESPACE);
        			addWire(packge, importing, map);
        		}
        	}
        }
    }
    
    private void addWire(String packge, Bundle importing, Map<Bundle, Set<String>> map) {
		Set<String> wires = map.get(importing);
		if (wires == null)  {
			wires = new HashSet<String>();
			map.put(importing, wires);
		}
		wires.add(packge);
    }
}
