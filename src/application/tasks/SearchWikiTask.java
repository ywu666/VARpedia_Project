package application.tasks;

import javafx.concurrent.Task;

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

	public BashCommand getBashCommand() {
		return bashCommand;
	}
}
