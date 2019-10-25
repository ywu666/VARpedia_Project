package application.tasks;

import javafx.concurrent.Task;

/**
 * Task that searches Wikipedia for the term and gets the result.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class SearchWikiTask extends Task<Void> {

	String searchTerm;
	BashCommand bashCommand;

	public SearchWikiTask(String serchTerm) {
		this.searchTerm = serchTerm;
	}

	@Override
	protected Void call() throws Exception {

		String command = "wikit \"" + searchTerm + "\"";
		bashCommand = new BashCommand(command, true);
		bashCommand.run();

		return null;
	}

	/**
	 * Gets the result of the search.
	 * 
	 * @return String result of searching term
	 */
	public String getResult() {
		return bashCommand.getStdOutString();
	}
}
