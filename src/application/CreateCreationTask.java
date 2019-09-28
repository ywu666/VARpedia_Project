package application;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.Size;

import javafx.concurrent.Task;

public class CreateCreationTask extends Task<Void> {

	private String term;
	private String creationName;
	private Integer numImages;
	
	CreateCreationTask(Creation creation) {
		this.term = creation.getTerm();
		this.numImages = creation.getNumImages();
		this.creationName = creation.getCreationName();
	}

	@Override
	protected Void call() throws Exception {
		BashCommand imagesDir = new BashCommand("mkdir .newTerm/images");
		imagesDir.run();
		
		downloadImages(term, numImages);
		getSlideshow();
		makeCreation();
		
		return null;
	}
	
	private void downloadImages(String term, Integer numberOfImages) {
		try {
			String apiKey = getAPIKey("apiKey");
			String sharedSecret = getAPIKey("sharedSecret");
			Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
			
			Integer resultsPerPage = numberOfImages;
			int page = 0;
			
	        PhotosInterface photos = flickr.getPhotosInterface();
	        SearchParameters params = new SearchParameters();
	        params.setSort(SearchParameters.RELEVANCE);
	        params.setMedia("photos"); 
	        params.setText(term);
	        
	        PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
	        
	        int count = 0;
	        for (Photo photo: results) {
	        	try {
	        		BufferedImage image = photos.getImage(photo,Size.LARGE);
		        	String filename = count + ".jpg";
		        	File outputfile = new File(".newTerm/images",filename);
		        	ImageIO.write(image, "jpg", outputfile);
	        	} catch (FlickrException fe) {
	        		System.err.println("Ignoring image " + photo.getId() +": "+ fe.getMessage());
				}
	        	count += 1;
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getAPIKey(String key) throws Exception {
		String config = "src/application/resources/flickr-api-keys.txt";
		
		File file = new File(config); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		
		String line;
		while ( (line = br.readLine()) != null ) {
			if (line.trim().startsWith(key)) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find " + key + " in config file "+file.getName());
	}
	
	private void getSlideshow() {
		BashCommand getAudioLength = new BashCommand("echo `soxi -D .newTerm/audio.wav`", true);
		getAudioLength.run();
		Float length = Float.parseFloat(getAudioLength.getStdOutString());
		
		if (numImages == 1) {
			String slideshow = "ffmpeg -loop 1 -i .newTerm/images/0.jpg -c:v libx264 -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" -t " + length + " -pix_fmt yuv420p .newTerm/image_slide.mp4";
			BashCommand bash = new BashCommand(slideshow);
			bash.run();
			
			String merge = "ffmpeg -i .newTerm/image_slide.mp4 -i .newTerm/audio.wav -c:v copy -c:a aac -strict experimental -r 25 .newTerm/slideshow.mp4";
			BashCommand mergeCommand = new BashCommand(merge);
			mergeCommand.run();
			
		} else {
			Float freq = length / numImages;
			String merge = "ffmpeg -framerate 1/" + freq + " -i .newTerm/images/%01d.jpg -i .newTerm/audio.wav -c:v libx264 -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" -pix_fmt yuv420p -r 25 .newTerm/slideshow.mp4";
			BashCommand bash = new BashCommand(merge);
			bash.run();
			
		}
	}
	
	private void makeCreation() {
		String textCommand = "ffmpeg -i .newTerm/slideshow.mp4 -max_muxing_queue_size 1024 -vf drawtext=\"fontfile=myfont.ttf: text='" + term + "': fontcolor=white: fontsize=40: box=1: boxcolor=black@0.5: boxborderw=5: x=(w-text_w)/2: y=(h-text_h)/2\" -codec:a copy creations/" + creationName + ".mp4";
		BashCommand addtext = new BashCommand(textCommand);
		addtext.run();
	}
}
