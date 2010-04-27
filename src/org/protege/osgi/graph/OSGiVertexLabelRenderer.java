/**
 * 
 */
package org.protege.osgi.graph;

import java.util.Dictionary;

import org.apache.commons.collections15.Transformer;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class OSGiVertexLabelRenderer implements Transformer<Bundle, String> {
    
    @SuppressWarnings("unchecked")
    public String transform(Bundle vertex) {
        if (vertex instanceof Bundle) {
            Dictionary<String, String> headers = vertex.getHeaders();
            String name = headers.get(Constants.BUNDLE_NAME);
            return name == null ? vertex.getSymbolicName() : name;
        }
        return null;
    }

}