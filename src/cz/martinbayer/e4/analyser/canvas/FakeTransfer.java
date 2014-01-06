package cz.martinbayer.e4.analyser.canvas;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * transfer type used only to support DnD on canvas - items are just relocated
 * so there is no need to do more
 * 
 * @author Martin
 * 
 */
public class FakeTransfer extends ByteArrayTransfer {

	private static final String MYTYPENAME = "CanvasItem";

	private static final int MYTYPEID = registerType(MYTYPENAME);

	private static FakeTransfer _instance = new FakeTransfer();

	public static FakeTransfer getInstance() {
		return _instance;
	}

	@Override
	public void javaToNative(Object object, TransferData transferData) {
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		return null;
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { MYTYPENAME };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { MYTYPEID };
	}
}