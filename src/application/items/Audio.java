package application.items;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents an audio file made from a selected piece of text.
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

	public String getMood() {
		return mood.get();
	}

	public void setMood(SimpleStringProperty mood) {
		this.mood = mood;
	}

	public String getText() {
		return text.get();
	}

	public void setText(SimpleStringProperty text) {
		this.text = text;
	}

	public String getVoice() {
		return voice.get();
	}

	public void setVoice(SimpleStringProperty voice) {
		this.voice = voice;
	}
	
	public static String getMoodSettings(String mood) {
		if ("Happy".equals(mood)) {
			return " -s 250 -a 150 ";

		} else if ("Sad".equals(mood)) {
			return " -s 100 -a 80 ";
		}else {
			return " ";
		}
	}
}
