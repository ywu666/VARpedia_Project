package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	static Stage STAGE;

    @Override
    public void start(Stage primaryStage) throws Exception {
    	STAGE = primaryStage;
    	
    	BashCommand mkCreationDir = new BashCommand("mkdir -p creations");
    	mkCreationDir.run();
    	
    	BashCommand rmNewTermDir = new BashCommand("if [ -d \".newTerm\" ]; then rm -r .newTerm; fi;");
    	rmNewTermDir.run();
    	
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
		Parent root = loader.load();
		MenuController controller = loader.getController();
		controller.setUpMenu();
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