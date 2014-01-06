package cz.martinbayer.e4.analyser.palette;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ProcessorsContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		Collection<RootPaletteItem> roots = (Collection<RootPaletteItem>) inputElement;
		Object[] elements = roots.toArray();
		return elements;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof RootPaletteItem) {
			return ((RootPaletteItem) parentElement).getChildren().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof SubPaletteItem) {
			return ((SubPaletteItem) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof RootPaletteItem) {
			return (((RootPaletteItem) element).getChildren().size() > 0);
		}
		return false;
	}
}
