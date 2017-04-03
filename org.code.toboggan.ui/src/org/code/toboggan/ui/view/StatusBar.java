package org.code.toboggan.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class StatusBar extends Composite {
	private Label label;
	private Color color;

	public StatusBar(Composite parent, int style) {
		super(parent, style);
		this.initialize();
		
		color = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("org.eclipse.ui.editors.backgroundColor");
		if (color != null){
			this.setBackground(color);
		}		
	}

	private void initialize() {		
		RowLayout layout = new RowLayout();
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.fill = true;
		this.setLayout(layout);
		
		label = new Label(this, SWT.NONE);
		if (color != null){
			label.setBackground(color);
		}
	}

	public void setStatus(String status) {
		label.setText("Status: " + status);
		getShell().layout(true, true);
	}
}
