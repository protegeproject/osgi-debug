package org.protege.osgi;

import java.awt.event.ActionEvent;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.protege.editor.core.ui.action.ProtegeAction;

public class ProtegeMenu extends ProtegeAction {

	@Override
	public void initialise() throws Exception {

	}

	@Override
	public void dispose() throws Exception {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		DebugActivator.startJung(context);
	}

}
