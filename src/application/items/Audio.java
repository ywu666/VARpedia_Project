package application.items;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents audio made from a selected piece of text.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class Audio {
	
	private SimpleStringProperty mood;
	private SimpleStringProperty voice;
	private SimpleStringProperty text;
	public static final Map<String, String> voices;
	
	static {
		voices = new HashMap<>();
		voices.put("United Kingdom", "uk");
		voices.put("United States","us");
		voices.put("West Indies", "wi");
	}
	
	public Audio(String voice, String mood, String text) {
		this.setVoice(new SimpleStringProperty(voice));
		this.setMood(new SimpleStringProperty(mood));
		this.setText(new SimpleStringProperty(text));
	}

	/**
	 * @return mood setting for the audio
	 */
	public String getMood() {
		return mood.get();
	}

	/**
	 * Sets the mood setting for the audio
	 * 
	 * @param mood The mood that the audio chunk should have
	 */
	public void setMood(SimpleStringProperty mood) {
		this.mood = mood;
	}

	/**
	 * @return text the audio is speaking.
	 */
	public String getText() {
		return text.get();
	}

	/**
	 * Sets the text that the audio is speaking.
	 * 
	 * @param text The text that should be spoken with the audio settings specified
	 */
	public void setText(SimpleStringProperty text) {
		this.text = text;
	}

	/**
	 * @return voice setting specified for the audio
	 */
	public String getVoice() {
		return voice.get();
	}

	/**
	 * Sets the voice setting for the audio.
	 * 
	 * @param voice The voice option chosen for the audio
	 */
	public void setVoice(SimpleStringProperty voice) {
		this.voice = voice;
	}
	
	/**
	 * Gets the mood settings needed for the text to be spoken with the specified mood.
	 * 
	 * @param mood The mood selection or the settings that need to be retuned
	 * @return a String of the mood settings needed by espeak to speak with the specified mood
	 */
	public static String getMoodSettings(String mood) {
		if ("Happy".equals(mood)) {
			return " -s 250 -a 150 ";

		} else if ("Sad".equals(mood)) {
			return " -s 100 -a 80 ";
			
		} else {
			return " ";
		}
	}
}
