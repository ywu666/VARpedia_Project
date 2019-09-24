package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	
	static Stage STAGE;

    @Override
    public void start(Stage primaryStage) throws Exception {
    	STAGE = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("resources/Menu.fxml"));
        setStage(root);
        STAGE.show();
    }
    
    static void setStage(Parent root) {
    	Scene scene = new Scene(root);
    	STAGE.setScene(scene);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
