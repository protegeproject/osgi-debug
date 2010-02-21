package org.protege.osgi.graph;

import javax.swing.JFrame;

import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.protege.osgi.PackageViewer;

public class MainFrame extends JFrame implements PackageViewer {
    private static final long serialVersionUID = 7674084671467647220L;

    public MainFrame() {
        super("JUNG-based OSGi Debug Frame");
    }
    
    public void initialize(BundleContext context, PackageAdmin packages) {
        MainPanel panel = new MainPanel(context, packages);
        setContentPane(panel);
        pack();
        setVisible(true);
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }
    
    
}
