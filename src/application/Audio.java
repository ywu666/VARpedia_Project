package application;

import javafx.beans.property.SimpleStringProperty;

public class Audio {
	
	private SimpleStringProperty fileName;
	private SimpleStringProperty mood;
	private SimpleStringProperty text;
	
	public Audio(Integer fileNum, String mood, String text) {
		this.setFileName(new SimpleStringProperty("audio" + fileNum));
		this.setMood(new SimpleStringProperty(mood));
		this.setText(new SimpleStringProperty(text));
	}

	public String getFileName() {
		return fileName.get();
	}

	public void setFileName(SimpleStringProperty fileName) {
		this.fileName = fileName;
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
}
