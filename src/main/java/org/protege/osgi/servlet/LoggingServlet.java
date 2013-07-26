package org.protege.osgi.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LoggingServlet extends HttpServlet {
    private static final long serialVersionUID = -1826004792504173966L;

    public static final String PATH="/debug/logging";
    public static final String LOGGING_PATH_FIELD  = "loggingPath";
    public static final String LOGGING_LEVEL_FIELD = "loggingLevel";
    public static final String SUBMIT_REQUEST      = "Submit";
    public static final String SUBMIT_GET_LEVEL    = "getLevel";
    public static final String SUBMIT_SET_LEVEL    = "setLevel";
    public static final Level[] LEVELS = { Level.DEBUG, Level.INFO, Level.ERROR, Level.FATAL };
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String loggingPath = getLoggingPath(request);
        Level loggingLevel = getLoggingLevel(request);
        Logger logger = null;
        if (loggingPath != null) {
            logger = Logger.getLogger(loggingPath);
        }
        
        String requestType = request.getParameter(SUBMIT_REQUEST);
        if (SUBMIT_SET_LEVEL.equals(requestType) && loggingLevel != null && logger != null) {
            logger.setLevel(loggingLevel);
        }
        
        Level currentLevel = null;
        if (loggingPath != null) {
            currentLevel = logger.getEffectiveLevel();
        }
        
        doForm(response.getWriter(), loggingPath, currentLevel);
        
    }
    
    private void doForm(PrintWriter out, String loggingPath, Level currentLevel) {
        out.println("<HTML><HEAD><TITLE>Loging Level Configuration</TITLE>"
                       + "</HEAD><BODY>");
        out.println("<FORM METHOD=\"GET\"/>");
        out.println("<P>Logging Level: <SELECT NAME=\"" + LOGGING_LEVEL_FIELD + "\">");
        for (Level level : LEVELS) {
            out.println("<OPTION VALUE=\"" + level + "\" ");
            if (level == currentLevel) {
                out.println("SELECTED=\"TRUE\"");
            }
            out.println(">" + level);
            out.println("</OPTION>");
        }
        out.println("</SELECT></P>");
        out.println("<P>LoggingPath: <INPUT TYPE=\"text\" NAME=\"" + LOGGING_PATH_FIELD + "\"");
        if (loggingPath != null) {
            out.println(" VALUE=\"" + loggingPath + "\"");
        }
        out.println("/></P><P>");
        out.println("<INPUT TYPE=\"SUBMIT\" NAME=\"" + SUBMIT_REQUEST + "\" VALUE=\"" + SUBMIT_GET_LEVEL + "\"/>");
        out.println("<INPUT TYPE=\"SUBMIT\" NAME=\"" + SUBMIT_REQUEST + "\" VALUE=\"" + SUBMIT_SET_LEVEL + "\"/>");
        out.println("</FORM>");
    }
    
    private String getLoggingPath(HttpServletRequest request) {
        String name = request.getParameter(LOGGING_PATH_FIELD);
        if (name == null) {
            return null;
        }
        name = name.replace('/', '.');
        return name;
    }
    
    private Level getLoggingLevel(HttpServletRequest request) {
        String name = request.getParameter(LOGGING_LEVEL_FIELD);
        for (Level level : LEVELS) {
            if (level.toString().equals(name)) {
                return level;
            }
        }
        return null;
    }
}
