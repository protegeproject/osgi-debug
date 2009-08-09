package org.protege.osgi.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

public class PackageServlet extends HttpServlet {
    private static final long serialVersionUID = -2083051888153213291L;
    
    public static final String PATH="/debug/package";
    
    public static final String BUNDLE_LIST_MENU = "BundleList";
    
    private BundleContext context;
    private PackageAdmin packageAdmin;
    
    public PackageServlet(BundleContext context, PackageAdmin packageAdmin) {
        this.context = context;
        this.packageAdmin = packageAdmin;
    }
    
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        Bundle b = getBundle(request);
        PrintWriter out = response.getWriter();
        doExplanation(out);
        doForm(out, b == null ? -1 : b.getBundleId());
        doResults(out, b);
    }
    
    private void doExplanation(PrintWriter out) {
        out.println("<H1>Package Import/Export Debug Utility</H1>");
        out.println("<P>This servlet helps one see which import statements from one bundle get");
        out.println("hooked up with which export statements from other bundles.<P>");
    }
    
    private Bundle getBundle(HttpServletRequest request) {
        String[] ids  = request.getParameterValues(BUNDLE_LIST_MENU);
        if (ids == null) {
            return null;
        }
        for (String id : ids) {
            id = id.trim();
            long bundleId = Long.parseLong(id);
            return context.getBundle(bundleId);
        }
        return null;
    }
    
    private void doForm(PrintWriter out, long bundleId) {
        out.println("<HTML><HEAD><TITLE>Bundle Package Debugger</TITLE>"
                       + "</HEAD><BODY>");
        out.println("<FORM METHOD=\"GET\"/>");
        out.println("<SELECT NAME=\"" + BUNDLE_LIST_MENU + "\">");
        for (Bundle b : context.getBundles()) {
            out.println("<OPTION VALUE=\"" + b.getBundleId() + "\" ");
            if (b.getBundleId() == bundleId) {
                out.println("SELECTED=\"TRUE\"");
            }
            out.println(">");
            printBundle(out, b, null);
            out.println("</OPTION>");
        }
        out.println("</SELECT>");
        out.println("<INPUT TYPE=\"SUBMIT\" VALUE=\"SUBMIT\"/>");
        out.println("</FORM>");
    }
    
    
    private void doResults(PrintWriter out, Bundle b) {
        if (b != null) {
            out.println("Information for bundle ");
            printBundle(out, b, "");
            doImports(out, b);
            doExports(out, b);
        }
    }

    private void doImports(PrintWriter out, Bundle b) {
        Set<ExportedPackage> imports = new TreeSet<ExportedPackage>(new Comparator<ExportedPackage>() {

            public int compare(ExportedPackage p1, ExportedPackage p2) {
                return p1.getName().compareTo(p2.getName());
            }
            
        });
        for (Bundle exporter : context.getBundles()) {
            if (exporter.equals(b)) continue;
            ExportedPackage[] exports = packageAdmin.getExportedPackages(exporter);
            if (exports != null) {
                for (ExportedPackage export : exports)  {
                    Bundle[] importers = export.getImportingBundles();
                    if (importers != null) {
                        for (Bundle importer : importers) {
                            if (b.equals(importer)) {
                                imports.add(export);
                            }
                        }
                    }
                }
            }
        }
        out.println("<P><B>Imports</B><P>");
        if (imports.isEmpty()) {
            out.println("No imports");
        }
        else {
            out.println("<UL>");
            for (ExportedPackage p : imports) {
                out.println("<li> " + p.getName() + " imported from bundle ");
                printBundle(out, p.getExportingBundle(), "");
            }
            out.println("</UL>");
        }
    }
    
    private void doExports(PrintWriter out, Bundle b) {
        ExportedPackage [] packages = packageAdmin.getExportedPackages(b);
        out.println("<P><B>Exports</B><P>");
        if (packages != null && packages.length > 0) {
            for (ExportedPackage p : packages) {
                out.println("<li> " + p.getName());
                Bundle [] importers = p.getImportingBundles();
                if (importers != null && importers.length > 0
                        && (importers.length > 1 || !importers[0].equals(b))) {
                    out.println(" -- This package is imported by the following bundles: <UL>");
                    for (Bundle importer : importers) {
                        if (!importer.equals(b)) {
                            out.println("<li> ");
                            printBundle(out, importer, "");
                        }
                    }
                    out.println("</UL>");
                }
            }
            out.println("</UL>");
        } else {
            out.println("No exports.");
        }
    }
    
    public static void printBundle(PrintWriter out, Bundle b, String hyperlink) {
        String bundleString = b.getSymbolicName() + " - " + b.getBundleId() + " (" + b.getHeaders().get(Constants.BUNDLE_NAME) + ")";
        if (hyperlink != null) {
            out.print("<A HREF=\"" + hyperlink + "?" + BUNDLE_LIST_MENU + "=" + b.getBundleId() + "\">" 
                            + bundleString + "</A>");
        }
        else {
            out.print(bundleString);
        }
    }

}
