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
	@FXML private Button backgroundMusic;
	
	private MediaPlayer videoPlayer;
	private Media video;
	private Creation creation;
	private Double length;
	private MediaPlayer musicPlayer;
	
	@FXML 
	public void handleMenu() {
		videoPlayer.stop();
		musicPlayer.stop();
		
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
		length = videoPlayer.getTotalDuration().toSeconds();
		boolean playing = videoPlayer.getStatus() == Status.PLAYING;
		boolean ended = videoPlayer.getCurrentTime().toSeconds() == length;

		if(ended) {
			videoPlayer.seek(videoPlayer.getStartTime());
			play.setText("Pause");
			
		} else if (playing) {
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
		if ("Replay".equals(play.getText())) {
			play.setText("Pause");
		}
	}
	
	@FXML
	public void handleMusic() {		

		if (musicPlayer.getStatus() == Status.PLAYING) {
			musicPlayer.pause();
			backgroundMusic.setText("Add Background Music");

		} else {
			musicPlayer.play();
			backgroundMusic.setText("Stop Background Music");
		}
		
		musicPlayer.setOnEndOfMedia(new Runnable() {

			@Override
			public void run() {
				musicPlayer.seek(musicPlayer.getStartTime());
			}
		});
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
		
		Media sound = new Media(new File(System.getProperty("user.dir") +"/media/khalafnasirs_-_Love_Story_In_Rain_2.mp3").toURI().toString());
		musicPlayer = new MediaPlayer(sound);
		musicPlayer.setVolume(1.0);

		videoPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
					Duration newValue) {
				videoProgress.setProgress((Double)newValue.toSeconds() / (Double)video.getDuration().toSeconds());
			}
		});

		videoPlayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				play.setText("Replay");
				videoProgress.setProgress(1.0);
				musicPlayer.stop();
				backgroundMusic.setText("Background Music");
			}
		});
	}
}
