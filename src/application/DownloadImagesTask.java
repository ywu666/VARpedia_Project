package application;

import java.awt.image.BufferedImage;
import java.io.File;

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

public class DownloadImagesTask extends Task<Void> {

	private String term;
	
	public DownloadImagesTask(String term) {
		this.term = term;
	}
	
	@Override
	protected Void call() throws Exception {
		BashCommand imagesDir = new BashCommand("mkdir -p .newTerm/images .newTerm/selectedImages");
		imagesDir.run();
		downloadImages(term, 10);
		return null;
	}
	
	
	
	/**
	 * Downloads the specified number of images from Flickr with the term specified when they searched.
	 * Saves the images in a hidden folder with the rest of the information for the creation.
	 * @param term
	 * @param numberOfImages
	 */
	private void downloadImages(String term, Integer numberOfImages) {
		try {
			String apiKey = "5f87be2672e915465e33b57efa5ae005";
			String sharedSecret = "501c995d21d9fd77";
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

}
