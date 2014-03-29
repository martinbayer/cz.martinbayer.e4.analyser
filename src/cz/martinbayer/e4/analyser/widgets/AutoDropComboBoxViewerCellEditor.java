package cz.martinbayer.e4.analyser.widgets;

import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class AutoDropComboBoxViewerCellEditor extends ComboBoxViewerCellEditor {
	private TableViewer tableViewer;

	public AutoDropComboBoxViewerCellEditor(Composite parent,
			TableViewer tableViewer) {
		super(parent, SWT.READ_ONLY);
		this.tableViewer = tableViewer;
		setActivationStyle(DROP_DOWN_ON_MOUSE_ACTIVATION);
	}

	@Override
	protected Control createControl(Composite parent) {
		Control control = super.createControl(parent);
		CCombo cCombo = super.getViewer().getCCombo();
		enableApplyEditorValueOnListSelection(cCombo);
		return control;
	}

	private long timeOfNonFinalSelectionEvent = 0;

	/**
	 * Solves the problem that selecting an element in the combo viewer does not
	 * directly apply the value selected from it's list, see <a
	 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=230398">this bug
	 * report</a>. We must not apply the value if the user is still busy
	 * selecting a value with the keyboard up/down arrows or mousewheel. See <a
	 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=54989">this bug
	 * report</a>.
	 */
	private void enableApplyEditorValueOnListSelection(final CCombo cCombo) {
		cCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isFinalSelection(e)) {
					fireApplyEditorValue();
					tableViewer.refresh();
				}
			}

			private boolean isFinalSelection(SelectionEvent e) {
				return !cCombo.getListVisible()
						&& e.time != timeOfNonFinalSelectionEvent;
			}
		});
		cCombo.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseScrolled(MouseEvent e) {
				timeOfNonFinalSelectionEvent = e.time;
			}
		});
		cCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN) {
					timeOfNonFinalSelectionEvent = e.time;
				}
			}
		});
	}
}