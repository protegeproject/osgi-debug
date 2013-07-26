/**
 * 
 */
package org.protege.osgi.graph;

import org.apache.commons.collections15.Transformer;
import org.osgi.framework.Bundle;

public class OSGiToolTipTransformer implements Transformer<Bundle, String> {
    public String transform(Bundle b) {
        StringBuffer sb = new StringBuffer();
        sb.append(b.getBundleId());
        sb.append(" - ");
        sb.append(b.getSymbolicName());
        int state = b.getState();
        if (state == Bundle.UNINSTALLED) {
            sb.append(" (Uninstalled)");
        }
        else if (state == Bundle.STOPPING) {
            sb.append(" (Stopping)");
        }
        else if (state == Bundle.STARTING) {
            sb.append(" (Starting)");
        }
        else if (state == Bundle.RESOLVED) {
            sb.append(" (Resolved)");
        }
        else if (state == Bundle.INSTALLED) {
            sb.append(" (Installed)");
        }
        else if (state == Bundle.ACTIVE) {
            sb.append(" (Active)");
        }
        return sb.toString();
    }
}