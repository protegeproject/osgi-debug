package org.protege.osgi.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

public abstract class PackageBrowserDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 6347220213399845867L;
    private JList packageList;
    
    public PackageBrowserDialog(BundleContext context) {
        packageList = new JList(getPackageNames(context));
        packageList.addMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent e) {
               if (e.getClickCount() > 1) {
                   userSelectedSomething();
               }
            } 
        });
        JScrollPane scrollPane = new JScrollPane(packageList);
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(scrollPane, BorderLayout.CENTER);

        setContentPane(content);

        JButton select = new JButton("Select");
        select.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                userSelectedSomething();
            }
        });
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        south.add(select);
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        south.add(cancel);
        content.add(south, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(350, 350));
    }
    
    private Object[] getPackageNames(BundleContext context) {
        TreeSet<String> packageNames = new TreeSet<String>();
        for (Bundle b : context.getBundles()) {
            BundleWiring wiring = b.adapt(BundleWiring.class);
            for (BundleWire wire : wiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE)) {
                String packageName = (String) wire.getCapability().getAttributes().get(BundleRevision.PACKAGE_NAMESPACE);
                packageNames.add(packageName);
            }
        }
        Object[] objects = new Object[packageNames.size()];
        int index = 0;
        for (String packageName : packageNames) {
            objects[index++] = packageName;
        }
        return objects;
    }
    
    private void userSelectedSomething() {
        String packageName = (String) packageList.getSelectedValue();
        if (packageName != null) {
            packageSelected(packageName);
        }
        setVisible(false);
    }
    
    abstract protected void packageSelected(String packageName);
    
    
}
