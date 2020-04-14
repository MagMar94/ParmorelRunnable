package hvl.projectparmorel.scripts.logreaders;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	/**
	 * Gets all the time from the log files, and all the metrics and combines them to a CSV.
	 * 
	 * The output should be the same order as the execution.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<ModelStrategyEvaluation> timeEvaluations = TimeLogReader.getTimeEvaluations(fc);
		
		System.out.println("Getting log file...");
		File file = getLogFile();
		if (file != null) {
			if (file.getName().endsWith(".txt")) {
				System.out.println("Intepreting metrics log...");
				List<Evaluation> metricEvaluations = interpretMetricLog(file);
				Map<String, ModelMetricEvaluation> metricEvaluationMap = getMetricEvaluationMap(metricEvaluations);
				
				List<Evaluation> finalEvaluations = new ArrayList<>();
				for(ModelStrategyEvaluation timeEvaluation : timeEvaluations) {
					if(metricEvaluationMap.containsKey(timeEvaluation.getName())) {
						ModelMetricEvaluation evaluation = metricEvaluationMap.get(timeEvaluation.getName());
						evaluation.setTime(timeEvaluation.getTime());
						finalEvaluations.add(evaluation);
					} else {
						ModelMetricEvaluation evaluation = new ModelMetricEvaluation();
						evaluation.setName(timeEvaluation.getName());
						evaluation.setTime(timeEvaluation.getTime());
						finalEvaluations.add(evaluation);
					}
				}

				System.out.println("Generating csv...");
				String csvString = generateCSVStringFrom(finalEvaluations);
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

	private static int logFileSelectedOption;
	/**
	 * Gets the log file to read
	 * 
	 * @return the file to lread
	 */
	private static File getLogFile() {
		fc.setDialogTitle("Select the metric log");
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		int returnVal = fc.showOpenDialog(null);

		try {
			EventQueue.invokeAndWait(new Runnable() {
			    @Override
			    public void run() {
			    	logFileSelectedOption = fc.showOpenDialog(null);
			    }
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (logFileSelectedOption == JFileChooser.APPROVE_OPTION) {
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
	private static List<Evaluation> interpretMetricLog(File logFile) {
		List<Evaluation> evaluations = new ArrayList<>();

		String log = readLog(logFile);
		if (log != null) {
			Pattern pattern = Pattern.compile(beforeEvaluationString + "(.*?)" + afterEvaluationString, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(log);

			while (matcher.find()) {
				String modelEvaluationString = matcher.group(1);
				ModelMetricEvaluation evaluation = new ModelMetricEvaluation();
				evaluation.setName(convertToPureModelName(findNameForModel(modelEvaluationString)));
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
	 * Puts the evaluations in a map from the model name to the evaluation
	 * 
	 * @param evaluations
	 * @return a map mapping from model name to evaluation
	 */
	private static Map<String, ModelMetricEvaluation> getMetricEvaluationMap(List<Evaluation> evaluations){
		Map<String, ModelMetricEvaluation> metricMap = new HashMap<>();
		
		for(Evaluation evaluation : evaluations) {
			if(evaluation instanceof ModelMetricEvaluation) {
				ModelMetricEvaluation e = (ModelMetricEvaluation) evaluation;
				metricMap.put(e.getName(), e);
			}
			
		}
		return metricMap;
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
	 * The names of the models are different in the logs. One log uses the whole
	 * path, and the other uses just the name. Additionally, one of them is based on
	 * the repaired model name, and the other the original name. The repaired
	 * version starts with an integer and an underscore, so we assume everything
	 * after the underscore is the original name.
	 * 
	 * @param modelName
	 * @return the assumed file name for the model in the time log
	 */
	private static String convertToPureModelName(String modelName) {
		int lastUnderscoreIndex = modelName.lastIndexOf("_");
		return modelName.substring(lastUnderscoreIndex + 1);
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
