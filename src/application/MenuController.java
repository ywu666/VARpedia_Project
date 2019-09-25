package application;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

public class MenuController {
	
	@FXML private Button play;
	@FXML private Button delete;
	@FXML private Button quit;
	@FXML private Button create;
	@FXML private ListView<String> listCreations;
	
	@FXML
	private void handlePlay() {
		String selection = listCreations.getSelectionModel().getSelectedItem();
		
		if (selection == null) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "You have not selected an item in the list to play.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
		} else {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/MediaPlay.fxml"));
				Parent root = loader.load();
				MediaPlayController controller = loader.getController();
				controller.playCreation(selection);
				Main.setStage(root);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleDelete() {
		
	}
	
	@FXML
	private void handleCreate() {
		
		
		try {
	        Parent root = FXMLLoader.load(getClass().getResource("resources/Search.fxml"));
			Main.setStage(root);
			
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	@FXML
	private void handleQuit() {
		Platform.exit();
		System.exit(0);
	}
	
	@FXML
	public void setUpMenu() {
		String command = "ls creations | sort | sed 's/\\.mp4$//'";
		BashCommand getCreations = new BashCommand(command, true);
		getCreations.run();
		List<String> creations = getCreations.getStdOutList();
		
		listCreations.getItems().addAll(creations);
	}
}
