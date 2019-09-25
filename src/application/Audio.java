package application;

import javafx.beans.property.SimpleStringProperty;

public class Audio {
	
	private SimpleStringProperty fileName;
	private SimpleStringProperty mood;
	private String text;
	
	public Audio(Integer fileNum, String mood) {
		this.setFileName(new SimpleStringProperty("audio" + fileNum));
		this.setMood(new SimpleStringProperty(mood));
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
}
