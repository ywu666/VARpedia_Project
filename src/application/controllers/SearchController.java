package application.controllers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.items.AlertBox;
import application.items.NewCreation;
import application.tasks.DownloadImagesTask;
import application.tasks.SearchWikiTask;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controller for search view. This view allows the user to search terms, with the results
 * appearing in an editable text area below.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
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
		// Gets search term entered by user to lower case so that case does not stop the user from getting a result
		searchTerm = field.getText().trim().toLowerCase();

		// Warns the user if they have not entered a search term
		if (searchTerm == null || "".equals(searchTerm) || searchTerm.length() == 0) {
			AlertBox.showWaitAlert(AlertType.WARNING, "Please enter a valid term");

		} else {
			SearchWikiTask searchTask = new SearchWikiTask(searchTerm);

			// Shows a term is being searched in the a progress indicator
			searchIndicator.setOpacity(1);
			searchIndicator.progressProperty().bind(searchTask.progressProperty());

			// Search the term
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(searchTask);
			executorService.shutdown();

			// When finished searching the term, hide the search indicator
			searchTask.setOnSucceeded((succeededEvent) -> {
				searchIndicator.progressProperty().unbind();
				searchIndicator.setOpacity(0);

				// Get the result of the search, warning the user if the search was unsuccessful
				String searchResult = searchTask.getResult();
				if (searchResult.equals(searchTerm + " not found :^(")) {
					AlertBox.showWaitAlert(AlertType.WARNING, searchTerm + " cannot be found. Please enter a valid term.");

				} else {
					// If the search was successful, put the result in the text area below
					results.setText(searchResult.trim());
				}
			});
		}
	}

	@FXML
	private void handleContinue() {
		String text = results.getText();

		// Checks user has searched a term and that there is text in the editable text field, warning them if not
		if (text.equals("") || searchTerm == null) {
			AlertBox.showWaitAlert(AlertType.WARNING, "Please search a term before continuing.");

		} else {
			NewCreation creation = new NewCreation(searchTerm, results.getText());

			// Downloads the images based on the search term
			DownloadImagesTask downloadTask = new DownloadImagesTask(searchTerm);
			ExecutorService executorService2 = Executors.newSingleThreadExecutor();
			executorService2.execute(downloadTask);
			executorService2.shutdown();

			AudioController controller = (AudioController) loadView("../resources/Audio.fxml", this);
			controller.initialiseController(creation);
		}
	}

	/**
	 * Searches term in the search field when enter key is pressed.
	 * 
	 * @param event The KeyEvent
	 */
	@FXML
	private void handleEnterKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			handleSearch();
		}
	}
}
