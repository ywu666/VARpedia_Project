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

public class Creation implements Serializable {
	
	private static final long serialVersionUID = -6102326908274463170L;
	
	private String name;
	private String term;
	private String file;
	private Integer rating;
	private LocalDate lastViewed = null;
	
	public Creation(String name, String term, String file) {
		this.setName(name);
		this.setTerm(term);
		this.setFile(file);
	}
	
	public Creation(String name, String term) {
		this.setName(name);
		this.setTerm(term);
	}
	
	public Creation(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public LocalDate getLastViewed() {
		return lastViewed;
	}

	public void setLastViewed(LocalDate lastViewed) {
		this.lastViewed = lastViewed;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		
		if (!(obj instanceof Creation)) return false;
		
		Creation c = (Creation) obj;
		return this.getName().equals(c.getName());
	}
	
	private static void saveCreations(List<Creation> creations) {
		
		try {
			File file = new File(".creations/creations.txt");
			file.delete();
			
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream o = new ObjectOutputStream(f);
			
			// Write objects to file
			for (Creation c : creations) {
				o.writeObject(c);
			}
			
			o.close();
			f.close();

		} catch (IOException e) {
			System.out.println("Error initializing stream.");
			e.printStackTrace();
		}
	}
	
	public static List<Creation> getCreations() {
		List<Creation> creations = new ArrayList<>();
		
		try {
			FileInputStream fi = new FileInputStream(new File(".creations/creations.txt"));
			ObjectInputStream oi = new ObjectInputStream(fi);
			
			// Read creations
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
	
	public static void addCreation(Creation c) {
		List<Creation> creations = getCreations();
		creations.add(c);
		saveCreations(creations);
	}
	
	public static void removeCreation(Creation c) {
		BashCommand delCreation = new BashCommand("rm " + c.getFile());
		delCreation.run();
		
		List<Creation> creations = getCreations();
		creations.remove(c);
		saveCreations(creations);
	}
	
	public static void removeCreation(String name) {
		removeCreation(new Creation(name));
	}
	
	public static void creationPlayed(Creation c) {
		List<Creation> creations = getCreations();
		creations.remove(c);
		c.setLastViewed(LocalDate.now());
		creations.add(c);
		saveCreations(creations);
	}
	
	public static void creationRated(Creation c) {
		List<Creation> creations = getCreations();
		creations.remove(c);
		c.setRating(c.getRating());
		creations.add(c);
		saveCreations(creations);
	}
	
	public static boolean checkExists(String name) {
		List<Creation> creations = getCreations();
		
		return creations.contains(new Creation(name));
	}
}
