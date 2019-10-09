package application;

import java.io.File;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
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
	@FXML private ProgressBar videoProgress;
	@FXML private Slider rating;
	@FXML private Label currentRating;
	
	private MediaPlayer videoPlayer;
	private Media video;
	private Creation creation;
	
	@FXML 
	public void handleMenu() {
		
		videoPlayer.stop();
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Menu.fxml"));
			Parent root = loader.load();
			MenuController controller = loader.getController();
			controller.setUpTable();
			Main.setStage(root);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@FXML 
	public void handlePlay() {
		if (videoPlayer.getStatus() == Status.PLAYING) {
			videoPlayer.pause();
			play.setText("Play");
			
		} else {
			videoPlayer.play();
			play.setText("Pause");
		}
	}
	
	@FXML 
	public void handleForward() {
		videoPlayer.seek(videoPlayer.getCurrentTime().add(Duration.seconds(3)));

	}
	
	@FXML
	public void handleRate() {
		creation.setRating((int)rating.getValue());
		Creation.creationRated(creation);
		currentRating.setText(creation.getRating().toString());
	}

	@FXML 
	public void handleBackward() {
		videoPlayer.seek(videoPlayer.getCurrentTime().add(Duration.seconds(-3)));

	}
	
	/**
	 * Begins playing the creation. Also handles updating the progress bar for the video.
	 * @param name of creation being played
	 */
	public void playCreation(Creation c) {
		creation = c;
		
		Creation.creationPlayed(creation);
		
		File fileUrl = new File(c.getFile());
		video = new Media(fileUrl.toURI().toString());
		videoPlayer = new MediaPlayer(video);
		videoPlayer.setAutoPlay(true);
		mediaView.setMediaPlayer(videoPlayer);
		
		
		if (creation.getRating() == null) {
			currentRating.setText("-");
		} else {
			currentRating.setText(creation.getRating().toString());
		}
		
		videoPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
					Duration newValue) {
				videoProgress.setProgress((Double)newValue.toSeconds() / (Double)video.getDuration().toSeconds());
			}
		});
		
	}
	
}
