package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

public class MenuController {
	
	@FXML private Button play;
	@FXML private Button delete;
	@FXML private Button quit;
	@FXML private Button create;
	@FXML private TableView<TableCreation> creationTable;
	@FXML private TableColumn<TableCreation, String> creationColumn;
	@FXML private TableColumn<TableCreation, String> ratingColumn;
	@FXML private TableColumn<TableCreation, String> lastViewedColumn;
	
	@FXML
	private void handlePlay() {
		TableCreation selection = creationTable.getSelectionModel().getSelectedItem();
		
		if (creationTable.getSelectionModel().isEmpty()) { // Checks has selected an item
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "You have not selected an item in the list to play.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
		} else if (!selection.getStatus()) { 
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "You have selected an item in the list that is not ready yet. Please wait.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/MediaPlay.fxml"));
				Parent root = loader.load();
				MediaPlayController controller = loader.getController();
				controller.playCreation(creationTable.getSelectionModel().getSelectedItem().getCreation());
				Main.setStage(root);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleDelete() {
		TableCreation selection = creationTable.getSelectionModel().getSelectedItem();
		
		if (creationTable.getSelectionModel().isEmpty()) { // Checks has selected an item
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "You have not selected an item in the list to delete.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else if (!selection.getStatus()) { // Checks the item is ready
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "You have selected an item in the list that is not ready yet. Please wait.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else {
			
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete '" + selection.getName() + "'.", ButtonType.OK, ButtonType.CANCEL);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			
			if (alert.getResult() == ButtonType.OK) {
				Creation.removeCreation(selection.getCreation());
				setUpTable();
			}
		}
	}
	
	@FXML
	private void handleCreate() {
		try {
	        Parent root = FXMLLoader.load(getClass().getResource("resources/Search.fxml"));
			Main.setStage(root);
			
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	@FXML
	private void handleQuit() {		
		Platform.exit();
		System.exit(0);
	}
	
	public void setUpTable() {		
		List<TableCreation> list = new ArrayList<>();
		
		for (Creation c : Creation.getCreations()) {
			list.add(new TableCreation(c));
		}
		creationColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
		lastViewedColumn.setCellValueFactory(new PropertyValueFactory<>("lastViewed"));
		creationTable.getItems().clear();
		creationTable.getItems().addAll(FXCollections.observableArrayList(list));
		
	}
	
	/**
	 * Sets up the menu when there is a creation being made. Indicates the creation is not ready
	 * but is in the process of being created.
	 * @param creationBeingMade is the name of the creation that is not ready
	 */
	public void setUpMenu(String creationBeingMade) {
		setUpTable();
		TableCreation temp = new TableCreation(new Creation(creationBeingMade, null), false);
		creationTable.getItems().add(temp);
	}
}
