/**
 * 
 */
package cz.martinbayer.e4.analyser.palette;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import cz.martinbayer.analyser.processorsPool.ProcessorPluginWrapper;
import cz.martinbayer.analyser.processorsPool.ProcessorsPool;

/**
 * @author Martin Bayer
 */
public class PalettePart {

	public static final String ID = "cz.martinbayer.e4.analyser.palette.PalettePart";

	Label l;

	@Inject
	private MApplication application;

	private TreeViewer viewer;

	@PostConstruct
	public void postConstruct(Composite parent) {

		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ProcessorsContentProvider());
		viewer.setLabelProvider(new ProcessorsLabelProvider());

		List<ProcessorPluginWrapper> processors = ProcessorsPool.getInstance()
				.getProcessors();
		List<RootPaletteItem> roots = PaletteItemResolver
				.resolveProcessors(processors);

		initOtherItems(roots);
		viewer.setInput(roots);
		viewer.expandAll();
		viewer.addSelectionChangedListener(new PaletteItemSelectedListener(
				application));
	}

	private void initOtherItems(List<RootPaletteItem> roots) {
		roots.add(new OtherPaletteItems());
	}

	@Focus
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
