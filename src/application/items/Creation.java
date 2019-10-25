package application.items;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.tasks.BashCommand;

/**
 * Represents all the information needed for a creation.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class Creation implements Serializable {
	
	private static final long serialVersionUID = -6102326908274463170L;
	
	private String name;
	private String term;
	private String file;
	private Integer rating;
	private LocalDate lastViewed = null;
	
	public Creation(String name, String term, String file) {
		this.name = name;
		this.term = term;
		this.file = file;
	}
	
	public Creation(String name, String term) {
		this.name = name;
		this.term = term;
	}
	
	public Creation(String name) {
		this.name = name;
	}

	/**
	 * @return the name of the creation
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the term for the creation
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @return the name of the file for the creation
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @return the rating information for the creation
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * Sets the rating information for the creation.
	 * 
	 * @param rating An integer between 1 to 5 representing the confidence of the user with that creation
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * Get the LocalDate representing when the creation was last viewed.
	 * 
	 * @return the last time the creation was viewed
	 */
	public LocalDate getLastViewed() {
		return lastViewed;
	}

	/**
	 * Sets the LocalDate representing when the creation was last viewed.
	 * 
	 * @param lastViewed The last time the creation was viewed
	 */
	public void setLastViewed(LocalDate lastViewed) {
		this.lastViewed = lastViewed;
	}
	
	/**
	 * Overrides the equals method, determining when two objects are equal.
	 * 
	 * @param obj The object that is to be compared with the current one.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		
		if (!(obj instanceof Creation)) return false;
		
		Creation c = (Creation) obj;
		return this.getName().equals(c.getName());
	}
	
	/**
	 * Saves the creations to the creation text file containing all the information
	 * about the Creation objects that have been saved.
	 * 
	 * @param creations The list of Creation objects to be saved in the text file
	 */
	private static void saveCreations(List<Creation> creations) {
		
		try {
			File file = new File(".creations/creations.txt");
			file.delete();
			
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream o = new ObjectOutputStream(f);
			
			// Write Creation objects to the text file
			for (Creation c : creations) {
				o.writeObject(c);
			}
			
			o.close();
			f.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the current list of Creation objects from the creations text file.
	 * 
	 * @return The list of creation objects
	 */
	public static List<Creation> getCreations() {
		List<Creation> creations = new ArrayList<>();
		
		try {
			FileInputStream fi = new FileInputStream(new File(".creations/creations.txt"));
			ObjectInputStream oi = new ObjectInputStream(fi);
			
			// Read creations from text file, adding them to the list of Creation objects
			boolean cont = true;
			while (cont) {
				Creation c = (Creation) oi.readObject();
				if (c != null) {
					creations.add(c);
				} else {
					cont = false;
				}
			}
			
			oi.close();
			fi.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		} catch (EOFException e) {
			// Expected Exception. End of file was reached.
		} catch (IOException e) {
			System.out.println("Error initializing stream.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return creations;
	}
	
	/**
	 * Adds a creation to the list of creations, updating it in the creation text file.
	 * 
	 * @param c The creation to add to the list of creations.
	 */
	public static void addCreation(Creation c) {
		List<Creation> creations = getCreations();
		creations.add(c);
		saveCreations(creations);
	}
	
	/**
	 * Removes a creation from the list of creations.
	 * 
	 * @param c The creation to remove from the list of creations
	 */
	public static void removeCreation(Creation c) {
		// Deletes the creation video from the hidden creations directory
		BashCommand delCreation = new BashCommand("rm " + c.getFile());
		delCreation.run();
		
		// Removes the creation from the list and updates the list of creations in the text file
		List<Creation> creations = getCreations();
		creations.remove(c);
		saveCreations(creations);
	}
	
	/**
	 * Removes a creation from the list of creations.
	 * 
	 * @param name The name of the creation to remove from the list of creations
	 */
	public static void removeCreation(String name) {
		removeCreation(new Creation(name));
	}
	
	/**
	 * Updates the time the creation was last viewed to now.
	 * 
	 * @param c The creation that was viewed
	 */
	public static void creationPlayed(Creation c) {
		List<Creation> creations = getCreations();
		creations.remove(c);
		c.setLastViewed(LocalDate.now());
		creations.add(c);
		saveCreations(creations);
	}
	
	/**
	 * Adds the rating information to the creation.
	 * 
	 * @param c The creation that needs the rating updated
	 */
	public static void creationRated(Creation c) {
		List<Creation> creations = getCreations();
		creations.remove(c);
		c.setRating(c.getRating());
		creations.add(c);
		saveCreations(creations);
	}
	
	/**
	 * Checks the creation exists in the saved creations.
	 * 
	 * @param name The name of the creation that should be checked
	 * @return true if the creation exists, and false if it does not exist
	 */
	public static boolean checkExists(String name) {
		List<Creation> creations = getCreations();
		
		return creations.contains(new Creation(name));
	}
}
