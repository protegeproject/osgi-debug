package org.protege.osgi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;

public class ClassLoaderServlet extends HttpServlet {
    private static final long serialVersionUID = -4969594574389053166L;
    
    public static final String PATH = "/debug/classloader";
    
    public static final String CLASS_NAME_FIELD = "ClassName";

    private BundleContext context;
    private PackageAdmin packageAdmin;
    
    public ClassLoaderServlet(BundleContext context, PackageAdmin packageAdmin) {
        this.context = context;
        this.packageAdmin = packageAdmin;
    }
    
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        String className = getClassName(request);
        PrintWriter out = response.getWriter();
        doExplanation(out);
        doForm(out, className);
        if (className != null) {
            doResults(out, className);
        }
        out.println("</BODY></HTML>");
        out.close();
    }
    

    
    private String getClassName(HttpServletRequest request) {
        String[] names = request.getParameterValues(CLASS_NAME_FIELD);
        if (names == null) {
            return null;
        }
        for (String name : names) {
            name = name.replace('/', '.');
            return name;
        }
        return null;
    }
    
    private void doExplanation(PrintWriter out) {
        out.println("<H1>Class Loader Debug Utility</H1>");
        out.println("This servlet provides some debugging capabilities for problems with OSGi classloading.");
        out.println("It will provide input on the following situations:<UL>");
        out.println("<li> It will inform you if no bundle can find the class in question.");
        out.println("This means that some class path in some manifest must be updated or some jar file is");
        out.println("missing.");
        out.println("<li> It will inform you if there are multiple different instances of the same class.");
        out.println("In this case, care needs to be taken to ensure that these two different instances");
        out.println("of the same class do not cause a conflict and perhaps the bundles need to be ");
        out.println("modified so that there is only one version of this class.");
        out.println("</UL>");
        out.println("In addition with the <A HREF=\"" + PackageServlet.PATH + "\">package debug servlet</A>");
        out.println("you can determine whether the import statements in the bundle Manifests are allowing ");
        out.println("the bundle needing the class to see it.<P>");
    }
    
    private void doForm(PrintWriter out, String className) {
        out.println("<HTML><HEAD><TITLE>Bundle Class Loader Debugger</TITLE>"
                       + "</HEAD><BODY>");
        out.println("<FORM METHOD=\"GET\"/>");
        out.println("<P>Class Name: <INPUT TYPE=\"text\" NAME=\"" + CLASS_NAME_FIELD + "\" ");
        if (className != null) {
            out.println("VALUE=\"" + className + "\"");
        }
        out.println("><P>");
        out.println("<INPUT TYPE=\"SUBMIT\" VALUE=\"SUBMIT\"/>");
        out.println("</FORM>");
    }
    
    private void doResults(PrintWriter out, String className) {
        out.println("<P>");
        Map<Class, Set<Bundle>> loadableClassMap = new HashMap<Class, Set<Bundle>>();
        Map<Bundle, Throwable> exceptions = new HashMap<Bundle, Throwable>();
        for (Bundle b : context.getBundles()) {
            Class c = null;
            try {
               c = b.loadClass(className); 
            }
            catch(Throwable t) {
                exceptions.put(b, t);
            }
            if (c != null) {
                Set<Bundle> bundles = loadableClassMap.get(c);
                if (bundles == null) {
                    bundles = new HashSet<Bundle>();
                    loadableClassMap.put(c, bundles);
                }
                bundles.add(b);
            }
        }
        if (loadableClassMap.size() == 0) {
            out.println("<P>No bundle could load this class<P>");
        }
        else {
            out.println("<UL>");
            for (Map.Entry<Class, Set<Bundle>> entry : loadableClassMap.entrySet()) {
                Class c = entry.getKey();
                Set<Bundle> bundles = entry.getValue();
                out.println("<li>");
                displayClass(out, c, bundles);
            }
            out.println("</UL>");
        }
    }
    
    
    private void displayClass(PrintWriter out, Class c, Set<Bundle> bundles) {
        Bundle loadingBundle = packageAdmin.getBundle(c);
        if (loadingBundle == null) {
            out.println("Found Class " + c + " not associated with any bundle");
        }
        else {
            out.println("Found Class " + c + " loaded by Bundle ");
            PackageServlet.printBundle(out, loadingBundle, PackageServlet.PATH);
            bundles.remove(loadingBundle);
        }
        if (bundles.size() >= 1) {
            out.println("<UL>");
            for (Bundle b : bundles) {
                out.println("<li> Seen by bundle: ");
                PackageServlet.printBundle(out, b, PackageServlet.PATH);
            }
            out.println("</UL>");
        }
    }
    
    
    private Throwable rootException(Throwable  t) {
        Throwable nextException;
        while ((nextException = t.getCause()) != null) {
            t = nextException;
        }
        return t;
    }

    
    
}
