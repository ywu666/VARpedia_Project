package application.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows bash commands to be run with process builder.
 * 
 * @author Courtney Hunter
 */
public class BashCommand {

	Process process;
	String command;
	Boolean getStdOut;
	List<String> stdOut;

	/**
	 * @param command is the command to be executed
	 * @param getStdOut is a boolean defining whether std output is needed
	 */
	public BashCommand(String command, Boolean getStdOut) {
		this.command = command;
		this.getStdOut = getStdOut;
		stdOut = new ArrayList<String>();
	}
	
	/**
	 * @param command is the command to be executed
	 */
	public BashCommand(String command) {
		this(command, false);
	}

	/**
	 * Runs the process with the given command.
	 */
	public void run() {

		try {
			ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
			processBuilder.redirectErrorStream(true);
			processBuilder.directory();
			process = processBuilder.start();

			// If user specified they want the output, read the output
			if (getStdOut) {
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

				String line = null;
				while ((line = stdoutBuffered.readLine()) != null) {
					stdOut.add(line);
				}
			}

			int exitStatus = process.waitFor();

			if (exitStatus != 0) {
				return;
			}

			process.destroy();


		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// Expected if process cancelled
		}
	}

	/**
	 * Cancel the bash command process.
	 */
	public void cancelled() {
		process.destroy();
	}

	/**
	 * Gets the list of standard output.
	 * 
	 * @return the list of output strings from the bash command that was run
	 */
	public List<String> getStdOutList() {
		return stdOut;
	}

	/**
	 * Gets a String representing the standard output from the bash command that was run.
	 * 
	 * @return the standard output of the command in the form of a String
	 */
	public String getStdOutString() {
		String output = "";
		for (String s : stdOut) {
			output += s;
		}

		return output;
	}
}