package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

public class SearchController {
	
	@FXML private Button menu;
	@FXML private Button submitText;
	@FXML private Button search;
	@FXML private TextField field;
	@FXML private TextArea results;
	
	@FXML
	private void handleMenu() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("resources/Menu.fxml"));
			Main.setStage(root);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleSearch() {
		
		String searchTerm = field.getText().trim().toLowerCase();
		
		if (searchTerm == null || "".equals(searchTerm) || searchTerm.length() == 0) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please enter a valid term", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else {
			String command = "wikit " + searchTerm;
			BashCommand bashCommand = new BashCommand(command, true);
			bashCommand.run();
			String searchResult = bashCommand.getStdOutString();
			
			if (searchResult.equals(searchTerm + " not found :^(")) {
				Alert alertInvalid = new Alert(Alert.AlertType.WARNING, searchTerm + " cannot be found. Please enter a valid term.", ButtonType.OK);
				alertInvalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alertInvalid.showAndWait();
				
			} else {
				results.setText(searchResult.trim());
				results.setWrapText(true);
			}
		}
	}
	
	@FXML
	private void handleSubmitText() {
		
	}
}
