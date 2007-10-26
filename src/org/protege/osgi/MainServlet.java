package org.protege.osgi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 3334728889947569432L;
    
    public static final String PATH="/debug";

    @Override
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML><HEAD><TITLE>OSGi Debug Servlets</TITLE>"
                    + "</HEAD><BODY>");
        out.println("<HEADING>Debug Utilities</HEADING>");
        out.println("<P><A HREF=\"" + ClassLoaderServlet.PATH + "\">Class Loading Debug</A>");
        out.println("<P><A HREF=\"" + PackageServlet.PATH + "\">Package Debug</A>");
        out.println("</BODY></HTML>");
    }

}
