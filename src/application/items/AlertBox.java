package application.items;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

/**
 * Class allowing user to show confirmation alerts and show and wait alerts.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class AlertBox {

	/**
	 * Shows an alert, that waits for the user to click OK.
	 * 
	 * @param type The type of the alert
	 * @param message The message that should be shown with the alert
	 */
	public static void showWaitAlert(AlertType type, String message) {
		Alert alert = new Alert(type, message, ButtonType.OK);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

	/**
	 * Shows an alert with a type of CONFIRMATION, waiting for a user to confirm or cancel.
	 * 
	 * @param message The message that should be shown with the alert
	 * @return whether the user has confirmed or cancelled
	 */
	public static boolean confirmationAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
		
		if (alert.getResult() == ButtonType.OK) {
			return true;
		}

		return false;
	}
}
