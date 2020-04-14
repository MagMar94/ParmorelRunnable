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

public class TimeLogReader extends LogReader {

	public static void main(String[] args) {
		@SuppressWarnings("unchecked")
		List<Evaluation> evaluations = (List<Evaluation>)(List<?>) getTimeEvaluations(fc);
		if(evaluations != null) {
			System.out.println("Generating csv...");
			String csvString = generateCSVStringFrom(evaluations);
			System.out.println("Getting destination file...");
			File saveFile = getDestinationFile();
			if (saveFile != null) {
				System.out.println("Saving...");
				writeTo(csvString, saveFile);
				System.out.println("Finished!");
			}
		}

		System.exit(0);
	}
	
	/**
	 * Returns a time map, mappging from the name of the model to the time it took to repair it.
	 * 
	 * @return a map from model name to repairing time.
	 */
	public static Map<String, Integer> getTimeMap(JFileChooser fc){
		Map<String, Integer > timeMap = new HashMap<>();
		@SuppressWarnings("unchecked")
		List<Evaluation> evaluations = (List<Evaluation>)(List<?>) getTimeEvaluations(fc);
		for(Evaluation evaluation : evaluations) {
			if(evaluation instanceof ModelStrategyEvaluation) {
				ModelStrategyEvaluation mEvaluation = (ModelStrategyEvaluation) evaluation;
				timeMap.put(mEvaluation.getName(), mEvaluation.getTime());
			}	
		}
		
		return timeMap;
	}

	/**
	 * Promts the user for a folder containing all the results. It enters all the subdirectories and reads the log, extracting model name and time.
	 * 
	 * @return of all the evaluatons
	 */
	public static List<ModelStrategyEvaluation> getTimeEvaluations(JFileChooser fc) {
		System.out.println("Getting experiment result folder...");
		File topFolder = getLogFile(fc);
		if (topFolder != null && topFolder.isDirectory()) {
			File[] subFiles = topFolder.listFiles();

			List<ModelStrategyEvaluation> evaluations = new ArrayList<>();
			for (File file : subFiles) {
				if (file.isDirectory()) {
					System.out.println("Reading log for " + file.getName());
					File logFile = new File(file.getAbsoluteFile() + "/LogFile.log");
					ModelStrategyEvaluation evaluation = interpretLog(logFile);
					evaluations.add(evaluation);
				}
			}
			return evaluations;
		}
		return null;
	}

	private static int returnVal;
	private static File getLogFile(JFileChooser fc) {
		fc.setDialogTitle("Select the experiment result folder");
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//int returnVal = fc.showOpenDialog(null);
		
		try {
			EventQueue.invokeAndWait(new Runnable() {
			    @Override
			    public void run() {
			    	returnVal = fc.showOpenDialog(null);
			    }
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			if (folder.isDirectory()) {
				return folder;
			}
			System.out.println("The selected option is not a folder.");
		} else {
			System.out.println("Cancelled by user.");
		}
		return null;
	}

	private static ModelStrategyEvaluation interpretLog(File logFile) {
		String log = readLog(logFile);

		String modelName = findNameForModel(log);
		int timeRepairingModel = findTimeRepairingModel(log);

		return new ModelStrategyEvaluation(modelName, timeRepairingModel);
	}

	/**
	 * Finds a name for the model
	 * 
	 * @param modelEvaluationString
	 * @return the name
	 */
	private static String findNameForModel(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Repairing " + "(.*?)" + "\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * Finds the time it took to repair the model
	 * 
	 * @param log
	 * @return
	 */
	private static int findTimeRepairingModel(String modelEvaluationString) {
		Pattern pattern = Pattern.compile("Time repairing model: " + "(.*?)" + " ms\n", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(modelEvaluationString);
		while (matcher.find()) {
			return Integer.parseInt(matcher.group(1));
		}
		return -1;
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
			myWriter.write(ModelStrategyEvaluation.CSV_COLUMN_HEADER + "\n");
			myWriter.write(content);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}
}
