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
	
	private Boolean created = false;
	private Creation creation;
	
	@FXML
	private void handleMenu() {
		
		if (created == false) {
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
	}
	
	@FXML
	private void handleCreate() {
		
		String name = creationName.getText().trim();
		
		if (name == null || name.equals("") || name.length() == 0 || name.contains(" ")) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please enter a valid creation name. (Note: no spaces)", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else {
			System.out.println("Slider val: " + slider.getValue());
			creation.setNumImages(slider.getValue());
			creation.setCreationName(name);
			CreateCreationTask task = new CreateCreationTask(creation);
			Thread thread = new Thread(task);
			thread.start();
			
			progressBar.progressProperty().bind(task.progressProperty());

			task.setOnSucceeded((event) -> {
				progressBar.progressProperty().unbind();
				progressBar.setProgress(1);
				created = true;
			});
		}
	}
	
	public void initialiseCreateController(Creation creation) {
		this.creation = creation;
	}
}
