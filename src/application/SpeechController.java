package application;

import java.awt.Label;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
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
	
	Integer audioFileNum = 0;
	
	
	@FXML
	private void handleMenu() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("resources/Menu.fxml"));
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
			// handle selection and preview
			
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
		BashCommand mkDir = new BashCommand("mkdir .newTerm");
		mkDir.run();
		BashCommand saveTxt = new BashCommand("echo \"" + selectionText.getText() + "\" > .newTerm/selection.txt");
		saveTxt.run();
		
		if ("Happy".equals(selection)) {
			BashCommand text2wave = new BashCommand("text2wave -o .newTerm/audio" + audioFileNum + ".wav selection.txt -eval src/application/resources/Happy.scm");
			text2wave.run();
			
		} else if ("Neutral".equals(selection)) {
			BashCommand text2wave = new BashCommand("text2wave -o .newTerm/audio" + audioFileNum + ".wav selection.txt -eval src/application/resources/Neutral.scm");
			text2wave.run();
			
		} else if ("Sad".equals(selection)) {
			BashCommand text2wave = new BashCommand("text2wave -o .newTerm/audio" + audioFileNum + ".wav selection.txt -eval src/application/resources/Sad.scm");
			text2wave.run();
		}
		
		BashCommand rmTxtFile = new BashCommand("rm .newTerm/selection.txt");
		rmTxtFile.run();
	}
	
	@FXML
	private void handleContinue() {
	}
	
	public void initialiseController(String text) {
		creationText.setText(text);
		
		ArrayList<String> voiceList = new ArrayList<String>();
		voiceList.add("Happy");
		voiceList.add("Neutral");
		voiceList.add("Sad");
		selectSound.setItems(FXCollections.observableArrayList(voiceList));
	}
}
