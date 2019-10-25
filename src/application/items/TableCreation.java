package application.items;

import java.time.LocalDate;
import java.time.Period;

import javafx.beans.property.SimpleStringProperty;

/**
 * Holds the important information about the creation to be added to the table.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class TableCreation {
	
	private SimpleStringProperty name;
	private SimpleStringProperty term;
	private Rating rating;
	private LastViewed lastViewed;
	
	private Creation creation;
	private Boolean ready = true;
	
	public TableCreation(Creation creation, boolean ready) {
		this.ready = ready;
		this.creation = creation;
		
		this.setName(new SimpleStringProperty(creation.getName()));
		this.setTerm(new SimpleStringProperty(creation.getTerm()));
		this.setRating(new Rating(creation));
		this.setLastViewed(new LastViewed(creation));
	}
	
	public TableCreation(Creation creation) {
		this(creation, true);
	}

	/**
	 * Gets the name of the creation to be displayed in the table.
	 * 
	 * @return the creation name to be displayed in the table
	 */
	public String getName() {
		// If the creation is not ready show this
		if (!ready) {
			return name.get() + " - not ready";
		}
		
		return name.get();
	}

	/**
	 * Sets the name property of the creation.
	 * 
	 * @param name The name of the creation
	 */
	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	/**
	 * Gets the term for the creation.
	 * 
	 * @return the term of the creation
	 */
	public String getTerm() {
		return term.get();
	}

	/**
	 * Sets the term for the creation.
	 * 
	 * @param term The term information for the creation
	 */
	public void setTerm(SimpleStringProperty term) {
		this.term = term;
	}

	/**
	 * Gets the rating associated with the creation.
	 * 
	 * @return the rating for the creation
	 */
	public String getRating() {
		return rating.toString();
	}

	/**
	 * Sets the rating for the creation.
	 * @param rating
	 */
	public void setRating(Rating rating) {
		this.rating = rating;
	}

	/**
	 * Gets the last time the creation was viewed.
	 * 
	 * @return the last time the creation was viewed
	 */
	public LastViewed getLastViewed() {
		return lastViewed;
	}

	/**
	 * Sets the last time the creation was viewed.
	 * 
	 * @param lastViewed The last time the creation was viewed
	 */
	public void setLastViewed(LastViewed lastViewed) {
		this.lastViewed = lastViewed;
	}
	
	/**
	 * Gets the Creation object associated with the TableCreation.
	 * 
	 * @return the Creation that the table object TableCreation is based on
	 */
	public Creation getCreation() {
		return creation;
	}
	
	/**
	 * Gets the status of the creation.
	 * 
	 * @return true if the creation is ready, false if it is not
	 */
	public Boolean getStatus() {
		return ready;
	}
	
	/**
	 * Represents the information associated with when the creation was last viewed.
	 * 
	 * @author Courtney Hunter and Yujia Wu
	 */
	public class LastViewed implements Comparable<LastViewed> {
		
		Period diff;
		boolean watched;
		
		private LastViewed(Creation c) {
			LocalDate last = creation.getLastViewed();
			
			if (last == null) {
				watched = false;
				diff = null;
			} else {
				watched = true;
				diff = Period.between(creation.getLastViewed(), LocalDate.now());
			}
		}
		
		@Override
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
	
	/**
	 * Represents the rating of a creation.
	 * 
	 * @author Courtney Hunter and Yujia Wu
	 */
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
		
		@Override
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
