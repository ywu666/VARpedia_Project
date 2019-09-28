package application;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

public class CreateController {
	
	@FXML TextField creationName;
	@FXML Slider slider;
	@FXML ProgressBar progressBar;
	
	private Boolean create = false;
	private Creation creation;
	
	@FXML
	private void handleMenu() {
		
		if (create == false) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to abandon your creation.", ButtonType.OK, ButtonType.CANCEL);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			
			if (alert.getResult() == ButtonType.OK) {
				BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
		    	rmNewTermDir.run();
				
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
		} else {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
				Parent root = loader.load();
				MenuController controller = loader.getController();
				controller.setUpMenu(creation.getCreationName());
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleCreate() {
		String name = creationName.getText().trim();
		
		if (!name.matches("^[a-zA-Z0-9_-]+$")) {
			  Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a name for your creation using only " +
			        "alphabetical letters, digits, hyphens and underscores.", ButtonType.OK);
			  alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			  alert.showAndWait();
			 
		} else if (name == null || name.equals("") || name.length() == 0 || name.contains(" ")) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please enter a valid creation name. (Note: no spaces)", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (newTermExists(name)) {
			
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You already have a creation with this name.\n" +
					"Would you like to overwrite?", ButtonType.YES, ButtonType.CANCEL);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.YES) {
					String command = "rm -f creations/" + name + ".mp4";
					BashCommand removeCreation = new BashCommand(command);
					removeCreation.run();
					
					creation.setCreationName(name);
					handleCreate();
				}
			});
			
		} else {
			creation.setNumImages(slider.getValue());
			creation.setCreationName(name);
			
			CreateCreationTask task = new CreateCreationTask(creation);
			Thread thread = new Thread(task);
			thread.start();
			
			create = true;
			
			progressBar.progressProperty().bind(task.progressProperty());

			task.setOnSucceeded((event) -> {
				progressBar.progressProperty().unbind();
				progressBar.setProgress(1);
				
				BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
		    	rmNewTermDir.run();
		    	
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
					Parent root = loader.load();
					MenuController controller = loader.getController();
					controller.setUpMenu();
					Main.setStage(root);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	private Boolean newTermExists(String name) {
		File file = new File("creations/" + name + ".mp4");
		
		return file.exists();
	}
	
	public void initialiseCreateController(Creation creation) {
		this.creation = creation;
	}
}
