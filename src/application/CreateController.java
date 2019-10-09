package application;

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
	
	private Boolean creating = false;
	private NewCreation newCreation;
	
	@FXML
	private void handleMenu() {
		// Checks the user wants to abandon the creation by going back to 'Menu'.
		if (creating == false) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to abandon your creation.", ButtonType.OK, ButtonType.CANCEL);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			// If they confirmed, the creation will be abandoned otherwise will do nothing
			if (alert.getResult() == ButtonType.OK) {
				BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
		    	rmNewTermDir.run();
				
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
					Parent root = loader.load();
					MenuController controller = loader.getController();
					controller.setUpTable();
					Main.setStage(root);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else { // Creation is in the process of being created, will go back to menu with no issue
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
				Parent root = loader.load();
				MenuController controller = loader.getController();
				controller.setUpMenu(newCreation.getCreationName());
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleCreate() {
		String name = creationName.getText().trim();
		
		if (Creation.checkExists(name)) { // Handle when creation name exists already, check if they want to overwrite
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You already have a creation with this name.\n" +
					"Would you like to overwrite?", ButtonType.YES, ButtonType.CANCEL);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.YES) { // If they want to overwrite create the creation
					
					Creation.removeCreation(name);
					
					newCreation.setCreationName(name);
					handleCreate();
				}
			});
			
		} else { // If creation name is valid, make the creation
			
			newCreation.setNumImages(slider.getValue());
			newCreation.setCreationName(name);
			
			CreateCreationTask task = new CreateCreationTask(newCreation);
			Thread thread = new Thread(task);
			thread.start();
			
			creating = true;
			progressBar.progressProperty().bind(task.progressProperty());
			task.setOnSucceeded((event) -> {
				progressBar.progressProperty().unbind();
				progressBar.setProgress(1);
				
				BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
		    	rmNewTermDir.run();
		    	
		    	// Alert user creation is ready, then return to menu
		    	Alert alert = new Alert(Alert.AlertType.INFORMATION, "Your creation, " + name + " is now ready.", ButtonType.OK);
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.showAndWait();
				  
		    	try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
					Parent root = loader.load();
					MenuController controller = loader.getController();
					controller.setUpTable();
					Main.setStage(root);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	public void initialiseCreateController(NewCreation creation) {
		this.newCreation = creation;
	}
}
