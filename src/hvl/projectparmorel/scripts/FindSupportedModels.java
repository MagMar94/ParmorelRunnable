package hvl.projectparmorel.scripts;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JFileChooser;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import hvl.projectparmorel.ecore.EcoreErrorExtractor;
import hvl.projectparmorel.ecore.EcoreModel;
import hvl.projectparmorel.ecore.EcoreModelProcessor;
import hvl.projectparmorel.general.Error;
import hvl.projectparmorel.general.ErrorExtractor;
import hvl.projectparmorel.general.Model;
import hvl.projectparmorel.general.ModelProcessor;
import hvl.projectparmorel.general.ModelType;
import hvl.projectparmorel.knowledge.Knowledge;

public class FindSupportedModels {

	private static Logger logger;
	private static FileHandler fh;
	private static JFileChooser fc;
	private static List<File> supportedModels;

	public static void main(String[] args) {
		fc = new JFileChooser();
		supportedModels = new ArrayList<>();
		List<Evaluation> evaluations = getEvaluations(fc);

		File destinationFolder = getDestinationFolder(fc);
		if(!destinationFolder.exists())
			destinationFolder.mkdir();
		for (File f : supportedModels) {
			try {
				Files.copy(f.toPath(), new File(destinationFolder + "/" + f.getName()).toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		File csvFile = new File(destinationFolder + "/0_overview.csv");
		String csvString = generateCSVStringFrom(evaluations);
		System.out.println("Saving...");
		writeTo(csvString, csvFile);
		System.out.println("Finished!");

		if (fh != null) {
			logger.removeHandler(fh);
			fh.close();
		}
	}

	/**
	 * Promts the user for a folder containing all the results. It enters all the
	 * subdirectories and reads the log, extracting model name and time.
	 * 
	 * @return of all the evaluatons
	 */
	public static List<Evaluation> getEvaluations(JFileChooser fc) {
		initializeLogger();

		System.out.println("Getting model folder...");
		File[] models = getModels(fc);

		List<Evaluation> evaluations = new ArrayList<>();

		Knowledge knowledge = new Knowledge();
		
		for (File modelFile : models) {
			if (modelFile.getName().contains("ecore")) {
				System.out.println("Model: " + modelFile.getName());
				ErrorExtractor errorExtractor = new EcoreErrorExtractor();

				ResourceSet resourceSet = new ResourceSetImpl();
				resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
						new EcoreResourceFactoryImpl());

				URI uri = URI.createFileURI(modelFile.getAbsolutePath());
				Resource modelResource = resourceSet.getResource(uri, true);
				Model model = new EcoreModel(resourceSet, modelResource, uri);

				List<Error> allErrors = errorExtractor.extractErrorsFrom(model.getRepresentationCopy(), true);
				List<Error> unsupportedErrors = new ArrayList<>();
				System.out.println("Ectracted errors: " + allErrors.toString());
				for (Error e : allErrors) {
					if (ModelType.ECORE.doesNotSupportError(e.getCode())) {
						System.out.println(e.getCode() + " is not supported.");
						unsupportedErrors.add(e);
					}
				}
				allErrors.removeAll(unsupportedErrors);

				if (!allErrors.isEmpty()) {
					System.out.println("Analyzing model.");
					ModelProcessor modelProcessor = new EcoreModelProcessor(knowledge);
					Set<Integer> unsupportedErrorsCodes = modelProcessor.initializeQTableForErrorsInModel(model);
					System.out.println("Unsupported errors: " + unsupportedErrorsCodes.toString());
					for (Integer code : unsupportedErrorsCodes) {
						System.out.println(code + " is not supported.");
						model.getModelType().addUnsupportedErrorCode(code);
					}

					List<Error> supportedErrors = new ArrayList<>();
					supportedErrors.addAll(allErrors);
					for (Error error : supportedErrors) {
						if (unsupportedErrorsCodes.contains(error.getCode())) {
							unsupportedErrors.add(error);
						}
					}
					supportedErrors.removeAll(unsupportedErrors);
					if (unsupportedErrors.isEmpty()) {
						supportedModels.add(modelFile);
					}
					evaluations.add(new ModelEval(modelFile.getName(), supportedErrors, unsupportedErrors));
				} else {
					evaluations.add(new ModelEval(modelFile.getName(), new ArrayList<>(), unsupportedErrors));
				}
				System.out.println("Evaluation of model complete \n\n");
			}
		}

		return evaluations;
	}

	private static void initializeLogger() {
		logger = Logger.getLogger("SupportedModels");
		try {
			fh = new FileHandler("SupportedModels.log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private static int returnVal;

	private static File[] getModels(JFileChooser fc) {
		fc.setDialogTitle("Select the models");
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

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
			return fc.getSelectedFiles();
		} else {
			System.out.println("Cancelled by user.");
		}
		return null;
	}

	/**
	 * Opens a dialog that lets the user select the folder to store the mutated
	 * models.
	 * 
	 * @return a folder to store the models in, or null if the selected file is not
	 *         a model or operation is cancelled
	 */
	private static File getDestinationFolder(JFileChooser fc) {
		System.out.println("Getting destination file...");
		fc.setDialogTitle("Select the destination folder");
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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
			File selectedFile = fc.getSelectedFile();
			if (selectedFile.isDirectory()) {
				return selectedFile;
			}
			System.out.println("Selected file was not a folder.");
			return null;
		} else {
			System.out.println("Cancelled by user.");
			return null;
		}
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
			myWriter.write(ModelEval.CSV_HEADER + "\n");
			myWriter.write(content);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}
}