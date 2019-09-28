package application;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class MediaPlayController {
	
	@FXML private MediaView mediaView;
	@FXML private Button forward;
	@FXML private Button backward;
	@FXML private Button play;
	@FXML private Button menue;
	
	private MediaPlayer videoPlayer;
	private Media video;
	
	@FXML 
	public void handleMenu() {
		
		videoPlayer.stop();
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
			Parent root = loader.load();
			MenuController controller = loader.getController();
			controller.setUpMenu();
			Main.setStage(root);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@FXML 
	public void handlePlay() {
		if (videoPlayer.getStatus() == Status.PLAYING) {
			System.out.println(videoPlayer.getStatus());
			videoPlayer.pause();
		} else {
			System.out.println(videoPlayer.getStatus());
			videoPlayer.play();
		}
	}
	
	@FXML 
	public void handleForward() {
		videoPlayer.seek(videoPlayer.getCurrentTime().add(Duration.seconds(3)));

	}

	@FXML 
	public void handleBackward() {
		videoPlayer.seek(videoPlayer.getCurrentTime().add(Duration.seconds(-3)));

	}
	
	public void playCreation(String name) {
		File fileUrl = new File("creations/"+ name +".mp4");
		video = new Media(fileUrl.toURI().toString());
		videoPlayer = new MediaPlayer(video);
		videoPlayer.setAutoPlay(true);
		mediaView.setMediaPlayer(videoPlayer);
		
	}
	
}
