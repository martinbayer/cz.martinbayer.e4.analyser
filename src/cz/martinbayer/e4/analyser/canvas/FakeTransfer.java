package cz.martinbayer.e4.analyser.canvas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItemDnDData;

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
		if (!checkMyType(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		CanvasItemDnDData dndData = (CanvasItemDnDData) object;
		try {

			// write data to a byte array and then ask super to convert to
			// pMedium
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(out);
			objectStream.writeObject(dndData);
			byte[] buffer = out.toByteArray();
			objectStream.close();
			super.javaToNative(buffer, transferData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null)
				return null;
			CanvasItemDnDData dndData = new CanvasItemDnDData();
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				ObjectInputStream readIn = new ObjectInputStream(in);
				dndData = (CanvasItemDnDData) readIn.readObject();
				readIn.close();
			} catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace();
				return null;
			}
			return dndData;
		}
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

	boolean checkMyType(Object object) {
		if (object == null || !(object instanceof CanvasItemDnDData)) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean validate(Object object) {
		return checkMyType(object);
	}
}