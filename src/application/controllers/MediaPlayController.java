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

/**
 * Controller for the video player view. This view allows the user to watch their creations and rate
 * their confidence for them.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
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
		// Stop the music and video when returning to the menu
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

		// If video has ended start video again and play music if specified
		if (ended) {
			videoPlayer.seek(videoPlayer.getStartTime());
			play.setText("Pause");

			if (music) {
				musicPlayer.play();
			}

		// If video is playing then video and music should be paused
		} else if (playing) {
			videoPlayer.pause();
			play.setText("Play");

			if (music) {
				musicPlayer.pause();
			}

		// Otherwise video must be paused so should start playing again, with music if specified
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
		// Skip forward 3 seconds in the video
		videoPlayer.seek(videoPlayer.getCurrentTime().add(Duration.seconds(3)));
	}

	@FXML
	public void handleRate() {
		creation.setRating((int)rating.getValue());
		
		// Update creation
		Creation.creationRated(creation);
		
		// Update text in media view
		currentRating.setText(creation.getRating().toString());
	}

	@FXML 
	public void handleBackward() {
		// Skip backwards 3 seconds in the video
		videoPlayer.seek(videoPlayer.getCurrentTime().add(Duration.seconds(-3)));

		// If the video has ended,, skipping back will mean it is not so button text should change
		if ("Replay".equals(play.getText())) {
			play.setText("Pause");
		}
	}

	@FXML
	public void handleMusic() {
		// Update music settings
		music = !music;

		// If user decides they want music, begin playing and change text
		if (music) {
			backgroundMusic.setText("Stop Background Music");
			
			if (videoPlayer.getStatus() == Status.PLAYING) {
				musicPlayer.play();
			}

		// If user decides to stop the music, pause it and change the text
		} else {
			musicPlayer.pause();
			backgroundMusic.setText("Add Background Music");
		}

		// When the music has reached the end, begin again in a loop
		musicPlayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				musicPlayer.seek(musicPlayer.getStartTime());
			}
		});
	}

	/**
	 * Begins playing the creation. Also handles updating the progress bar for the video.
	 * 
	 * @param name of creation being played
	 */
	public void playCreation(Creation c) {
		creation = c;
		
		// Update time viewed information for creation
		Creation.creationPlayed(creation);

		// Set up the video player to play the creation specified
		File file = new File(c.getFile());
		video = new Media(file.toURI().toString());
		videoPlayer = new MediaPlayer(video);
		videoPlayer.setAutoPlay(true);
		mediaView.setMediaPlayer(videoPlayer);

		// Set up creation rating information in the view
		if (creation.getRating() == null) {
			currentRating.setText("-");
		} else {
			currentRating.setText(creation.getRating().toString());
		}

		// Set up music player
		Media media = new Media(new File("media/khalafnasirs_-_Love_Story_In_Rain_2.mp3").toURI().toString());
		musicPlayer = new MediaPlayer(media);
		musicPlayer.setVolume(1.0);

		// Update progress bar according to position in the video
		videoPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
					Duration newValue) {
				videoProgress.setProgress((Double)newValue.toSeconds() / (Double)video.getDuration().toSeconds());
			}
		});

		// When creation video has ended, update play/pause button text, set progress bar to completed and stop music
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
