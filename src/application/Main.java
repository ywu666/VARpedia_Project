package application;

import java.io.File;

import application.controllers.MenuController;
import application.tasks.BashCommand;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	
	static Stage STAGE;

    @Override
    public void start(Stage primaryStage) throws Exception {
    	STAGE = primaryStage;
    	
    	new File(".creations").mkdir();
    	
    	new File(".creations/creations.txt").createNewFile();
    	
    	BashCommand rmNewTermDir = new BashCommand("if [ -d \".newTerm\" ]; then rm -r .newTerm; fi;");
    	rmNewTermDir.run();
    	
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
		Pane root = loader.load();
		MenuController controller = loader.getController();
		controller.setUpMenu();
		setStage(root);
		
        STAGE.show();
    }
    
    public static void setStage(Pane root) {
    	Scene scene = new Scene(root);
    	STAGE.setScene(scene);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}