package application.tasks;

import application.items.Audio;
import javafx.concurrent.Task;

/**
 * Task that merges the audio saved by the user.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class MergeAudioTask extends Task<Void> {

	private Object[] audioList;
	private Integer audioFileNum = 0;

	public MergeAudioTask(Object[] audioList) {
		this.audioList = audioList;
	}

	@Override
	protected Void call() throws Exception {
		// Creates new audio directory to hold all the audio files
		BashCommand mkDir = new BashCommand("mkdir -p .newTerm/audio");
		mkDir.run();

		// Saves all audio in the list provided
		for (Object audio : audioList) {
			if (audio instanceof Audio) {
				audioFileNum += 1;
				saveAudio((Audio)audio);
			}
		}

		// Merges all audio in the audio directory created previously
		String audio = "";
		for (int i = 1; i <= audioFileNum; i++) {
			audio += ".newTerm/audio/" + i + ".wav ";
		}
		String command = "sox " + audio + ".newTerm/audio.wav";
		BashCommand mergeAudio = new BashCommand(command);
		mergeAudio.run();

		return null;
	}

	/**
	 * Saves audio wav file based on the Audio object information passed in.
	 * 
	 * @param a The Audio object containing the information for the audio to be saved
	 */
	private void saveAudio(Audio a) {
		String voice = a.getVoice();
		String mood = a.getMood();
		String text = a.getText();

		// Makes new txt file containing the text to be spoken in the audio
		BashCommand saveTxt = new BashCommand("echo \"" + text + "\" > .newTerm/selection.txt");
		saveTxt.run();

		// Saves the audio
		BashCommand text2wave = new BashCommand("espeak -v en-" + Audio.voices.get(voice) + Audio.getMoodSettings(mood) + "-f .newTerm/selection.txt --stdout >.newTerm/audio/" + audioFileNum + ".wav");
		text2wave.run();

		// Removes the txt file containing the text to be spoken in the audio
		BashCommand rmTxtFile = new BashCommand("rm .newTerm/selection.txt");
		rmTxtFile.run();
	}
}
