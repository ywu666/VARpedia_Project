package application.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.items.AlertBox;
import application.items.Creation;
import application.items.NewCreation;
import application.tasks.BashCommand;
import application.tasks.CreateCreationTask;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class CreateController extends Controller {

	@FXML TextField creationName;
	@FXML ProgressBar progressBar;
	@FXML Button selectAll;
	@FXML FlowPane holder;
	@FXML Button back;
	@FXML Button create;

	private int numSelectedImg;
	private Boolean creating = false;
	private NewCreation newCreation;
	private List<Pane> listPane = new ArrayList<>();
	private List<ImageController> listImages= new ArrayList<>();

	@FXML
	private void handleMenu() {
		// Checks the user wants to abandon the creation by going back to 'Menu'.
		if (creating == false) {

			boolean confirm = AlertBox.confirmationAlert("Are you sure you want to abandon your creation.");
			// If they confirmed, the creation will be abandoned otherwise will do nothing
			if (confirm) {
				BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
				rmNewTermDir.run();

				MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
				controller.setUpMenu();
			}

		} else { // Creation is in the process of being created, will go back to menu with no issue

			MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
			controller.setUpMenu(newCreation);
		}
	}

	@FXML
	private void handleBack() {

		BashCommand rmAudio = new BashCommand("rm -r .newTerm/audio.wav .newTerm/audio");
		rmAudio.run();

		AudioController controller = (AudioController) loadView("../resources/Audio.fxml", this);
		controller.initialiseController(newCreation);
	}

	@FXML
	private void handleCreate() {
		back.setDisable(true);
		create.setDisable(true);
		selectAll.setDisable(true);

		String name = creationName.getText().trim();

		if (Creation.checkExists(name)) { // Handle when creation name exists already, check if they want to overwrite
			boolean confirm = AlertBox.confirmationAlert("You already have a creation with this name.\n" +
					"Would you like to overwrite?");
			if (confirm) {
				Creation.removeCreation(name);
				newCreation.setCreationName(name);
				handleCreate();
			}

		} else if ("".equals(name)) {
			AlertBox.showWaitAlert(AlertType.WARNING, "Please enter a name for your creation.");

		} else { // If creation name is valid, make the creation

			selectedImages();
			if(numSelectedImg == 0) {
				AlertBox.showWaitAlert(AlertType.WARNING, "Please select at least one images.");

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
					AlertBox.showWaitAlert(AlertType.INFORMATION, "Your creation, " + name + " is now ready.");

					MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
					controller.setUpMenu();
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
		for(int i=0; i<10; i++) {
			ImageController controller = (ImageController) loadView("../resources/Image.fxml", this, false);
			listImages.add(controller);
			listPane.add(controller.getPane());
			File fileUrl = new File(".newTerm/images/" + i +".jpg");
			Image image = new Image(fileUrl.toURI().toString());
			controller.setImage(image, i);
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
