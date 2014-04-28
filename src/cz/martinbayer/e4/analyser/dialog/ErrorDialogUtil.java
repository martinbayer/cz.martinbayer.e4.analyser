package cz.martinbayer.e4.analyser.dialog;

import javax.print.attribute.standard.Severity;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

public class ErrorDialogUtil {

	public static void showErrorDialog(final Shell shell,
			final Throwable exception) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				ErrorDialog.openError(shell, "Error occured in E4Logsis",
						"Check details for more information about error",
						new Status(Severity.ERROR.getValue(), FrameworkUtil
								.getBundle(ErrorDialogUtil.class)
								.getSymbolicName(), "Exception thrown",
								exception));
			}
		});
	}
}
