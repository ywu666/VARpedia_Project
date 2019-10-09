package application;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

public class CreateController {
	
	@FXML TextField creationName;
	@FXML ProgressBar progressBar;
	
	@FXML ImageView image1;
	@FXML ImageView image2;
	@FXML ImageView image3;
	@FXML ImageView image4;
	@FXML ImageView image5;
	@FXML ImageView image6;
	@FXML ImageView image7;
	@FXML ImageView image8;
	@FXML ImageView image9;
	@FXML ImageView image10;
	
	
	@FXML CheckBox box1;
	@FXML CheckBox box2;
	@FXML CheckBox box3;
	@FXML CheckBox box4;
	@FXML CheckBox box5;
	@FXML CheckBox box6;
	@FXML CheckBox box7;
	@FXML CheckBox box8;
	@FXML CheckBox box9;
	@FXML CheckBox box10;
	
	private int numImages;
	
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
			
		} else if ("".equals(name)) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please enter a name for your creation.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else { // If creation name is valid, make the creation
			
			selectedImages();
			newCreation.setNumImages(numImages);
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
		setImageView(image1,0);
		setImageView(image2,1);
		setImageView(image3,2);
		setImageView(image4,3);
		setImageView(image5,4);
		setImageView(image6,5);
		setImageView(image7,6);
		setImageView(image8,7);
		setImageView(image9,8);
		setImageView(image10,9);
	}
	
	public void setImageView(ImageView imageView,int num) {
		File fileUrl = new File(".newTerm/images/" + num +".jpg");
	    Image image = new Image(fileUrl.toURI().toString());
	    imageView.setImage(image);
	}

	public void selected(CheckBox box,int num) {
		if(box.isSelected()) {
			numImages++;
			String command ="cp .newTerm/images/"+num+".jpg .newTerm/selectedImages/"+num+".jpg" ;
			BashCommand bash = new BashCommand(command);
			bash.run();
		}
	}

	public void selectedImages() {
		selected(box1,0);
		selected(box2,1);
		selected(box3,2);
		selected(box4,3);
		selected(box5,4);
		selected(box6,5);
		selected(box7,6);
		selected(box8,7);
		selected(box9,8);
		selected(box10,9);
	}
}
