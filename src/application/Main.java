package application;

import java.io.File;

import application.controllers.MenuController;
import application.tasks.BashCommand;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Entry point of the VARpedia application.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class Main extends Application {
	
	static Stage STAGE;

    @Override
    public void start(Stage primaryStage) throws Exception {
    	STAGE = primaryStage;
    	
    	// Makes hidden creations directory and creations text file if they are not present
    	new File(".creations").mkdir();
    	new File(".creations/creations.txt").createNewFile();
    	
    	// Removes the hidden newTerm directory if it is present due to the user exiting the program previously
    	// from the wrong place
    	BashCommand rmNewTermDir = new BashCommand("if [ -d \".newTerm\" ]; then rm -r .newTerm; fi;");
    	rmNewTermDir.run();
    	
    	// Loads the menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
		Pane root = loader.load();
		MenuController controller = loader.getController();
		controller.setUpMenu();
		setStage(root);
		
        STAGE.show();
    }
    
    /**
     * Sets the stage for the application.
     * 
     * @param root The pane to be shown on the screen
     */
    public static void setStage(Pane root) {
    	Scene scene = new Scene(root);
    	STAGE.setScene(scene);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}