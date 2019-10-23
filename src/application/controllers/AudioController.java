package application.controllers;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.items.Audio;
import application.items.NewCreation;
import application.tasks.BashCommand;
import application.tasks.MergeAudioTask;
import application.tasks.PreviewSpeechTask;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

public class AudioController extends Controller {

	@FXML private Button moveUpButton;
	@FXML private Button moveDownButton;
	@FXML private Button playButton;
	@FXML private Button deleteButton;
	@FXML private Tooltip tip;
	@FXML private TextArea creationText;
	@FXML private TextArea selectionText;
	@FXML private ComboBox<String> selectMood;
	@FXML private ComboBox<String> selectVoice;
	@FXML private Label savedLabel;
	@FXML TableView<Audio> table;
	@FXML TableColumn<Audio, String> fileNameColumn;
	@FXML TableColumn<Audio, String> voiceColumn;
	@FXML TableColumn<Audio, String> moodColumn;

	PreviewSpeechTask previewTask;
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
		// Check user wishes to abandon creation before exiting to menu
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to abandon your creation.", ButtonType.OK, ButtonType.CANCEL);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.OK) {
			BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
			rmNewTermDir.run();

			MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
			controller.setUpMenu();
		}
	}

	@FXML
	private void handleMoveUp() {
		int index = table.getSelectionModel().getSelectedIndex();
		boolean isTop = index == 0;

		if (!isTop) {
			table.getItems().add(index - 1, table.getItems().remove(index));
			table.getSelectionModel().clearAndSelect(index - 1);
		}
	}

	@FXML
	private void handleMoveDown() {
		int index = table.getSelectionModel().getSelectedIndex();
		boolean isBottom = index == table.getItems().size() - 	1;

		if (!isBottom) {
			table.getItems().add(index + 1, table.getItems().remove(index));
			table.getSelectionModel().clearAndSelect(index + 1);
		}
	}

	@FXML
	private void handleDelete() {
		Audio selection = table.getSelectionModel().getSelectedItem();
		table.getItems().remove(selection);
		creation.removeAudio(selection);

		if (table.getItems().size() == 0) {
			tip.setText("Save an audio file.");
		}
	}

	@FXML
	private void handleSelect() {
		creationText.setEditable(false);
		selectionText.setEditable(false);

		String selection = creationText.getSelectedText();
		if (selection.equals("")) { // Check user has selected something
			Alert alert = new Alert(Alert.AlertType.WARNING, "Please highlight the text you wish to select before pressing submit.", ButtonType.OK);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();

		} else {
			String[] words = selection.split(" ");
			if (words.length > 30) { // Check there are at most 30 words selected
				Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Selection must be at most 30 words.", ButtonType.OK);
				alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alertEmpty.showAndWait();

			} else {
				selectionText.setText(selection);
			}
		}
	}

	@FXML
	private void handlePlay() {
		Audio selection = table.getSelectionModel().getSelectedItem();
		preview(Audio.voices.get(selection.getVoice()), selection.getMood(), selection.getText());
	}

	@FXML
	private void handlePreview() {
		String mood = selectMood.getValue();
		String voice = Audio.voices.get(selectVoice.getValue());
		String text = selectionText.getText();

		if (text.equals("")) { // Check the user has selected some text to preview
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select text before previewing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
		} else {// Preview text to speech with selected options

			if(voice == null) {
				voice = "uk";
			}

			preview(voice, mood, text);
		}
	}

	@FXML
	private void handleSave() {
		tip.setText("Select an audio file.");

		String mood = selectMood.getValue();
		String voice = selectVoice.getValue();
		String text = selectionText.getText().trim();

		if (text.equals("")) { // Check the user has selected some text to preview
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select text before previewing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();

		} else { // Save audio with selected options
			if(voice == null) {
				voice = "United Kingdom";
			}

			if (mood == null) {
				mood = "Neutral";	
			}

			Audio a = new Audio(voice, mood, text);
			table.getItems().add(a);
			creation.addAudio(a);
		}
	}

	private void preview(String voice, String mood, String text) {
		if (previewTask != null) {
			previewTask.cancel();
		}

		previewTask = new PreviewSpeechTask("en-" + voice, mood, text);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(previewTask);
		executorService.shutdown();
	}

	@FXML
	private void handleContinue() {
		creation.setAudioList(table.getItems());

		if (previewTask != null) {
			previewTask.cancel();
		}

		if (table.getItems().isEmpty()) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Save an audio file(s) before continuing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
		} else {
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
	 * Sets up the table listing the audio files saved and their corresponding settings
	 * @param c
	 */
	public void initialiseController(NewCreation c) {
		this.creation = c;

		creationText.setText(creation.getText());
		selectMood.setItems(FXCollections.observableArrayList(moodList));
		selectVoice.setItems(FXCollections.observableArrayList(voiceList));

		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
		voiceColumn.setCellValueFactory(new PropertyValueFactory<>("voice"));
		moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
		table.getItems().addAll(FXCollections.observableList(creation.getAudioList()));

		moveUpButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		moveDownButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		playButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		deleteButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
	}
}
