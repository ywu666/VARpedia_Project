package application;

import java.util.ArrayList;
import java.util.List;

public class Creation {
	
	private String term;
	private String text;
	private Integer numImages;
	private List<Audio> audioFiles = new ArrayList<Audio>();
	
	Creation(String term, String text) {
		this.term = term;
		this.text = text;
	}
	
	public void addAudioFile(Integer fileNum, String mood) {
		audioFiles.add(new Audio(fileNum, mood));
	}
	
	public String getText() {
		return text;
	}
	
	public String getTerm() {
		return term;
	}
	
	public void setNumImages(Double numImages) {
		this.numImages = numImages.intValue();
	}
	
	public Integer getNumImages() {
		return numImages;
	}
	
	public List<Audio> getAudioList() {
		return audioFiles;
	}
}
