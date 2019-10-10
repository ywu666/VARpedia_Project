package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

public class AudioController {
	@FXML private Button menu;
	@FXML private Button select;
	@FXML private Button preview;
	@FXML private Button save;
	@FXML private Button continueButton;
	@FXML private TextArea creationText;
	@FXML private TextArea selectionText;
	@FXML private ComboBox<String> selectMood;
	@FXML private ComboBox<String> selectVoice;
	@FXML private Label savedLabel;
	
	@FXML TableView<Audio> table;
	@FXML TableColumn<Audio, String> fileNameColumn;
	@FXML TableColumn<Audio, String> voiceColumn;
	@FXML TableColumn<Audio, String> moodColumn;
	
	private NewCreation creation;
	
	@FXML
	private void handleMenu() {
		// Check user wishes to abandon creation before exiting to menu
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
				controller.setUpTable();
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	private void handlePreview() {
		String mood = selectMood.getValue();
		String voice = selectVoice.getValue();
		String text = selectionText.getText();
		String sayText = "\"(SayText \\\"" + text + "\\\")\"";
		if (text.equals("")) { // Check the user has selected some text to preview
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select text before previewing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (voice == null) { // Check the user has selected a voice option
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a voice setting for your selection.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (mood == null) { // Check the user has selected a mood option
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a mood setting for your selection.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
				
		} else { // Preview text to speech with selected options
			PreviewSpeechTask task = new PreviewSpeechTask(voice, mood, sayText);
	        ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(task);
			executorService.shutdown();
			
		}
	}
	
	@FXML
	private void handleSave() {
		
		String mood = selectMood.getValue();
		String voice = selectVoice.getValue();
		String text = selectionText.getText().trim();
		
		if (text.equals("")) { // Check the user has selected some text to preview
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select text before previewing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (voice == null) { // Check the user has selected a voice option
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a voice setting for your selection.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (mood == null) { // Check the user has selected a mood option
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a mood setting for your selection.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
				
		} else { // Save audio with selected options
			table.getItems().add(new Audio(voice, mood, text));
		}
	}
	
	private String getMoodSettings(String mood) {
		if ("Happy".equals(mood)) {
			return "\"(set! duffint_params '((start 130) (end 105)))\" \"(Parameter.set 'Int_Method 'DuffInt)\" \"(Parameter.set 'Int_Target_Method Int_Targets_Default)\" \"(Parameter.set 'Duration_Stretch 0.8)\" ";
		} else if ("Neutral".equals(mood)) {
			return "\"(set! duffint_params '((start 120) (end 105)))\" \"(Parameter.set 'Int_Method 'DuffInt)\" \"(Parameter.set 'Int_Target_Method Int_Targets_Default)\" \"(Parameter.set 'Duration_Stretch 1)\" ";
		} else if ("Sad".equals(mood)) {
			return "\"(set! duffint_params '((start 110) (end 105)))\" \"(Parameter.set 'Int_Method 'DuffInt)\" \"(Parameter.set 'Int_Target_Method Int_Targets_Default)\" \"(Parameter.set 'Duration_Stretch 2.2)\" ";
		}
		return null;
	}
	
	@FXML
	private void handleContinue() {
		
		if (table.getItems().isEmpty()) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Save an audio file(s) before continuing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
		} else {
			Object[] array = table.getItems().toArray();
			MergeAudioTask task = new MergeAudioTask(array);
			Thread thread = new Thread(task);
			thread.run();
			
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Create.fxml"));
				Parent root = loader.load();
				CreateController controller = loader.getController();
				controller.initialiseCreateController(creation);
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets up the table listing the audio files saved and their corresponding settings
	 * @param c
	 */
	public void initialiseController(NewCreation c) {
		this.creation = c;
		
		creationText.setText(creation.getText());
		
		ArrayList<String> moodList = new ArrayList<String>();
		moodList.add("Happy");
		moodList.add("Neutral");
		moodList.add("Sad");
		selectMood.setItems(FXCollections.observableArrayList(moodList));
		
		ArrayList<String> voiceList = new ArrayList<String>();
		voiceList.add("akl_nz_jdt_diphone");
		voiceList.add("kal_diphone");
		selectVoice.setItems(FXCollections.observableArrayList(voiceList));
		
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
		voiceColumn.setCellValueFactory(new PropertyValueFactory<>("voice"));
		moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
		table.getItems().addAll(FXCollections.observableList(creation.getAudioList()));
	}
	
	/**
	 * Handles the previewing speech. Allows user to preview speech while the screen does not freeze.
	 */
	private class PreviewSpeechTask extends Task<Void> {
		
		String voice;
		String mood;
		String sayText;
		
		PreviewSpeechTask(String voice, String mood, String sayText) {
			this.voice = voice;
			this.mood = mood;
			this.sayText = sayText;
		}

		@Override
		protected Void call() throws Exception {
			
			String command = "festival -b \"(voice_" + voice + ")\" " + getMoodSettings(mood) + sayText;
			BashCommand preview = new BashCommand(command);
			preview.run();
			
			return null;
		}
	}
	
	/**
	 * Handles merging the audio files that have been saved.
	 */
	private class MergeAudioTask extends Task<Void> {
		private Object[] audioList;
		private Integer audioFileNum = 0;
		
		MergeAudioTask(Object[] audioList) {
			this.audioList = audioList;
		}

		@Override
		protected Void call() throws Exception {

			BashCommand mkDir = new BashCommand("mkdir -p .newTerm/audio");
			mkDir.run();
			
			for (Object audio : audioList) {
				if (audio instanceof Audio) {
					audioFileNum += 1;
					saveAudio((Audio)audio);
				}
			}
			
			String audio = "";
			for (int i = 1; i <= audioFileNum; i++) {
				audio += ".newTerm/audio/" + i + ".wav ";
			}
			String command = "sox " + audio + ".newTerm/audio.wav";
			BashCommand mergeAudio = new BashCommand(command);
			mergeAudio.run();
			
			return null;
		}
		
		private void saveAudio(Audio a) {
			String voice = a.getVoice();
			String mood = a.getMood();
			String text = a.getText();
			
			BashCommand saveTxt = new BashCommand("echo \"" + text + "\" > .newTerm/selection.txt");
			saveTxt.run();
			
			BashCommand text2wave = new BashCommand("text2wave -o .newTerm/audio/" + audioFileNum + ".wav .newTerm/selection.txt -eval \"(voice_" + voice + ")\" " + getMoodSettingsEval(mood));
			text2wave.run();
			
			BashCommand rmTxtFile = new BashCommand("rm .newTerm/selection.txt");
			rmTxtFile.run();
		}
		
		private String getMoodSettingsEval(String mood) {
			if ("Happy".equals(mood)) {
				return "-eval \"(set! duffint_params '((start 130) (end 105)))\" -eval \"(Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)\" -eval \"(Parameter.set 'Duration_Stretch 0.8)\" ";
			} else if ("Neutral".equals(mood)) {
				return "-eval \"(set! duffint_params '((start 120) (end 105)))\" -eval \"(Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)\" -eval \"(Parameter.set 'Duration_Stretch 1)\" ";
			} else if ("Sad".equals(mood)) {
				return "-eval \"(set! duffint_params '((start 110) (end 105)))\" -eval \"(Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)\" -eval \"(Parameter.set 'Duration_Stretch 2.2)\" ";
			}
			return null;
		}
	}
	
}