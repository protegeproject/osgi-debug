package org.protege.osgi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

public class ClassLoaderServlet extends HttpServlet {
    private static final long serialVersionUID = -4969594574389053166L;
    
    public static final String BUNDLE_LIST_MENU = "BundleList";
    public static final String CLASS_NAME_FIELD = "ClassName";

    private BundleContext context;
    private PackageAdmin packageAdmin;
    
    public ClassLoaderServlet(BundleContext context) {
        this.context = context;
        ServiceReference packageReference = context.getServiceReference(PackageAdmin.class.getName());
        if (packageReference != null) {
            packageAdmin = (PackageAdmin) context.getService(packageReference);
        }
    }
    
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        Bundle b = getBundle(request);
        String className = getClassName(request);
        PrintWriter out = response.getWriter();
        doForm(out, b == null ? -1 : b.getBundleId(), className);
        if (b != null && className != null) {
            doResults(out, b, className);
        }
        out.println("</BODY></HTML>");
        out.close();
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
    
    private String getClassName(HttpServletRequest request) {
        String[] names = request.getParameterValues(CLASS_NAME_FIELD);
        if (names == null) {
            return null;
        }
        for (String name : names) {
            return name;
        }
        return null;
    }
    
    private void doForm(PrintWriter out, long bundleId, String className) {
        out.println("<HTML><HEAD><TITLE>Bundle Class Loader Debugger</TITLE>"
                       + "</HEAD><BODY>");
        out.println("<FORM METHOD=\"GET\"/>");
        out.println("<SELECT NAME=\"" + BUNDLE_LIST_MENU + "\">");
        for (Bundle b : context.getBundles()) {
            out.println("<OPTION VALUE=\"" + b.getBundleId() + "\" ");
            if (b.getBundleId() == bundleId) {
                out.println("SELECTED=\"TRUE\"");
            }
            out.println(">");
            printBundle(out, b);
            out.println("</OPTION>");
        }
        out.println("</SELECT>");
        out.println("<P>Class Name: <INPUT TYPE=\"text\" NAME=\"" + CLASS_NAME_FIELD + "\" ");
        if (className != null) {
            out.println("VALUE=\"" + className + "\"");
        }
        out.println("><P>");
        out.println("<INPUT TYPE=\"SUBMIT\" VALUE=\"SUBMIT\"/>");
        out.println("</FORM>");
    }
    
    private void doResults(PrintWriter out, Bundle b, String className) {
        out.println("<P>");
        Class<?> c = null;
        try {
            c = b.loadClass(className);
        }
        catch (Exception e) {
            out.println("Could not load class " + className + " in bundle ");
            printBundle(out, b);
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        if (c != null)  {
            out.println("Bundle ");
            printBundle(out, b);
            out.println(" loaded class " + c + ".");
            if (packageAdmin != null) {
                Bundle supplier = packageAdmin.getBundle(c);
                if (supplier != null) {
                    out.println("The bundle supplying the class was ");
                    printBundle(out, supplier);
                    out.println(".");
                } else {
                    out.println("This class was not supplied by a bundle.");
                }
            }
        }
        if (packageAdmin != null) {
            ExportedPackage [] packages = packageAdmin.getExportedPackages(b);
            if (packages != null && packages.length > 0) {
                out.println("The packages exported by this bundle are as follows: <UL>");
                for (ExportedPackage p : packages) {
                    out.println("<li> " + p.getName());
                    Bundle [] importers = p.getImportingBundles();
                    if (importers != null && importers.length > 0
                            && (importers.length > 1 || !importers[0].equals(b))) {
                        out.println(" -- This package is imported by the following bundles: <UL>");
                        for (Bundle importer : importers) {
                            if (!importer.equals(b)) {
                                out.println("<li> ");
                                printBundle(out, importer);
                            }
                        }
                        out.println("</UL>");
                    }
                }
                out.println("</UL>");
            }
        }
        
        
    }
    
    private void printBundle(PrintWriter out, Bundle b) {
        out.print(b.getSymbolicName() + " - " + b.getBundleId() + " (" + b.getHeaders().get(Constants.BUNDLE_NAME) + ")");
    }
    
    
}
