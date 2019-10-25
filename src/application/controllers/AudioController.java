package application.controllers;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.items.AlertBox;
import application.items.Audio;
import application.items.NewCreation;
import application.tasks.BashCommand;
import application.tasks.MergeAudioTask;
import application.tasks.PreviewSpeechTask;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

/**
 * Controller for audio view. This view allows the user to preview and save audio with specified settings.
 * The user can rearrange, delete and play saved audio.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class AudioController extends Controller {

	@FXML private Button preview;
	@FXML private Button save;
	@FXML private Button moveUpButton;
	@FXML private Button moveDownButton;
	@FXML private Button playButton;
	@FXML private Button deleteButton;
	@FXML private Tooltip tip;
	@FXML private TextArea creationText;
	@FXML private ComboBox<String> selectMood;
	@FXML private ComboBox<String> selectVoice;
	@FXML private Label wordCount;
	@FXML TableView<Audio> table;
	@FXML TableColumn<Audio, String> textColumn;
	@FXML TableColumn<Audio, String> voiceColumn;
	@FXML TableColumn<Audio, String> moodColumn;

	private PreviewSpeechTask previewTask;
	private NewCreation creation;
	private static final ArrayList<String> voiceList;
	private static final ArrayList<String> moodList;
	
	static {
		moodList = new ArrayList<String>();
		moodList.add("Happy");
		moodList.add("Neutral");
		moodList.add("Sad");

		voiceList = new ArrayList<String>();
		voiceList.add("United Kingdom");
		voiceList.add("United States");
		voiceList.add("West Indies");
	}

	@FXML
	private void handleMenu() {
		boolean confirm = AlertBox.confirmationAlert("Are you sure you want to abandon your creation.");
		if (confirm) {
			// Remove hidden newTerm folder when user chooses to abandon creation
			BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
			rmNewTermDir.run();

			MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
			controller.setUpMenu();
		}
	}

	@FXML
	private void handlePreview() {
		String mood = selectMood.getValue();
		String voice = Audio.voices.get(selectVoice.getValue());
		String text = creationText.getSelectedText();

		// If there is no voice or mood selected provide default
		if(voice == null) {
			voice = "uk";
		}

		if (mood == null) {
			mood = "Neutral";	
		}

		preview(voice, mood, text);
	}


	@FXML
	private void handleSave() {
		tip.setText("Select an audio file.");

		String mood = selectMood.getValue();
		String voice = selectVoice.getValue();
		String text = creationText.getSelectedText().trim();

		// If there is no voice or mood selected provide default
		if(voice == null) {
			voice = "United Kingdom";
		}

		if (mood == null) {
			mood = "Neutral";	
		}

		// Create audio and add to table and list of audio in creation
		Audio a = new Audio(voice, mood, text);
		table.getItems().add(a);
		creation.addAudio(a);
	}

	@FXML
	private void handleMoveUp() {
		// Get position of selected item in audio table and determine if it is at the top
		int index = table.getSelectionModel().getSelectedIndex();
		boolean isTop = (index == 0);

		// If selected item is not at the top, move item up
		if (!isTop) {
			table.getItems().add(index - 1, table.getItems().remove(index));
			table.getSelectionModel().clearAndSelect(index - 1);
		}
	}

	@FXML
	private void handleMoveDown() {
		// Get position of selected item in audio table and determine if it is at the bottom
		int index = table.getSelectionModel().getSelectedIndex();
		boolean isBottom = index == table.getItems().size() - 	1;

		// If selected item is not at the bottom, move item down
		if (!isBottom) {
			table.getItems().add(index + 1, table.getItems().remove(index));
			table.getSelectionModel().clearAndSelect(index + 1);
		}
	}

	@FXML
	private void handleDelete() {
		// Get saved audio selected in audio table and remove it from the table and audio list in the creation
		Audio selection = table.getSelectionModel().getSelectedItem();
		table.getItems().remove(selection);
		creation.removeAudio(selection);

		// If the table now has no audio files saved remind the user to save one in the help tool-tip
		if (table.getItems().size() == 0) {
			tip.setText("Save an audio file.");
		}
	}

	@FXML
	private void handlePlay() {
		Audio selection = table.getSelectionModel().getSelectedItem();
		String voice = Audio.voices.get(selection.getVoice());
		preview(voice, selection.getMood(), selection.getText());
	}

	@FXML
	private void handleContinue() {
		creation.setAudioList(table.getItems());

		// Cancel the current preview if there is a current preview task
		if (previewTask != null) {
			previewTask.cancel();
		}

		// If there is no audio saved, tell the user to save some
		if (table.getItems().isEmpty()) {
			AlertBox.showWaitAlert(AlertType.WARNING, "Save an audio file(s) before continuing.");

		} else {
			// Merge the audio
			Object[] array = table.getItems().toArray();
			MergeAudioTask task = new MergeAudioTask(array);
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(task);
			executorService.shutdown();

			CreateController controller = (CreateController) loadView("../resources/Create.fxml", this);
			controller.initialiseController(creation);
		}
	}

	/**
	 * Starts the preview for the given settings and text.
	 * 
	 * @param voice The voice option chosen by the user
	 * @param mood The mood option chosen by the user
	 * @param text The text the voice will speak
	 */
	private void preview(String voice, String mood, String text) {
		// Cancel the current preview if there is a current preview task
		if (previewTask != null) {
			previewTask.cancel();
		}

		// Previews audio on new thread so that the user can continue using the application at the same time
		previewTask = new PreviewSpeechTask("en-" + voice, mood, text);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(previewTask);
		executorService.shutdown();
	}

	/**
	 * Sets up the controller for this page given the creation. Initializing the audio table, combo
	 * box options and the disable buttons listener.
	 * 
	 * Sets up the table listing the audio files saved and their corresponding settings
	 * @param c The object containing the creation information passed when the controller is initialized
	 */
	public void initialiseController(NewCreation c) {
		this.creation = c;
		setUpListener();		
		setUpAudioTable();

		// Set the text of the text area to what was decided in the search page
		creationText.setText(creation.getText());

		// Set the options for the combo boxes containing the mood and voice options
		selectMood.setItems(FXCollections.observableArrayList(moodList));
		selectVoice.setItems(FXCollections.observableArrayList(voiceList));
	}

	/**
	 * Sets up the audio table to hold all saved audio information.
	 */
	private void setUpAudioTable() {
		textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
		voiceColumn.setCellValueFactory(new PropertyValueFactory<>("voice"));
		moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
		table.getItems().addAll(FXCollections.observableList(creation.getAudioList()));

		// Disable the buttons controlling the saved audio unless an item has been selected in the table.
		BooleanBinding selected = Bindings.isEmpty(table.getSelectionModel().getSelectedItems());
		moveUpButton.disableProperty().bind(selected);
		moveDownButton.disableProperty().bind(selected);
		playButton.disableProperty().bind(selected);
		deleteButton.disableProperty().bind(selected);

		// Wrap the contents of the text column to resize based on how much text is in it
		textColumn.setCellFactory(tc -> {
			TableCell<Audio, String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
			text.wrappingWidthProperty().bind(textColumn.widthProperty().subtract(10));
			text.setStyle("-fx-fill: -fx-text-background-color");
			text.textProperty().bind(cell.itemProperty());	
			return cell;
		});
	}

	/**
	 * Sets up the listener or when the selected text changes in the creation text TextArea.
	 * The listener decides when the save and preview buttons are disabled and updates the word count.
	 */
	private void setUpListener() {
		creationText.selectedTextProperty().addListener((observble, oldValue, newValue) -> {
			// Decides when to disable the save and preview buttons
			save.setDisable(disable());
			preview.setDisable(disable());

			// Updates word count displayed on screen
			wordCount.setText(getWordCount().toString());
		});
	}

	/**
	 * Returns a boolean based on whether the selected text is valid to be previewed or saved.
	 * 
	 * @return true when the selection can be previewed and saved, false when it cannot be
	 */
	private boolean disable() {
		String selection = creationText.getSelectedText().trim();

		// If no words are selected the button should be disabled
		if (selection.equals("")) {
			return true;

		} else {
			// If there are more than 30 words selected the button should be disabled
			if (getWordCount() > 30) {
				return true;

			} else {
				return false;
			}
		}
	}

	/**
	 * Gets the word count of the current text selected.
	 * 
	 * @return the number of words selected in the creation text TextArea
	 */
	private Integer getWordCount() {
		String selection = creationText.getSelectedText().trim();

		// If there are no words selected return 0
		if (selection.equals("")) {
			return 0;

		} else {
			return selection.split(" ").length;
		}
	}
}
