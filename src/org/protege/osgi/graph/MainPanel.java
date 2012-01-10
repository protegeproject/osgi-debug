package org.protege.osgi.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.collections15.Transformer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

public class MainPanel extends JPanel {
    private static final long serialVersionUID = -6188855593855501050L;
    private static final int CLASS = 0;
    private static final int PACKAGE = 1;
    
    private JPanel footer;
    private BundleContext context;
    private JComboBox classOrPackageBox;
    private JTextField classOrPackageText;
    private VisualizationViewer<Bundle,Edge> graphView;
    private JComboBox layoutComboBox;
    private JDialog packageBrowser;

    public MainPanel(BundleContext context) {
        this.context = context;        
        setLayout(new BorderLayout());
        
        add(createHeader(), BorderLayout.NORTH);
        add(createMainDocument(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
 
        context.addFrameworkListener(new FrameworkListener() {
            
            public void frameworkEvent(FrameworkEvent event) {
                if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED 
                        || event.getType() == FrameworkEvent.STARTED
                        || event.getType() == FrameworkEvent.STARTLEVEL_CHANGED) {
                    refresh();
                }
            }
        });
    }
    
    private JComponent createHeader() {
        JPanel panel = new JPanel();
        
        panel.setLayout(new FlowLayout());
        String[] choices = { "Class", "Package" };
        classOrPackageBox = new JComboBox(choices);
        classOrPackageBox.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               graphView.repaint();
            } 
        });
        panel.add(classOrPackageBox);
        
        classOrPackageText = new JTextField();
        JTextField sample = new JTextField("org.protege.osgi.debug.graph.MainPanel");
        classOrPackageText.setPreferredSize(sample.getPreferredSize());
        classOrPackageText.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               graphView.repaint();
            } 
        });
        panel.add(classOrPackageText);
        
        JButton browse = new JButton("Browse Packages");
        browse.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               createBrowsePackagesDialog();
            } 
        });
        panel.add(browse);
        
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               graphView.repaint();
            } 
        });
        panel.add(refresh);
        
        JButton recalculate = new JButton("Rebuild Graph");
        recalculate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refresh();
             } 
         });
         panel.add(recalculate);
        
        return panel;
    }
    
    private JComponent createMainDocument() {
        drawGraph();
        return graphView;
    }
    
    private JComponent createFooter() {
        footer = new JPanel();
        footer.setLayout(new FlowLayout(FlowLayout.CENTER));  

        footer.add(layoutComboBox);
        footer.add(((AbstractModalGraphMouse)graphView.getGraphMouse()).getModeComboBox());

        return footer;
    }
    
    private void createBrowsePackagesDialog() {
        if (packageBrowser == null) {
            packageBrowser = new PackageBrowserDialog(context) {
                private static final long serialVersionUID = -6596713244703376792L;

                @Override
                protected void packageSelected(String packageName) {
                    classOrPackageText.setText(packageName);
                    graphView.repaint();
                    classOrPackageText.requestFocus();
                    classOrPackageText.setCaretPosition(packageName.length());
                }
            };
            packageBrowser.pack();
        }
        packageBrowser.setVisible(true);
    }

    private void drawGraph() {
        layoutComboBox = new JComboBox(LayoutEnum.getNames());
        layoutComboBox.setSelectedIndex(LayoutEnum.FR_LAYOUT.ordinal());
        layoutComboBox.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               refresh();
            } 
        }); // added to the footer later
        
        Layout<Bundle, Edge> layout = buildLayout();
        graphView = new VisualizationViewer<Bundle,Edge>(layout); 
        graphView.setPreferredSize(new Dimension(950, 650));
        graphView.getRenderContext().setVertexLabelTransformer(new OSGiVertexLabelRenderer());
        graphView.getRenderContext().setVertexFillPaintTransformer(new OSGiVertexPaintTransformer());
        graphView.getRenderContext().setEdgeDrawPaintTransformer(new OSGiEdgeTransformer());
        graphView.getRenderContext().setEdgeArrowPredicate(new OSGiEdgeArrowPredicate());
        AbstractModalGraphMouse gm = new DefaultModalGraphMouse<Bundle, Edge>();
        graphView.setGraphMouse(gm);
        graphView.setVertexToolTipTransformer(new OSGiToolTipTransformer());
        graphView.addGraphMouseListener(new OSGiGraphMouseListener());
    }
    
    private Layout<Bundle, Edge> buildLayout() {
        int layoutIndex = layoutComboBox.getSelectedIndex();
        LayoutEnum le = LayoutEnum.values()[layoutIndex];
        GraphBuilder builder = new GraphBuilder(context, getPackageName());
        Graph<Bundle, Edge> graph = builder.getGraph();
        Layout<Bundle, Edge> layout = le.buildLayout(graph);
        layout.setSize(new Dimension(900,600));
        return layout;
    }
    
    private void refresh() {
        graphView.setGraphLayout(buildLayout());
        graphView.repaint();
    }

    private String getCurrentClassName() {
        String className = classOrPackageText.getText();
        return className.replace('/','.');
    }
    
    private Class<?> getCurrentClass(Bundle b) {
        String className = getCurrentClassName();
        if (className == null || className.equals("")) {
            return null;
        }
        try {
            return b.loadClass(className);
        }
        catch (Throwable t) {
            return null;
        }
    }
    
    private Bundle getOwningBundle(Class<?> c) {
        return FrameworkUtil.getBundle(c);
    }
    
    private String getPackageName() {
        String name = getCurrentClassName();
        if (name == null || name.equals("")) {
            return null;
        }
        if (classOrPackageBox.getSelectedIndex() == CLASS) {
            int index = name.lastIndexOf('.');
            if (index < 0) {
                name = "";
            }
            else  {
                name = name.substring(0, index);
            }
        }
        return name;
    }
    
    private class OSGiVertexPaintTransformer implements Transformer<Bundle, Paint> {
        public Paint transform(Bundle b) {
            if (classOrPackageBox.getSelectedIndex() == PACKAGE) {
                return packageTransform(b);
            }
            else {
                return classTransform(b);
            }
        }
        
        private Paint packageTransform(Bundle b) {
            BundleWiring wiring = b.adapt(BundleWiring.class);
            String packageName = getPackageName();
            if (packageName == null || wiring == null) {
                return Color.RED;
            }
            List<BundleWire> exports = wiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE);
            if (exports != null) {
                for (BundleWire export : exports) {
                    String exportedPackage = (String) export.getCapability().getAttributes().get(BundleRevision.PACKAGE_NAMESPACE);
                    if (packageName.equals(exportedPackage)) {
                        return Color.BLUE;
                    }
                }
            }
            List<BundleWire> imports = wiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE);
            for (BundleWire imported : imports) {
                String importedPackage = (String) imported.getCapability().getAttributes().get(BundleRevision.PACKAGE_NAMESPACE);
                if (packageName.equals(importedPackage)) {
                    return Color.GREEN;
                }
            }
            return Color.RED;
        }
        
        private Paint classTransform(Bundle b) {
            Class<?> currentClass;
            if ((currentClass = getCurrentClass(b)) == null) {
                return Color.RED;
            }
            else if (b.equals(getOwningBundle(currentClass))) {
                return Color.BLUE;
            }
            else {
                return Color.GREEN;
            }
        }
    }
    
    private class OSGiEdgeTransformer implements Transformer<Edge, Paint> {
    	public Paint transform(Edge edge) {
    		String name = getPackageName();
            if (name == null) {
                return Color.BLACK;
            }
			for (String exportedPackage : edge.getPackages()) {
				if (name.equals(exportedPackage)) {
					return Color.RED;
				}
			}
			return Color.LIGHT_GRAY;
    	}
    }
    
}
