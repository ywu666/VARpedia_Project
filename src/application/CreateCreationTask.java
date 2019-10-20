package application;

import java.io.File;

import javafx.concurrent.Task;

public class CreateCreationTask extends Task<Void> {

	private String term;
	private Integer numImages;
	private String creationName;
	private String fileName;
	
	CreateCreationTask(NewCreation creation) {
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
		BashCommand getAudioLength = new BashCommand("echo `soxi -D .newTerm/audio.wav`", true);
		getAudioLength.run();
		Float length = Float.parseFloat(getAudioLength.getStdOutString());
		Float freq = length /numImages;
		
		String slideshow = "cat .newTerm/selectedImages/*.jpg | ffmpeg -f image2pipe -framerate 1/"+freq+" -i - -c:v libx264 -pix_fmt yuv420p -vf \"scale=w=1080:h=720:force_original_aspect_ratio=1,pad=1080:720:(ow-iw)/2:(oh-ih)/2\" -r 25 -y .newTerm/image_slide.mp4";
		BashCommand bash = new BashCommand(slideshow);
		bash.run();
		
		String merge = "ffmpeg -i .newTerm/image_slide.mp4 -i .newTerm/audio.wav -c:v copy -c:a aac -strict experimental -r 25 .newTerm/slideshow.mp4";
		BashCommand mergeCommand = new BashCommand(merge);
		mergeCommand.run();
	}
	
	/**
	 * Makes the creation by merging the slideshow with the audio and adding text of what the term searched was
	 */
	private void makeCreation() {
		getFileName();
		String textCommand = "ffmpeg -i .newTerm/slideshow.mp4 -max_muxing_queue_size 1024 -vf drawtext=\"fontfile=myfont.ttf: text='" + term + "': fontcolor=white: fontsize=70: box=1: boxcolor=black@0.5: boxborderw=5: x=(w-text_w)/2: y=(h-text_h)/2\" -codec:a copy " + fileName;
		BashCommand addtext = new BashCommand(textCommand);
		addtext.run();
	}
	
	private void getFileName() {
		Integer i = 1;
		while (new File(".creations/" + i + ".mp4").exists()) {
			i++;
		}
		
		fileName = ".creations/" + i.toString() + ".mp4";
	}
}
