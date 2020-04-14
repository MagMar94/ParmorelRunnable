package hvl.projectparmorel.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

public abstract class LogReader {
	
	protected static final JFileChooser fc = new JFileChooser();
	
	/**
	 * Reads the log file and returns the content as a String
	 * 
	 * @param logFile
	 * @return content as String
	 */
	protected static String readLog(File logFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(logFile));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Generates a CSV-string from the evaluations.
	 * 
	 * @param evaluations
	 * @return a string in CSV-format
	 */
	protected static String generateCSVStringFrom(List<Evaluation> evaluations) {
		String csvString = "";
		for (Evaluation e : evaluations) {
			csvString += e.toCsvString() + "\n";
		}
		return csvString;
	}
}
