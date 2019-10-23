package application.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import application.items.Creation;
import application.items.NewCreation;
import application.tasks.BashCommand;
import application.tasks.CreateCreationTask;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class CreateController {

	@FXML TextField creationName;
	@FXML ProgressBar progressBar;
	@FXML Button selectAll;
	@FXML FlowPane holder;
	@FXML Button back;

	private int numSelectedImg;
	private Boolean creating = false;
	private NewCreation newCreation;
	  private List<Pane> listPane = new ArrayList<>();
	    private List<ImageController> listImages= new ArrayList<>();
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
					FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Menu.fxml"));
					Parent root = loader.load();
					MenuController controller = loader.getController();
					controller.setUpMenu();
					Main.setStage(root);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else { // Creation is in the process of being created, will go back to menu with no issue
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Menu.fxml"));
				Parent root = loader.load();
				MenuController controller = loader.getController();
				controller.setUpMenu(newCreation);
				Main.setStage(root);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleBack() {
		
		BashCommand rmAudio = new BashCommand("rm -r .newTerm/audio.wav .newTerm/audio");
		rmAudio.run();
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Audio.fxml"));
			Parent root = loader.load();
			AudioController controller = loader.getController();
			controller.initialiseController(newCreation);
			Main.setStage(root);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleCreate() {
		back.setDisable(true);
		
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
			if(numSelectedImg == 0) {
				Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select at least one images.", ButtonType.OK);
				alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alertEmpty.showAndWait();

			} else {
				newCreation.setNumImages(numSelectedImg);
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
						FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Menu.fxml"));
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
	}

	@FXML
	public void handleSelectAll() {		   
		for(ImageController image: listImages) {
            image.selectedImage();
			
		}
	}

	public void initialiseController(NewCreation creation) {
		this.newCreation = creation;

		String term = creation.getTerm();
		List<Creation> listCreations = Creation.getCreations();
		int count = 0;
		String supplyName = term;
		while (listCreations.contains(new Creation(supplyName))) {
			count++;
			supplyName = term + "-" + count;
		}

		creationName.setText(supplyName);

		// Initialize all ten images
		for(int i=0;i<10;i++) {			
			try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Image.fxml"));
			Pane newPane = loader.load();
			ImageController c = loader.getController();
			File fileUrl = new File(".newTerm/images/" + i +".jpg");
			Image image = new Image(fileUrl.toURI().toString());
			c.setImage(image,i);
			listPane.add(newPane);
			listImages.add(c);
			
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
		holder.getChildren().clear();
		holder.getChildren().addAll(listPane);
	}


	public void selectedImages() {
        for(ImageController image: listImages) {
      	  if(image.checkSelectedImage()) {
    			numSelectedImg++;
    			String command ="cp .newTerm/images/"+image.imgNum()+".jpg .newTerm/selectedImages/"+image.imgNum()+".jpg" ;
    			BashCommand bash = new BashCommand(command);
    			bash.run(); 
      	  }
        }
	}
}
