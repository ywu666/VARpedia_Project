package application;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

public class SearchController {
	
	@FXML private Button menu;
	@FXML private Button submitText;
	@FXML private Button search;
	@FXML private TextField field;
	@FXML private TextArea results;
	@FXML private ProgressIndicator searchIndicator;
	
	private String searchTerm;
	
	@FXML
	private void handleMenu() {
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
			
			// download the images of the search term
			DownloadImagesTask task2 = new DownloadImagesTask(searchTerm);
			ExecutorService executorService2 = Executors.newSingleThreadExecutor();
			executorService2.execute(task2);
			executorService2.shutdown();
		}
	}
	
	@FXML
	private void handleSubmitText() {
		String text = results.getText();
		
		// Checks user has searched a term and that there is text in the editable text field
		if (text.equals("") || searchTerm == null) {
			Alert alertInvalid = new Alert(Alert.AlertType.WARNING, "Please search a term before continuing.", ButtonType.OK);
			alertInvalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertInvalid.showAndWait();
			
		} else {
			NewCreation creation = new NewCreation(searchTerm, results.getText());
		
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Speech.fxml"));
				Parent root = loader.load();
				SpeechController controller = loader.getController();
				controller.initialiseController(creation);
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	
	/**
	 * Task to search term user specified
	 */
	class SearchWikiTask extends Task<Void> {
			
		String searchTerm;
		BashCommand bashCommand;
		
		SearchWikiTask(String serchTerm) {
			this.searchTerm = serchTerm;
		}

		@Override
		protected Void call() throws Exception {
			
			String command = "wikit \"" + searchTerm + "\"";
			bashCommand = new BashCommand(command, true);
			bashCommand.run();
				
			return null;
		}
		
		public BashCommand getBashCommand() {
			return bashCommand;
		}
	 }
}
