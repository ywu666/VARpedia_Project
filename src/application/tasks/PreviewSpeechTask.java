package application.tasks;

import application.items.Audio;
import javafx.concurrent.Task;

public class PreviewSpeechTask extends Task<Void> {
	private final String command;
	private BashCommand preview;

	public PreviewSpeechTask(String voice, String mood, String sayText) {
		command = "espeak -v " + voice + Audio.getMoodSettings(mood) + "\"" + sayText + "\"";
	}

	@Override
	protected Void call() throws Exception {

		preview = new BashCommand(command);
		preview.run();

		return null;
	}

	@Override
	protected void cancelled() {
		preview.cancelled();
	}
}