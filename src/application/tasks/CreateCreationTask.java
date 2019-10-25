package application.tasks;

import java.io.File;

import application.items.Creation;
import application.items.NewCreation;
import javafx.concurrent.Task;

/**
 * Task that creates the creation and saves it.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class CreateCreationTask extends Task<Void> {

	private String term;
	private Integer numImages;
	private String creationName;
	private String fileName;
	
	public CreateCreationTask(NewCreation creation) {
		this.term = creation.getTerm();
		this.numImages = creation.getNumImages();
		this.creationName = creation.getCreationName();
	}

	@Override
	protected Void call() throws Exception {
		getSlideshow();
		makeCreation();
		Creation.addCreation(new Creation(creationName, term, fileName));
		
		return null;
	}
	
	/**
	 * Makes a video with audio and the downloaded images.
	 */
	private void getSlideshow() {
		// Gets the length of the audio for the creation
		BashCommand getAudioLength = new BashCommand("echo `soxi -D .newTerm/audio.wav`", true);
		getAudioLength.run();
		Float length = Float.parseFloat(getAudioLength.getStdOutString());
		// Finds how long each image should appear based on how long the audio is
		Float freq = length / numImages;
		
		// Make the slide show of images
		String slideshow = "cat .newTerm/selectedImages/*.jpg | ffmpeg -f image2pipe -framerate 1/" + freq +
				" -i - -c:v libx264 -pix_fmt yuv420p -vf \"scale=w=1080:h=720:force_original_aspect_ratio=1,pad=1080:720:(ow-iw)/2:(oh-ih)/2\" -r 25 -y .newTerm/image_slide.mp4";
		BashCommand bash = new BashCommand(slideshow);
		bash.run();
		
		// Merge the images and audio together
		String merge = "ffmpeg -i .newTerm/image_slide.mp4 -i .newTerm/audio.wav -c:v copy -c:a aac -strict experimental -r 25 .newTerm/slideshow.mp4";
		BashCommand mergeCommand = new BashCommand(merge);
		mergeCommand.run();
	}
	
	/**
	 * Makes the creation by adding text to the slideshow of what the term searched was.
	 */
	private void makeCreation() {
		getFileName();
		
		// Adds text to the slide show video
		String textCommand = "ffmpeg -i .newTerm/slideshow.mp4 -max_muxing_queue_size 1024 -vf drawtext=\"fontfile=myfont.ttf: text='" +
				term + "': fontcolor=white: fontsize=70: box=1: boxcolor=black@0.5: boxborderw=5: x=(w-text_w)/2: y=(h-text_h)/2\" -codec:a copy "
				+ fileName;
		BashCommand addtext = new BashCommand(textCommand);
		addtext.run();
	}
	
	/**
	 * Gets the file name for the creation that will be used when it is saved.
	 */
	private void getFileName() {
		Integer i = 1;
		
		// While a file with the name exists, test a new file name
		while (new File(".creations/" + i + ".mp4").exists()) {
			i++;
		}
		
		fileName = ".creations/" + i.toString() + ".mp4";
	}
}
