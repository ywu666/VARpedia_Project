package application.controllers;

import java.io.IOException;

import application.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class Controller {

	private Pane pane;

	public Controller loadView(String fxmlFile, Object location, boolean show) {

		try {
			FXMLLoader loader = new FXMLLoader(location.getClass().getResource(fxmlFile));
			pane = loader.load();
			
			if (show) {
				Main.setStage(pane);
			}
			
			Controller c = loader.getController();
			c.setPane(pane);

			return c;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public Controller loadView(String fxmlFile, Object location) {
		return loadView(fxmlFile, location, true);
	}
	
	public void setPane(Pane pane) {
		this.pane = pane;
	}

	public Pane getPane() {
		return pane;
	}
}
