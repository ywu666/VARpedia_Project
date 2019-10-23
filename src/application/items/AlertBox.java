package application.items;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

public class AlertBox {

	public static void showWaitAlert(AlertType type, String message) {
		Alert alert = new Alert(type, message, ButtonType.OK);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

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
