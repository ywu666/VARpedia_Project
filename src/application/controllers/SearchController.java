package application.controllers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.items.NewCreation;
import application.tasks.BashCommand;
import application.tasks.DownloadImagesTask;
import application.tasks.SearchWikiTask;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

public class SearchController extends Controller {

	@FXML private TextField field;
	@FXML private TextArea results;
	@FXML private ProgressIndicator searchIndicator;

	private String searchTerm;

	@FXML
	private void handleMenu() {
		MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
		controller.setUpMenu();
	}

	@FXML
	private void handleSearch() {
		searchTerm = field.getText().trim().toLowerCase();

		// Checks the user has entered a search term
		if (searchTerm == null || "".equals(searchTerm) || searchTerm.length() == 0) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please enter a valid term", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();

		} else {
			SearchWikiTask task = new SearchWikiTask(searchTerm);

			// Shows a term is being searched through a progress indicator
			searchIndicator.setOpacity(1);
			searchIndicator.progressProperty().bind(task.progressProperty());

			task.setOnSucceeded((succeededEvent) -> {
				searchIndicator.progressProperty().unbind();
				searchIndicator.setOpacity(0);

				BashCommand bashCommand = task.getBashCommand();
				String searchResult = bashCommand.getStdOutString();
				if (searchResult.equals(searchTerm + " not found :^(")) { // Alert user if term cannot be found
					Alert alertInvalid = new Alert(Alert.AlertType.WARNING, searchTerm + " cannot be found. Please enter a valid term.", ButtonType.OK);
					alertInvalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
					alertInvalid.showAndWait();

				} else {
					results.setText(searchResult.trim());
				}
			});

			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(task);
			executorService.shutdown();
		}
	}

	@FXML
	private void handleContinue() {
		String text = results.getText();

		// Checks user has searched a term and that there is text in the editable text field
		if (text.equals("") || searchTerm == null) {
			Alert alertInvalid = new Alert(Alert.AlertType.WARNING, "Please search a term before continuing.", ButtonType.OK);
			alertInvalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertInvalid.showAndWait();

		} else {
			NewCreation creation = new NewCreation(searchTerm, results.getText());

			// download the images of the search term
			DownloadImagesTask task2 = new DownloadImagesTask(searchTerm);
			ExecutorService executorService2 = Executors.newSingleThreadExecutor();
			executorService2.execute(task2);
			executorService2.shutdown();

			AudioController controller = (AudioController) loadView("../resources/Audio.fxml", this);
			controller.initialiseController(creation);
		}
	}

	/**
	 * Searches term in the search field when presses enter key
	 * @param event
	 */
	@FXML
	private void handleEnterKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			handleSearch();
		}
	}
}
