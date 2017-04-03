package org.code.toboggan.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class VerticalButtonBar extends Composite {
	private Button plusButton;
	private Button minusButton;
	private Button reloadButton;

	public VerticalButtonBar(Composite parent, int style) {
		super(parent, style);
		this.initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.verticalSpacing = 2;
		gridLayout.marginHeight = 2;
		gridLayout.marginWidth = 2;
		this.setLayout(gridLayout);

		plusButton = new Button(this, SWT.NONE);
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		data.heightHint = 30;
		data.widthHint = 30;
		plusButton.setLayoutData(data);
		plusButton.setText("+");
		plusButton.setEnabled(false);

		minusButton = new Button(this, SWT.NONE);
		GridData data2 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		data2.heightHint = 30;
		data2.widthHint = 30;
		minusButton.setLayoutData(data2);
		minusButton.setText("-");
		minusButton.setEnabled(false);

		reloadButton = new Button(this, SWT.NONE);
		GridData reloadData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		reloadData.heightHint = 30;
		reloadData.widthHint = 30;
		reloadButton.setLayoutData(reloadData);
		reloadButton.setText("\u21BA");
		reloadButton.setEnabled(false);
	}

	public Button getPlusButton() {
		return plusButton;
	}

	public Button getMinusButton() {
		return minusButton;
	}

	public Button getReloadButton() {
		return reloadButton;
	}
}
