package application.items;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the important information about the creation currently in the process of being made.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class NewCreation {

	private String term;
	private String name;
	private String text;
	private Integer numImages;
	private List<Audio> audioFiles = new ArrayList<Audio>();

	public NewCreation(String term, String text) {
		this.term = term;
		this.text = text;
	}

	/**
	 * Adds audio to the list of audio for the creation being made.
	 * 
	 * @param a The Audio object representing the audio to be added
	 */
	public void addAudio(Audio a) {
		audioFiles.add(a);
	}

	/**
	 * Removes audio from the list of audio for the creation being made.
	 * 
	 * @param a The Audio object representing the audio to be removed
	 */
	public void removeAudio(Audio a) {
		audioFiles.remove(a);
	}

	/**
	 * Gets the text the user will then be able to highlight and create audio from.
	 * 
	 * @return the text for the creation
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the term searched for the current creation.
	 * 
	 * @return the term for the current creation being made
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * Sets the number of images for the creation.
	 * 
	 * @param numImages The number of images in the creation
	 */
	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	/**
	 * Gets the number of images for the creation.
	 * 
	 * @return the number of images to be included in the creation
	 */
	public Integer getNumImages() {
		return numImages;
	}

	/**
	 * Sets the list of audio for the current creation.
	 * 
	 * @param audioList The list of audio
	 */
	public void setAudioList(List<Audio> audioList) {
		audioFiles.clear();
		audioFiles.addAll(audioList);
	}

	/**
	 * Gets the list of audio for the current creation.
	 * 
	 * @return the audio list
	 */
	public List<Audio> getAudioList() {
		return audioFiles;
	}

	/**
	 * Sets the name of the creation.
	 * 
	 * @param name The creation name
	 */
	public void setCreationName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the creation.
	 * 
	 * @return the creation name
	 */
	public String getCreationName() {
		return name;
	}
}
