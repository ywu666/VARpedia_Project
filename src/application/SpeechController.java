package application;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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
	@FXML private ComboBox<String> selectSound;
	
	private Integer audioFileNum = 0;
	private Creation creation;
	
	@FXML
	private void handleMenu() {
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
	
	@FXML
	private void handleSelect() {
		creationText.setEditable(false);
		selectionText.setEditable(false);
		
		String selection = creationText.getSelectedText();
		
		if (selection.equals("")) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please highlight the text you wish to select before pressing submit.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
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
		String selection = selectSound.getValue();
		String text = selectionText.getText();
		String sayText = "\"(SayText \\\"" + text + "\\\")\"";
		if (text.equals("")) {
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select text before previewing.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (selection == null) {
				Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "Please select a sound setting for your selection.", ButtonType.OK);
				alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alertEmpty.showAndWait();
				
		} else {
			if ("Happy".equals(selection)) {
 				String command = "festival -b src/application/resources/Happy.scm " + sayText;
				BashCommand preview = new BashCommand(command);
				preview.run();
				
			} else if ("Neutral".equals(selection)) {
				String command = "festival -b src/application/resources/Neutral.scm " + sayText;
				BashCommand preview = new BashCommand(command);
				preview.run();
				
			} else if ("Sad".equals(selection)) {
				String command = "festival -b src/application/resources/Sad.scm " + sayText;
				BashCommand preview = new BashCommand(command);
				preview.run();
			}
		}
	}
	
	@FXML
	private void handleSave() {
		audioFileNum += 1;
		String selection = selectSound.getValue();
		BashCommand mkDir = new BashCommand("mkdir -p .newTerm/audio");
		mkDir.run();
		BashCommand saveTxt = new BashCommand("echo \"" + selectionText.getText() + "\" > .newTerm/selection.txt");
		saveTxt.run();
		
		if ("Happy".equals(selection)) {
			saveAudio("Happy");
			
		} else if ("Neutral".equals(selection)) {
			saveAudio("Neutral");
			
		} else if ("Sad".equals(selection)) {
			saveAudio("Sad");
		}
	}
	
	private void saveAudio(String mood) {
		BashCommand text2wave = new BashCommand("text2wave -o .newTerm/audio/" + audioFileNum + ".wav .newTerm/selection.txt -eval src/application/resources/" + mood + ".scm");
		text2wave.run();
		creation.addAudioFile(audioFileNum, mood, selectionText.getText());
		
		BashCommand rmTxtFile = new BashCommand("rm .newTerm/selection.txt");
		rmTxtFile.run();
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
		
		ArrayList<String> voiceList = new ArrayList<String>();
		voiceList.add("Happy");
		voiceList.add("Neutral");
		voiceList.add("Sad");
		selectSound.setItems(FXCollections.observableArrayList(voiceList));
		
		this.creation = creation;
	}
	
	private class MergeAudioTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			
			String audio = "";
			for (int i = 1; i <= audioFileNum; i++) {
				audio += ".newTerm/audio/" + i + ".wav ";
			}
			String command = "sox " + audio + ".newTerm/audio.wav";
			BashCommand mergeAudio = new BashCommand(command);
			mergeAudio.run();
			
			return null;
		}
	}
}
