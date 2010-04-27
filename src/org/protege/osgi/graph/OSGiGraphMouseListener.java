package org.protege.osgi.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import edu.uci.ics.jung.visualization.control.GraphMouseListener;

public class OSGiGraphMouseListener implements GraphMouseListener<Bundle> {
    private Logger logger = Logger.getLogger(OSGiGraphMouseListener.class);
    private JPopupMenu pop;
    private Bundle bundle;

    public OSGiGraphMouseListener() {
         pop = new  JPopupMenu();
        
        JMenuItem start = new JMenuItem("Start Bundle");
        start.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                try {
                    bundle.start();
                }
                catch (BundleException be) {
                    logger.info("could not start bundle" + be);
                }
            }
        });
        pop.add(start);
        
        JMenuItem stop = new JMenuItem("Stop Bundle");
        stop.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                try {
                    bundle.stop();
                }
                catch (BundleException be) {
                    logger.info("could not stop bundle" + be);
                }
            }
        });
        pop.add(stop);
        
        JMenuItem update = new JMenuItem("Update Bundle");
        update.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                try {
                    bundle.update();
                }
                catch (BundleException be) {
                    logger.info("could not update bundle" + be);
                }
            }
        });
        pop.add(update);
        
        
        JMenuItem uninstall = new JMenuItem("Uninstall Bundle");
        uninstall.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                try {
                    bundle.uninstall();
                }
                catch (BundleException be) {
                    logger.info("could not uninstall bundle" + be);
                }
            }
        });
        pop.add(uninstall);
    }
    
    public void graphClicked(Bundle bundle, MouseEvent event) {
        this.bundle = bundle;
        pop.show(event.getComponent(), event.getX(), event.getY());
    }

    public void graphPressed(Bundle bundle, MouseEvent event) {
        
    }

    public void graphReleased(Bundle bundle, MouseEvent event) {
        
    }
    
}