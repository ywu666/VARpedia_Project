package application.controllers;

import java.io.IOException;

import application.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 * Class containing the methods that should be available for all Controllers.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class Controller {

	private Pane pane;

	/**
	 * Gets the controller of the view loaded based on the fxml file passed in.
	 * 
	 * @param fxmlFile The file containing the information for the view to be shown
	 * @param location The location of the class
	 * @param show Determines whether the view should be loaded to the application screen
	 * @return Controller object of the fxml file loaded
	 */
	public Controller loadView(String fxmlFile, Object location, boolean show) {

		try {
			// Load the view
			FXMLLoader loader = new FXMLLoader(location.getClass().getResource(fxmlFile));
			pane = loader.load();

			// Show the pane on the screen if show determines this should happen
			if (show) {
				Main.setStage(pane);
			}

			// Sets the pane of the controller as the pane loaded, so that it can be retrieved later
			Controller controller = loader.getController();
			controller.setPane(pane);

			return controller;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets the controller of the view loaded based on the fxml file passed in.
	 * Assumes the user wishes to show the view on the screen.
	 * 
	 * @param fxmlFile The file containing the information for the view to be shown
	 * @param location The location of the class
	 * @return Controller object of the fxml file loaded
	 */
	public Controller loadView(String fxmlFile, Object location) {
		return loadView(fxmlFile, location, true);
	}

	/**
	 * Sets the pane of the current Controller.
	 * 
	 * @param pane The pane associated with the Controller
	 */
	private void setPane(Pane pane) {
		this.pane = pane;
	}

	/**
	 * Gets the Pane object for the Controller.
	 * 
	 * @return the pane associated with the Controller the method is invoked on
	 */
	public Pane getPane() {
		return pane;
	}
}
