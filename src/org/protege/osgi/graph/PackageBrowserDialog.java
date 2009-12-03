package org.protege.osgi.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

public abstract class PackageBrowserDialog extends JDialog {
    
    public PackageBrowserDialog(BundleContext context, PackageAdmin packages) {
        TreeSet<String> packageNames = new TreeSet<String>();
        for (Bundle b : context.getBundles()) {
            ExportedPackage[] exports = packages.getExportedPackages(b);
            if (exports == null) {
                continue;
            }
            for (ExportedPackage p : exports) {
                Bundle[] importers = p.getImportingBundles();
                if (importers == null || importers.length == 0) {
                    continue;
                }
                if (importers.length == 1 && importers[0] == b) {
                    continue;
                }
                packageNames.add(p.getName());
            }
        }
        Object[] objects = new Object[packageNames.size()];
        int index = 0;
        for (String packageName : packageNames) {
            objects[index++] = packageName;
        }
        
        final JList packageList = new JList(objects);
        JScrollPane scrollPane = new JScrollPane(packageList);
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(scrollPane, BorderLayout.CENTER);

        setContentPane(content);

        JButton select = new JButton("Select");
        select.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String packageName = (String) packageList.getSelectedValue();
                packageSelected(packageName);
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
    
    abstract protected void packageSelected(String packageName);
    
    
}
