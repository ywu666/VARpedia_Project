package application;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class MenuController {
	
	@FXML private Button play;
	@FXML private Button delete;
	@FXML private Button quit;
	@FXML private Button create;
	@FXML private ListView listCreations;
	
	@FXML
	private void handlePlay() {
		
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
}
