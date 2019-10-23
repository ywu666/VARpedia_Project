package application.controllers;

import java.io.File;

import application.items.Creation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MediaPlayController extends Controller {

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
	boolean music = false;

	@FXML 
	public void handleMenu() {
		videoPlayer.stop();
		musicPlayer.stop();

		MenuController controller = (MenuController) loadView("../resources/Menu.fxml", this);
		controller.setUpMenu();
	}

	@FXML 
	public void handlePlay() {
		length = videoPlayer.getTotalDuration().toSeconds();
		boolean playing = videoPlayer.getStatus() == Status.PLAYING;
		boolean ended = videoPlayer.getCurrentTime().toSeconds() == length;

		if (ended) {
			videoPlayer.seek(videoPlayer.getStartTime());
			play.setText("Pause");

			if (music) {
				musicPlayer.play();
			}

		} else if (playing) {
			videoPlayer.pause();
			play.setText("Play");

			if (music) {
				musicPlayer.pause();
			}

		} else {
			videoPlayer.play();
			play.setText("Pause");

			if (music) {
				musicPlayer.play();
			}
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
		music = !music;

		if (music) {
			backgroundMusic.setText("Stop Background Music");

			if (videoPlayer.getStatus() == Status.PLAYING) {
				musicPlayer.play();
			}

		} else {
			musicPlayer.pause();
			backgroundMusic.setText("Add Background Music");
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

		Media sound = new Media(new File("media/khalafnasirs_-_Love_Story_In_Rain_2.mp3").toURI().toString());
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
			}
		});
	}
}