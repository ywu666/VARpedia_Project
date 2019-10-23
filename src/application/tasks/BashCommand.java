package application.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BashCommand {

	Process process;
	String command;
	Boolean getStdOut;
	List<String> stdOut = new ArrayList<String>();

	/**
	 * Helps with running bash commands.
	 * @param command is the command to be executed
	 * @param getStdOut is a boolean defining whether std output is needed
	 */
	public BashCommand(String command, Boolean getStdOut) {
		this.command = command;
		this.getStdOut = getStdOut;
		stdOut = new ArrayList<String>();
	}

	public BashCommand(String command) {
		this(command, false);
	}

	public void run() {

		try {
			ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
			processBuilder.redirectErrorStream(true);
			processBuilder.directory();
			process = processBuilder.start();

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

	public void cancelled() {
		process.destroy();
	}

	public List<String> getStdOutList() {
		return stdOut;
	}

	public String getStdOutString() {
		String output = "";
		for (String s : stdOut) {
			output += s;
		}

		return output;
	}
}