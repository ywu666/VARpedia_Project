package application;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the important information about the creation currently being made.
 */
public class NewCreation {
	
	private String term;
	private String name;
	private String text;
	private Integer numImages;
	private List<Audio> audioFiles = new ArrayList<Audio>();
	
	NewCreation(String term, String text) {
		this.term = term;
		this.text = text;
	}
	
	public void addAudio(Audio a) {
		audioFiles.add(a);
	}
	
	public void removeAudio(Audio a) {
		audioFiles.remove(a);
	}
	
	public String getText() {
		return text;
	}
	
	public String getTerm() {
		return term;
	}
	
	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}
	
	public Integer getNumImages() {
		return numImages;
	}
	
	public void setAudioList(List<Audio> audioList) {
		audioFiles.clear();
		audioFiles.addAll(audioList);
	}
	
	public List<Audio> getAudioList() {
		return audioFiles;
	}
	
	public void setCreationName(String name) {
		this.name = name;
	}
	
	public String getCreationName() {
		return name;
	}
}
