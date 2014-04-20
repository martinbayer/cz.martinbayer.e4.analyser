package cz.martinbayer.e4.analyser.persistence;

import java.io.File;

import org.eclipse.e4.core.contexts.IEclipseContext;

import cz.martinbayer.e4.analyser.ContextVariables;

/**
 * Class used to handle actually opened file
 */
public class ActualFileHelper {
	public static final String ACTUAL_PROJECT_FILE = ContextVariables.ACTUAL_PROJECT_FILE;

	/**
	 * Stores actually used file to eclipse context. It must be initialized
	 * when:
	 * <ul>
	 * <li>some project is opened</li>
	 * <li>Save As is used for project (since now the Save will save project to
	 * Save As location)</li>
	 * </ul>
	 * It must be deleted when:
	 * <ul>
	 * <li>New project is created (Save will check value whether it is null or
	 * not. If null, then Save As dialog is opened)</li>
	 * </ul>
	 * 
	 * @param ctx
	 * @param actualFile
	 */
	public static void setActualFile(IEclipseContext ctx, File actualFile) {
		ctx.set(ACTUAL_PROJECT_FILE, actualFile);
	}

	public static File getActualFile(IEclipseContext ctx) {
		return (File) ctx.get(ACTUAL_PROJECT_FILE);
	}
}
