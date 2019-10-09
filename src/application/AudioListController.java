package application;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

public class AudioListController {
	
	@FXML TableView<Audio> table;
	@FXML TableColumn<Audio, String> fileNameColumn;
	@FXML TableColumn<Audio, String> voiceColumn;
	@FXML TableColumn<Audio, String> moodColumn;
	
	private NewCreation creation;
	
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
				controller.setUpTable();
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleContinue() {
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
	
	/**
	 * Sets up the table view based on the list of audio files stated in the creation.
	 * @param creation containing the list of audio files.
	 */
	public void initialiseList(NewCreation creation) {
		this.creation = creation;
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
		voiceColumn.setCellValueFactory(new PropertyValueFactory<>("voice"));
		moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
		table.getItems().addAll(FXCollections.observableList(creation.getAudioList()));
	}
}
