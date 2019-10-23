package application.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import application.Main;
import application.items.Creation;
import application.items.NewCreation;
import application.items.TableCreation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class MenuController extends Controller {
	
	@FXML private TableView<TableCreation> creationTable;
	@FXML private TableColumn<TableCreation, String> creationColumn;
	@FXML private TableColumn<TableCreation, String> termColumn;
	@FXML private TableColumn<TableCreation, String> ratingColumn;
	@FXML private TableColumn<TableCreation, String> lastViewedColumn;
	@FXML private Button playButton;
	@FXML private Button deleteButton;
	@FXML private ComboBox<String> sortBy;

	private static final String NAME = "Creation Name";
	private static final String RATING = "Lowest Confidence Rating";
	private static final String TERM = "Term";
	private static final String REVIEW = "Need to Review";
	
	@FXML
	private void handleSort() {
		String sort = sortBy.getValue();
		creationTable.getSortOrder().clear();
		
		if (NAME.equals(sort)) {
			creationTable.getItems().sort(Comparator.comparing(TableCreation::getName));
			
		} else if (TERM.equals(sort)) {
			creationTable.getItems().sort(Comparator.comparing(TableCreation::getTerm)
					.thenComparing(TableCreation::getRating)
					.thenComparing(TableCreation::getLastViewed)
					.thenComparing(TableCreation::getName));
			
		} else if (RATING.equals(sort)) {
			creationTable.getItems().sort(Comparator.comparing(TableCreation::getRating)
					.thenComparing(TableCreation::getLastViewed)
					.thenComparing(TableCreation::getTerm)
					.thenComparing(TableCreation::getName));
			
		} else if (REVIEW.equals(sort)) {
			creationTable.getItems().sort(Comparator.comparing(TableCreation::getLastViewed)
					.thenComparing(TableCreation::getRating)
					.thenComparing(TableCreation::getTerm)
					.thenComparing(TableCreation::getName));
		}
	}
	
	@FXML
	private void handlePlay() {
		TableCreation selection = creationTable.getSelectionModel().getSelectedItem();
		
		if (!selection.getStatus()) { 
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "You have selected an item in the list that is not ready yet. Please wait.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else {
			Creation c = creationTable.getSelectionModel().getSelectedItem().getCreation();
			MediaPlayController controller = (MediaPlayController) loadView("../resources/MediaPlay.fxml", this);
			controller.playCreation(c);
		}
	}
	
	@FXML
	private void handleDelete() {
		TableCreation selection = creationTable.getSelectionModel().getSelectedItem();
		
		if (!selection.getStatus()) { // Checks the item is ready
			Alert alertEmpty = new Alert(Alert.AlertType.WARNING, "You have selected an item in the list that is not ready yet. Please wait.", ButtonType.OK);
			alertEmpty.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alertEmpty.showAndWait();
			
		} else {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete '" + selection.getName() + "'.", ButtonType.OK, ButtonType.CANCEL);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			
			if (alert.getResult() == ButtonType.OK) {
				Creation.removeCreation(selection.getCreation());
				setUpMenu();
			}
		}
	}
	
	@FXML
	private void handleCreate() {
		try {
	        Pane root = FXMLLoader.load(getClass().getResource("../resources/Search.fxml"));
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
	
	public void setUpMenu() {
		sortBy.getItems().addAll(NAME, TERM, RATING, REVIEW);
		
		List<TableCreation> list = new ArrayList<>();
		for (Creation c : Creation.getCreations()) {
			list.add(new TableCreation(c));
		}
		
		creationColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		termColumn.setCellValueFactory(new PropertyValueFactory<>("term"));
		ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
		lastViewedColumn.setCellValueFactory(new PropertyValueFactory<>("lastViewed"));
		creationTable.getItems().clear();
		creationTable.getItems().addAll(FXCollections.observableArrayList(list));
		
		creationTable.getItems().sort(Comparator.comparing(TableCreation::getRating)
				.thenComparing(TableCreation::getLastViewed)
				.thenComparing(TableCreation::getTerm)
				.thenComparing(TableCreation::getName));

		playButton.disableProperty().bind(Bindings.isEmpty(creationTable.getSelectionModel().getSelectedItems()));
		deleteButton.disableProperty().bind(Bindings.isEmpty(creationTable.getSelectionModel().getSelectedItems()));
		
		creationTable.setPlaceholder(new Label("You have no creations. Please click 'Create' to begin."));
	}
	
	/**
	 * Sets up the menu when there is a creation being made. Indicates the creation is not ready
	 * but is in the process of being created.
	 * @param creationBeingMade is the name of the creation that is not ready
	 */
	public void setUpMenu(NewCreation creation) {
		setUpMenu();
		TableCreation temp = new TableCreation(new Creation(creation.getCreationName(), creation.getTerm()), false);
		creationTable.getItems().add(temp);
	}
}
