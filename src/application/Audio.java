package application;

import javafx.beans.property.SimpleStringProperty;

public class Audio {
	
	private Integer fileNum;
	private SimpleStringProperty mood;
	private SimpleStringProperty voice;
	private SimpleStringProperty text;
	
	public Audio(Integer fileNum, String voice, String mood, String text) {
		this.fileNum = fileNum;
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
}
