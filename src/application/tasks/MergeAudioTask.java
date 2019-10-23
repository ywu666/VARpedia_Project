package application.tasks;

import application.items.Audio;
import javafx.concurrent.Task;

public class MergeAudioTask extends Task<Void> {

	private Object[] audioList;
	private Integer audioFileNum = 0;

	public MergeAudioTask(Object[] audioList) {
		this.audioList = audioList;
	}

	@Override
	protected Void call() throws Exception {

		BashCommand mkDir = new BashCommand("mkdir -p .newTerm/audio");
		mkDir.run();

		for (Object audio : audioList) {
			if (audio instanceof Audio) {
				audioFileNum += 1;
				saveAudio((Audio)audio);
			}
		}

		String audio = "";
		for (int i = 1; i <= audioFileNum; i++) {
			audio += ".newTerm/audio/" + i + ".wav ";
		}
		String command = "sox " + audio + ".newTerm/audio.wav";
		BashCommand mergeAudio = new BashCommand(command);
		mergeAudio.run();

		return null;
	}

	private void saveAudio(Audio a) {
		String voice = a.getVoice();
		String mood = a.getMood();
		String text = a.getText();

		BashCommand saveTxt = new BashCommand("echo \"" + text + "\" > .newTerm/selection.txt");
		saveTxt.run();

		BashCommand text2wave = new BashCommand("espeak -v en-" + Audio.voices.get(voice) + Audio.getMoodSettings(mood) + "-f .newTerm/selection.txt --stdout >.newTerm/audio/" + audioFileNum + ".wav");
		text2wave.run();

		BashCommand rmTxtFile = new BashCommand("rm .newTerm/selection.txt");
		rmTxtFile.run();
	}
}
