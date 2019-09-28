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
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

public class SpeechController {

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
	
	private Integer audioFileNum = 0;
	private Creation creation;
	
	@FXML
	private void handleMenu() {
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
				controller.setUpMenu();
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleSelect() {
		creationText.setEditable(false);
		selectionText.setEditable(false);
		
		String selection = creationText.getSelectedText();
		
		if (selection.equals("")) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "Please highlight the text you wish to select before pressing submit.", ButtonType.OK);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			
		} else {
			String[] words = selection.split(" ");
			
			if (words.length > 30) {
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
		if (text.equals("")) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select text before previewing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (voice == null) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a voice setting for your selection.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (mood == null) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a mood setting for your selection.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
				
		} else {
			PreviewSpeechTask task = new PreviewSpeechTask(voice, mood, sayText);
	        ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(task);
			executorService.shutdown();
			
		}
	}
	
	@FXML
	private void handleSave() {
		audioFileNum += 1;
		BashCommand mkDir = new BashCommand("mkdir -p .newTerm/audio");
		mkDir.run();
		
		String mood = selectMood.getValue();
		String voice = selectVoice.getValue();
		String text = selectionText.getText();
		if (text.equals("")) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select text before previewing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (voice == null) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a voice setting for your selection.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (mood == null) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a mood setting for your selection.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
				
		} else {
			System.out.println("Saved: voice-> " + voice + " mood-> " + mood + " text-> " + text);
			saveAudio(voice, mood, text);
		}
	}
	
	private void saveAudio(String voice, String mood, String text) {
		BashCommand saveTxt = new BashCommand("echo \"" + text + "\" > .newTerm/selection.txt");
		saveTxt.run();
		
		BashCommand text2wave = new BashCommand("text2wave -o .newTerm/audio/" + audioFileNum + ".wav .newTerm/selection.txt -eval \"(voice_" + voice + ")\" -eval src/application/resources/" + mood + ".scm");
		text2wave.run();
		
		BashCommand rmTxtFile = new BashCommand("rm .newTerm/selection.txt");
		rmTxtFile.run();
		
		File file = new File(".newTerm/audio/" + audioFileNum + ".wav");
		if (file.exists()) {
			creation.addAudioFile(audioFileNum, voice, mood, text);
			savedLabel.setText("Audio successfully saved: " + voice + ", " + mood + " :)");
		}
	}
	
	@FXML
	private void handleContinue() {
		BashCommand checkAudio = new BashCommand("test -d .newTerm; echo $?", true);
		checkAudio.run();
		
		if ("1".equals(checkAudio.getStdOutString())) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Save an audio file(s) before continuing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else {
			MergeAudioTask task = new MergeAudioTask();
			Thread thread = new Thread(task);
			thread.run();
			
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/AudioChosen.fxml"));
				Parent root = loader.load();
				AudioListController controller = loader.getController();
				controller.initialiseList(creation);
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void initialiseController(Creation creation) {
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
		
		this.creation = creation;
	}
	
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
			
			String command = "festival -b \"(voice_" + voice + ")\" src/application/resources/" + mood + ".scm " + sayText;
			BashCommand preview = new BashCommand(command);
			preview.run();
			
			return null;
		}
	}
	
	private class MergeAudioTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			
			String audio = "";
			for (int i = 1; i <= audioFileNum; i++) {
				audio += ".newTerm/audio/" + i + ".wav ";
			}
			String command = "sox " + audio + ".newTerm/audio.wav";
			System.out.println("command: " + command);
			BashCommand mergeAudio = new BashCommand(command);
			mergeAudio.run();
			
			return null;
		}
	}
}
