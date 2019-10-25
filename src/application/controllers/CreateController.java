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

/**
 * Controller for the create view. This view allows the user to determine which images to include in the creation,
 * the creation name and to tell the program to make the creation.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
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

		// If the creation is not being made check that the user means to abandon their creation by exiting the view
		if (creating == false) {
			boolean confirm = AlertBox.confirmationAlert("Are you sure you want to abandon your creation.");
			if (confirm) {

				// Remove the hidden newTerm directory as the user has chosen to abandon the current creation
				BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
				rmNewTermDir.run();

				MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
				controller.setUpMenu();
			}

		} else {
			// If the creation is being made, take the user back to the menu with the new creation shown as a
			// place holder
			MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
			controller.setUpMenu(newCreation);
		}
	}

	@FXML
	private void handleBack() {
		// Remove the audio information saved so that it can be edited
		BashCommand rmAudio = new BashCommand("rm -r .newTerm/audio.wav .newTerm/audio");
		rmAudio.run();

		AudioController controller = (AudioController) loadView("../resources/Audio.fxml", this);
		controller.initialiseController(newCreation);
	}

	@FXML
	public void handleSelectAll() {		   
		for (ImageController image : listImages) {
			image.selectedImage();
		}
	}

	@FXML
	private void handleCreate() {
		// Disable buttons that should not be used when creation is being made and creation process is completed
		back.setDisable(true);
		create.setDisable(true);
		selectAll.setDisable(true);

		// Get the creation name decided by the user and check whether the creation exists
		String name = creationName.getText().trim();
		if (Creation.checkExists(name)) {

			// If the creation exists, allow the user to decide whether to overwrite the current creation
			boolean confirm = AlertBox.confirmationAlert("You already have a creation with this name.\n" +
					"Would you like to overwrite?");

			// If the user decides to overwrite the creation, create the new creation, deleting the old one
			if (confirm) {
				Creation.removeCreation(name);
				newCreation.setCreationName(name);
				handleCreate();
			}

			// If the user has no name entered, alert them
		} else if ("".equals(name)) {
			AlertBox.showWaitAlert(AlertType.WARNING, "Please enter a name for your creation.");

		} else {
			selectedImages();

			// Alert the user if no images have been selected
			if (numSelectedImg == 0) {
				AlertBox.showWaitAlert(AlertType.WARNING, "Please select at least one images.");

			} else {
				newCreation.setNumImages(numSelectedImg);
				newCreation.setCreationName(name);

				// Make the creation
				creating = true;
				CreateCreationTask task = new CreateCreationTask(newCreation);
				Thread thread = new Thread(task);
				thread.start();
				progressBar.progressProperty().bind(task.progressProperty());
				
				
				task.setOnSucceeded((event) -> {
					progressBar.progressProperty().unbind();
					progressBar.setProgress(1);

					// Remove hidden newTerm directory
					BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
					rmNewTermDir.run();

					// Alert user when creation is ready, then return to main menu
					AlertBox.showWaitAlert(AlertType.INFORMATION, "Your creation, " + name + " is now ready.");

					MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
					controller.setUpMenu();
				});
			}
		}
	}

	/**
	 * Adds selected images into another folder so that they can be added to the final creation.
	 */
	public void selectedImages() {
		// Goes through all images checking if they are selected
		for (ImageController image : listImages) {
			if (image.checkSelectedImage()) {
				numSelectedImg++;
				
				// Add the image to the selectedImages directory
				String command = "cp .newTerm/images/" + image.imgNum() + ".jpg .newTerm/selectedImages/" + image.imgNum() + ".jpg"; 
				BashCommand bash = new BashCommand(command);
				bash.run(); 
			}
		}
	}

	/**
	 * Initializes the controller for this view based on the creation passed in. Initializing the name prompt and
	 * the images.
	 * 
	 * @param creation The object containing the information about the image
	 */
	public void initialiseController(NewCreation creation) {
		this.newCreation = creation;
		setCreationNamePrompt(creation.getTerm());
		showImages();
	}
	
	/**
	 * Set the initial text in the creation name field based on the term.
	 * Makes sure the name provided is not the same as a current creation.
	 * 
	 * @param term The term that the creation being made is based on
	 */
	private void setCreationNamePrompt(String term) {
		List<Creation> listCreations = Creation.getCreations();

		// Determines what the creation name should be, making sure it does not provide a name already used by a
		// current creation
		String supplyName = term;
		int count = 0;
		while (listCreations.contains(new Creation(supplyName))) {
			count++;
			supplyName = term + "-" + count;
		}
		
		// Sets the prompt text
		creationName.setText(supplyName);
	}

	/**
	 * Adds the images to the view and gets the controllers for them.
	 */
	private void showImages() {
		
		// Goes through all 10 images to be shown getting the list of controllers and panes
		for (int i = 0; i < 10; i++) {
			
			// Gets image controller and pane, adding them to the lists
			ImageController controller = (ImageController) loadView("../resources/Image.fxml", this, false);
			listImages.add(controller);
			listPane.add(controller.getPane());
			
			// Sets the image for the controller
			File fileUrl = new File(".newTerm/images/" + i + ".jpg");
			Image image = new Image(fileUrl.toURI().toString());
			controller.setImage(image, i);
		}

		// Adds the image panes to the view
		holder.getChildren().clear();
		holder.getChildren().addAll(listPane);
	}
}
