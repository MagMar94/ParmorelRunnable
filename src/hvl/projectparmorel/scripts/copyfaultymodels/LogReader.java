package hvl.projectparmorel.scripts.copyfaultymodels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class LogReader {
	private static String beforeEvaluationString = "INFO: NEW MODEL\n";
	private static String afterEvaluationString = "hvl.modelrepair.CopyFaultyModels main";
	
	private static final JFileChooser fc = new JFileChooser();
	
	public static void main(String[] args) {
		System.out.println("Getting log file...");
		File file = getLogFile();
		if (file != null) {
			if (file.getName().endsWith(".log")) {
				System.out.println("Intepreting log...");
				List<ModelEvaluation> evaluations = interpretLog(file);
				System.out.println("Generating csv...");
				String csvString = generateCSVStringFrom(evaluations);
				System.out.println("Getting destination file...");
				File saveFile = getDestinationFile();
				if (saveFile != null) {
					System.out.println("Saving...");
					writeTo(csvString, saveFile);
					System.out.println("Finished!");
				}
			} else {
				System.out.println("ERROR: Unsupported file format.");
			}
		}
		System.exit(0);
	}

	/**
	 * Gets the log file to read
	 * 
	 * @return the file to lread
	 */
	private static File getLogFile() {
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		} else {
			System.out.println("Cancelled by user.");
			return null;
		}
	}

	/**
	 * Interprets the logs content and returns it as a list of model evaluations.
	 * 
	 * @param logFile
	 * @return a list of model evaluations
	 */
	private static List<ModelEvaluation> interpretLog(File logFile) {
		List<ModelEvaluation> evaluations = new ArrayList<>();

		String log = readLog(logFile);
		if (log != null) {
			Pattern pattern = Pattern.compile(beforeEvaluationString + "(.*?)" + afterEvaluationString, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(log);

			while (matcher.find()) {
				String modelEvaluationString = matcher.group(1);
				ModelEvaluation evaluation = new ModelEvaluation();
				
				evaluation.setName(findNameForModel(modelEvaluationString));
				evaluation.setErrors(findErrorsForModel(modelEvaluationString));
				
				evaluations.add(evaluation);
			}
		}

		return evaluations;
	}

	/**
	 * Reads the log file and returns the content as a String
	 * 
	 * @param logFile
	 * @return content as String
	 */
	private static String readLog(File logFile) {
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
	 * Finds a name for the model
	 * 
	 * @param modelEvaluationString
	 * @return the name
	 */
	private static String findNameForModel(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Name: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	/**
	 * Finds a name for the model
	 * 
	 * @param modelEvaluationString
	 * @return the name
	 */
	private static List<Error> findErrorsForModel(String modelEvaluationString) {
		List<Error> errors = new ArrayList<>();
		
		Pattern pattern = Pattern.compile("<error>\n" + "(.*?)" + "</error>\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String errorAsString = matcher.group(1);
			errors.add(findErrorFromString(errorAsString));
		}
		return errors;
	}

	private static Error findErrorFromString(String errorAsString) {
		Pattern codePattern = Pattern.compile("Code:" + "(.*?)" + "\n", Pattern.DOTALL);
		Pattern messagePattern = Pattern.compile("Message:" + "(.*?)" + "\n", Pattern.DOTALL);
		
		Matcher codeMatcher = codePattern.matcher(errorAsString);
		Matcher messageMatcher = messagePattern.matcher(errorAsString);
		
		while(codeMatcher.find()) {
			Integer code = Integer.parseInt(codeMatcher.group(1));
			while(messageMatcher.find()) {
				String message = messageMatcher.group(1);
				return new Error(code, message);// new Error(codePa);
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
	private static String generateCSVStringFrom(List<ModelEvaluation> evaluations) {
		String csvString = "";
		for (ModelEvaluation e : evaluations) {
			System.out.println(e.getName());
			csvString += e.toCsvString();
		}
		return csvString;
	}

	/**
	 * Writes content to file
	 * 
	 * @param content
	 * @param file
	 */
	private static void writeTo(String content, File file) {
		try {
			file.createNewFile();
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(ModelEvaluation.CSV_COLUMN_HEADER + "\n");
			myWriter.write(content);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}

	/**
	 * Gets the destination file to save the output to.
	 * 
	 * @return the file to save to
	 */
	private static File getDestinationFile() {
		JFrame parentFrame = new JFrame();
		fc.setDialogTitle("Specify a .csv-file to save the result to");
		int returnVal = fc.showSaveDialog(parentFrame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		} else {
			System.out.println("Cancelled by user.");
			return null;
		}
	}
}
