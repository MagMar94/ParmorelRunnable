package hvl.projectparmorel.scripts.logreaders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import hvl.projectparmorel.scripts.Evaluation;
import hvl.projectparmorel.scripts.LogReader;

/**
 * A script that can read the metrics log and extract the necasary information.
 * This is returned as a csv.
 * 
 * @author Magnus
 *
 */
public class MetricsLogReader extends LogReader {
	private static String beforeEvaluationString = "---------------------------------------------------\n";
	private static String afterEvaluationString = "---------------------------------------------------\n";

	private static final JFileChooser fc = new JFileChooser();

	public static void main(String[] args) {
		System.out.println("Getting log file...");
		File file = getLogFile();
		if (file != null) {
			if (file.getName().endsWith(".txt")) {
				System.out.println("Intepreting log...");
				List<Evaluation> evaluations = interpretLog(file);
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
	private static List<Evaluation> interpretLog(File logFile) {
		List<Evaluation> evaluations = new ArrayList<>();

		String log = readLog(logFile);
		if (log != null) {
			Pattern pattern = Pattern.compile(beforeEvaluationString + "(.*?)" + afterEvaluationString, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(log);

			while (matcher.find()) {
				String modelEvaluationString = matcher.group(1);
				ModelMetricEvaluation evaluation = new ModelMetricEvaluation();
				evaluation.setName(findNameForModel(modelEvaluationString));
				evaluation.setNumberOfMetaclasses(findNumberOfMetaclassesFrom(modelEvaluationString));
				evaluation.setNumberOfReferences(findNumberOfReferencesFrom(modelEvaluationString));
				evaluation.setNumberOfAttributes(findNumberOfAttributesFrom(modelEvaluationString));
				evaluation.setDit(findDITFrom(modelEvaluationString));
				evaluation.setHagg(findHAggFrom(modelEvaluationString));
				evaluation.setUnidirectionalRef(findNumberOfUnidirectionalRefFrom(modelEvaluationString));
				evaluation.setOppositeRef(findNumberOfOppositeRefFrom(modelEvaluationString));
				evaluation.setMaintainability(findMaintainabilityFrom(modelEvaluationString));
				evaluation.setUnderstandability(findUnderstandabilityFrom(modelEvaluationString));
				evaluation.setComplexity(findComplexityFrom(modelEvaluationString));
				evaluation.setMetamodelReuse(findReuseFrom(modelEvaluationString));
				evaluation.setRelaxationIndexOfMetamodel(findRelaxationIndexFrom(modelEvaluationString));

				evaluations.add(evaluation);
			}
		}

		return evaluations;
	}

	/**
	 * Finds a name for the model
	 * 
	 * @param modelEvaluationString
	 * @return the name
	 */
	private static String findNameForModel(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Analyzing model: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * Finds the number of metaclasses
	 * 
	 * @param modelEvaluationString
	 * @return the number of metaclasses
	 */
	private static Integer findNumberOfMetaclassesFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Number of metaclasses: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Integer.parseInt(number);
		}
		return null;
	}

	/**
	 * Finds the number of references
	 * 
	 * @param modelEvaluationString
	 * @return the number of references
	 */
	private static Integer findNumberOfReferencesFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Number of references: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Integer.parseInt(number);
		}
		return null;
	}

	/**
	 * Finds the number of attributes
	 * 
	 * @param modelEvaluationString
	 * @return the number of attributes
	 */
	private static Integer findNumberOfAttributesFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Number of attributes: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Integer.parseInt(number);
		}
		return null;
	}

	/**
	 * Finds the DIT
	 * 
	 * @param modelEvaluationString
	 * @return the DIT
	 */
	private static Integer findDITFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("DIT: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String DIT = matcher.group(1);
			return Integer.parseInt(DIT);
		}
		return null;
	}

	/**
	 * Finds the HAgg
	 * 
	 * @param modelEvaluationString
	 * @return the HAgg:
	 */
	private static Integer findHAggFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("HAgg: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String HAgg = matcher.group(1);
			return Integer.parseInt(HAgg);
		}
		return null;
	}

	/**
	 * Finds the number of unidirectional references
	 * 
	 * @param modelEvaluationString
	 * @return the number of unidirectional references
	 */
	private static Integer findNumberOfUnidirectionalRefFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Unidirectional Ref: " + "(.*?)" + " ", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Integer.parseInt(number);
		}
		return null;
	}

	/**
	 * Finds the number of opposite references
	 * 
	 * @param modelEvaluationString
	 * @return the number of opposite references
	 */
	private static Integer findNumberOfOppositeRefFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Opposite refs: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Integer.parseInt(number);
		}
		return null;
	}

	/**
	 * Finds the maintainability
	 * 
	 * @param modelEvaluationString
	 * @return the maintainability
	 */
	private static Double findMaintainabilityFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Evaluation of Maintainability of the metamodel: " + "(.*?)" + "\n",
				Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Double.parseDouble(number);
		}
		return null;
	}

	/**
	 * Finds the understandability
	 * 
	 * @param modelEvaluationString
	 * @return the understandability
	 */
	private static Double findUnderstandabilityFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Evaluation of Understandability of the metamodel: " + "(.*?)" + "\n",
				Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Double.parseDouble(number);
		}
		return null;
	}

	/**
	 * Finds the complexity
	 * 
	 * @param modelEvaluationString
	 * @return the complexity
	 */
	private static Double findComplexityFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Evaluation of Complexity of the metamodel: " + "(.*?)" + "\n",
				Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Double.parseDouble(number);
		}
		return null;
	}

	/**
	 * Finds the reuse
	 * 
	 * @param modelEvaluationString
	 * @return the reuse
	 */
	private static Double findReuseFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Evaluation of Reuse of the metamodel: " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Double.parseDouble(number);
		}
		return null;
	}

	/**
	 * Finds the Relaxation Index
	 * 
	 * @param modelEvaluationString
	 * @return the Relaxation Index
	 */
	private static Double findRelaxationIndexFrom(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Evaluation of Relaxation Index of the metamodel: " + "(.*?)" + "\n",
				Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			String number = matcher.group(1);
			return Double.parseDouble(number);
		}
		return null;
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
			myWriter.write(ModelMetricEvaluation.CSV_COLUMN_HEADER + "\n");
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
