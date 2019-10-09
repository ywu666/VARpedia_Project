package application;

import java.time.LocalDate;
import java.time.Period;

import javafx.beans.property.SimpleStringProperty;

public class TableCreation {
	
	private SimpleStringProperty name;
	private SimpleStringProperty rating;
	private SimpleStringProperty lastViewed;
	
	private Creation creation;
	private Boolean ready;
	
	TableCreation(Creation creation) {
		ready = true;
		
		this.creation = creation;
		
		this.setName(new SimpleStringProperty(creation.getName()));
		String rating = "-";
		if (creation.getRating() != null) {
			rating = creation.getRating().toString();
		}
		
		this.setRating(new SimpleStringProperty(rating));
		
		String last = "-";
		if (creation.getLastViewed() != null) {
			last = Period.between(LocalDate.now(), creation.getLastViewed()).getDays() + " days";
		}
		
		this.setLastViewed(new SimpleStringProperty(last));
	}
	
	TableCreation(Creation creation, Boolean ready) {
		this(creation);
		this.ready = ready;
	}

	public String getName() {
		if (!ready) {
			return name.get() + " - not ready";
		}
		
		return name.get();
	}

	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public String getRating() {
		return rating.get();
	}

	public void setRating(SimpleStringProperty rating) {
		this.rating = rating;
	}

	public String getLastViewed() {
		return lastViewed.get();
	}

	public void setLastViewed(SimpleStringProperty lastViewed) {
		this.lastViewed = lastViewed;
	}
	
	public Creation getCreation() {
		return creation;
	}
	
	public Boolean getStatus() {
		return ready;
	}
}
