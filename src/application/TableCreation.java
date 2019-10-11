package application;

import java.time.LocalDate;
import java.time.Period;

import javafx.beans.property.SimpleStringProperty;

public class TableCreation {
	
	private SimpleStringProperty name;
	private SimpleStringProperty term;
	private Rating rating;
	private LastViewed lastViewed;
	
	private Creation creation;
	private Boolean ready = true;
	
	TableCreation(Creation creation) {
		ready = true;
		this.creation = creation;
		
		this.setName(new SimpleStringProperty(creation.getName()));
		this.setTerm(new SimpleStringProperty(creation.getTerm()));
		this.setRating(new Rating(creation));
		this.setLastViewed(new LastViewed(creation));
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

	public String getTerm() {
		return term.get();
	}

	public void setTerm(SimpleStringProperty term) {
		this.term = term;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public LastViewed getLastViewed() {
		return lastViewed;
	}

	public void setLastViewed(LastViewed lastViewed) {
		this.lastViewed = lastViewed;
	}
	
	public Creation getCreation() {
		return creation;
	}
	
	public Boolean getStatus() {
		return ready;
	}
	
	public class LastViewed implements Comparable<LastViewed> {
		
		Period diff;
		boolean watched;
		
		LastViewed(Creation c) {
			LocalDate last = creation.getLastViewed();
			
			if (last == null) {
				watched = false;
				diff = null;
			} else {
				watched = true;
				diff = Period.between(creation.getLastViewed(), LocalDate.now());
			}
		}
		
		public String toString() {
			if (!watched) {
				return "never";
			}
			return diff.getDays() + " days";
		}

		@Override
		public int compareTo(LastViewed o) {
			if (toString().equals(o.toString())) {
				return 0;
			} else if (o.diff == null) {
				return 1;
			} else if (diff == null) {
				return -1;
			} else if (diff.getDays() < o.diff.getDays()) {
				return 1;
			} else if (diff.getDays() < o.diff.getDays()) {
				return -1;
			}
			return 0;
		}
	}
	
	public class Rating implements Comparable<Rating> {
		Integer val;

		Rating(Creation c) {
			Integer cRating = creation.getRating();
			
			if (cRating == null) {
				val = 0;
			} else {
				val = cRating;
			}
		}
		
		public String toString() {
			if (val == 0) {
				return "-";
			}
			return val.toString();
		}
		
		@Override
		public int compareTo(Rating o) {
			return val.compareTo(o.val);
		}
	}
}
