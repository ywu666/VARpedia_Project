package application;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AudioListController {
	
	@FXML TableView<Audio> table;
	@FXML TableColumn<Audio, String> fileNameColumn;
	@FXML TableColumn<Audio, String> moodColumn;
	
	private Creation creation;
	
	@FXML
	private void handleMenu() {
		BashCommand rmNewTermDir = new BashCommand("rm -r .newTerm");
    	rmNewTermDir.run();
    	
		try {
			Parent root = FXMLLoader.load(getClass().getResource("resources/Menu.fxml"));
			Main.setStage(root);
			
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public void initialiseList(Creation creation) {
		this.creation = creation;
		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
		table.getItems().addAll(FXCollections.observableList(creation.getAudioList()));
	}
}
