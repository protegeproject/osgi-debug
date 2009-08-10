package org.protege.osgi.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.collections15.Transformer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelRenderer;

public class MainPanel extends JPanel {
    private static final int CLASS = 0;
    private static final int PACKAGE = 1;
    
    private BundleContext context;
    private PackageAdmin packages;
    
    private JComboBox classOrPackageBox;
    private JTextField classOrPackageText;
    private JPanel canvas;

    public MainPanel(BundleContext context, PackageAdmin packages) {
        this.context = context;
        this.packages = packages;
        
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createMainDocument(), BorderLayout.CENTER);
    }
    
    private JComponent createHeader() {
        JPanel panel = new JPanel();
        
        panel.setLayout(new FlowLayout());
        String[] choices = { "Class", "Package" };
        classOrPackageBox = new JComboBox(choices);
        panel.add(classOrPackageBox);
        
        classOrPackageText = new JTextField();
        JTextField sample = new JTextField("org.protege.osgi.debug.graph.MainPanel");
        classOrPackageText.setPreferredSize(sample.getPreferredSize());
        panel.add(classOrPackageText);
        
        JButton draw = new JButton("Draw");
        draw.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               canvas.repaint();
            } 
        });
        panel.add(draw);
        
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                classOrPackageText.setText("");
                System.out.println("Clear and draw everything.");
            }
        });
        panel.add(clear);
        return panel;
    }
    
    private JComponent createMainDocument() {
        canvas = new JPanel();
        drawGraph();
        return canvas;
    }
    
    private void drawGraph() {
        canvas.removeAll();
        GraphBuilder builder = new GraphBuilder(context, packages);
        DirectedGraph<Bundle, Edge> graph = builder.getGraph();
        Layout<Bundle, Edge> layout = new CircleLayout<Bundle, Edge>(graph);
        layout.setSize(new Dimension(900,600));
        BasicVisualizationServer<Bundle,Edge> vv = 
                  new BasicVisualizationServer<Bundle,Edge>(layout); 
        vv.setPreferredSize(new Dimension(950, 650));
        vv.getRenderContext().setVertexLabelTransformer(new OSGiVertexLabelRenderer());
        vv.getRenderContext().setVertexFillPaintTransformer(new OSGiVertexPaintTransformer());
        vv.getRenderContext().setEdgeDrawPaintTransformer(new OSGiEdgeTransformer());
        canvas.add(vv);
    }
    
    private Bundle getOwningBundleFromClassInBundle(Bundle b) {
        String className = classOrPackageText.getText();
        if (className == null || className.equals("")) {
            return null;
        }
        try {
            Class c = b.loadClass(className);
            Bundle owner = packages.getBundle(c);
            if (owner == null) {
                return context.getBundle(0);
            }
            else return owner;
        }
        catch (Throwable t) {
            return null;
        }
    }
    
    private static class OSGiVertexLabelRenderer implements Transformer<Bundle, String> {
        
        @SuppressWarnings("unchecked")
        public String transform(Bundle vertex) {
            if (vertex instanceof Bundle) {
                Dictionary<String, String> headers = vertex.getHeaders();
                String name = headers.get(Constants.BUNDLE_NAME);
                return name == null ? vertex.getSymbolicName() : name;
            }
            return null;
        }

    }
    
    private class OSGiVertexPaintTransformer implements Transformer<Bundle, Paint> {
        public Paint transform(Bundle b) {
            if (classOrPackageBox.getSelectedIndex() == PACKAGE || 
            		getOwningBundleFromClassInBundle(b) == null) {
                return Color.RED;
            }
            else {
                return Color.GREEN;
            }
        }
    }
    
    private class OSGiEdgeTransformer implements Transformer<Edge, Paint> {
    	public Paint transform(Edge edge) {
    		String name = classOrPackageText.getText();
    		if (classOrPackageBox.getSelectedIndex() == CLASS) {
    			int index = name.lastIndexOf('.');
    			if (index < 0) {
    				name = "";
    			}
    			else  {
    				name = name.substring(0, index);
    			}
    		}
			for (ExportedPackage export : edge.getPackages()) {
				if (export.getName().equals(name)) {
					return Color.GREEN;
				}
			}
			return Color.RED;
    	}
    }
    
    
}
