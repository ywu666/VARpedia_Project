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
	
	private String searchTerm;
	
	@FXML
	private void handleMenu() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
			Parent root = loader.load();
			MenuController controller = loader.getController();
			controller.setUpMenu();
			Main.setStage(root);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleSearch() {
		
		searchTerm = field.getText().trim().toLowerCase();
		
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
			}
		}
	}
	
	@FXML
	private void handleSubmitText() {
		Creation creation = new Creation(searchTerm, results.getText());
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Speech.fxml"));
			Parent root = loader.load();
			SpeechController controller = loader.getController();
			controller.initialiseController(creation);
			Main.setStage(root);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
