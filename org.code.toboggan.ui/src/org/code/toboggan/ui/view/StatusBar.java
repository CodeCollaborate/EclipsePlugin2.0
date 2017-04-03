package org.code.toboggan.ui.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StatusBar extends Composite {
	private Logger logger = LogManager.getLogger(this.getClass());

	private Label label;

	public StatusBar(Composite parent, int style) {
		super(parent, style);
		this.initialize();
		this.setBackground(new Color(null, 245, 245, 245)); // Grey
	}

	private void initialize() {
		logger.debug("UI-DEBUG: ");
		RowLayout layout = new RowLayout();
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		this.setLayout(layout);
		label = new Label(this, SWT.NONE);
	}

	public void setStatus(String status) {
		label.setText("Status: " + status);
	}
}