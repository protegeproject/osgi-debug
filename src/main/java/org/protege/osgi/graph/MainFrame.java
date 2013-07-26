package org.protege.osgi.graph;

import java.awt.Point;

import javax.swing.JFrame;

import org.osgi.framework.BundleContext;
import org.protege.osgi.PackageViewer;

public class MainFrame extends JFrame implements PackageViewer {
    private static final long serialVersionUID = 7674084671467647220L;

    public MainFrame() {
        super("JUNG-based OSGi Debug Frame");
    }
    
    public void initialize(BundleContext context) {
        MainPanel panel = new MainPanel(context);
        setContentPane(panel);
        setLocation(new Point(40,40));
        pack();
        setVisible(true);
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }
    
    
}
