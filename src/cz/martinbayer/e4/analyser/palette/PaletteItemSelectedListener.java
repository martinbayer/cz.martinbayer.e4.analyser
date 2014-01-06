package cz.martinbayer.e4.analyser.palette;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import cz.martinbayer.e4.analyser.ContextVariables;

public class PaletteItemSelectedListener implements ISelectionChangedListener {

	private MApplication application;

	public PaletteItemSelectedListener(MApplication application) {
		this.application = application;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		this.application.getContext()
				.set(ContextVariables.PALETTE_SELECTED_ITEM,
						((IStructuredSelection) event.getSelection())
								.getFirstElement());
	}

}
