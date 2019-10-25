package application.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import application.items.AlertBox;
import application.items.Creation;
import application.items.NewCreation;
import application.items.TableCreation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the menu view. This view shows the creations, allowing users to play, delete,
 * create new creations and quit the application.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
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
		// Get user's preference on how to sort the creations and clear the current sort
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

		// If selection is not finished being created, warn the user
		if (!selection.getStatus()) {
			AlertBox.showWaitAlert(AlertType.WARNING,
					"You have selected an item in the list that is not ready yet. Please wait.");

		// If valid creation, get creation selected and play in media player
		} else {
			Creation c = creationTable.getSelectionModel().getSelectedItem().getCreation();
			MediaPlayController controller = (MediaPlayController) loadView("../resources/MediaPlay.fxml", this);
			controller.playCreation(c);
		}
	}

	@FXML
	private void handleDelete() {
		TableCreation selection = creationTable.getSelectionModel().getSelectedItem();

		// If selection is not finished being created, warn the user
		if (!selection.getStatus()) {
			AlertBox.showWaitAlert(AlertType.WARNING, "You have selected an item in the list that is not ready yet. Please wait.");

		} else {
			// Check the user wants to delete the creation, if they do, delete it and refresh the menu
			boolean confirm = AlertBox.confirmationAlert("Are you sure you want to delete '" + selection.getName() + "'.");
			if (confirm) {
				Creation.removeCreation(selection.getCreation());
				setUpMenu();
			}
		}
	}

	@FXML
	private void handleCreate() {
		loadView("../resources/Search.fxml", this);
	}

	@FXML
	private void handleQuit() {
		Platform.exit();
		System.exit(0);
	}

	public void setUpMenu() {
		// Add sort options to the choice box
		sortBy.getItems().addAll(NAME, TERM, RATING, REVIEW);

		// Set up list of TableCreation objects to be added to the creation table
		List<TableCreation> list = new ArrayList<>();
		for (Creation c : Creation.getCreations()) {
			list.add(new TableCreation(c));
		}

		// Set up the creation table
		creationTable.setPlaceholder(new Label("You have no creations. Please click 'Create' to begin."));
		creationColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		termColumn.setCellValueFactory(new PropertyValueFactory<>("term"));
		ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
		lastViewedColumn.setCellValueFactory(new PropertyValueFactory<>("lastViewed"));
		creationTable.getItems().clear();
		creationTable.getItems().addAll(FXCollections.observableArrayList(list));

		// Set the default sort to the table
		creationTable.getItems().sort(Comparator.comparing(TableCreation::getRating)
				.thenComparing(TableCreation::getLastViewed)
				.thenComparing(TableCreation::getTerm)
				.thenComparing(TableCreation::getName));

		// Disable both the play and delete button until an item has been selected in the list
		playButton.disableProperty().bind(Bindings.isEmpty(creationTable.getSelectionModel().getSelectedItems()));
		deleteButton.disableProperty().bind(Bindings.isEmpty(creationTable.getSelectionModel().getSelectedItems()));

		// Color the rows of the creation table based on the rating
		setColorOnRating();
	}

	/**
	 * Sets up the menu when there is a creation being made. Indicates the creation is not ready
	 * and it is in the process of being created.
	 * 
	 * @param creationBeingMade The creation that is not ready
	 */
	public void setUpMenu(NewCreation creation) {
		setUpMenu();
		
		// Add temporary table item
		TableCreation temp = new TableCreation(new Creation(creation.getCreationName(), creation.getTerm()), false);
		creationTable.getItems().add(temp);
	}
	
	/**
	 * Sets the color of each row in the creation table based on the rating.
	 */
	private void setColorOnRating() {
		ratingColumn.setCellFactory(column -> {
			return new TableCell<TableCreation, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? "" : getItem().toString());
					setGraphic(null);

					TableRow<?> currentRow = getTableRow();
					// Color only rows that are not empty
					if (!isEmpty()) {
						// Set empty ratings and ratings of one to light red
						if (item.equals("-") || item.equals("1") ) {
							currentRow.setStyle("-fx-background-color: lightcoral");
						
						// Set ratings of 2 and 3 to yellow
						} else if (item.equals("2") || item.equals("3")) {
							currentRow.setStyle("-fx-background-color: yellow");
						
						// Set ratings of 4 or 5 to green
						} else {
							currentRow.setStyle("-fx-background-color: lightgreen");
						}
					}
				}
			};
		});
	}
}
