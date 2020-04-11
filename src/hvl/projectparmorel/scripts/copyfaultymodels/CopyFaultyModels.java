package hvl.projectparmorel.scripts.copyfaultymodels;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JFileChooser;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

/**
 * A script that lets the user select some models and a destination. All the
 * models that contain errors are copied to the destination.
 * 
 * @author Magnus
 *
 */
public class CopyFaultyModels {
	private static final JFileChooser fc = new JFileChooser();
	private static Logger logger;

	public static void main(String[] args) throws IOException {
		logger = Logger.getLogger("modelLogger");

		FileHandler fh = null;

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());

		File[] originalModelFiles = getFilesFromUser();
		File destinationFolder = getDestinationFolder();

		try {
			fh = new FileHandler(destinationFolder.getAbsolutePath() + "/00_models.log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (destinationFolder != null) {
			for (File f : originalModelFiles) {
				if (isEcore(f)) {
					System.out.println("Validating " + f.getName());
					Resource modelOriginal = getResourceFromFile(resourceSet, f);

					logger.info("NEW MODEL\nName: " + f.getName());
					try {
						if (errorsExistIn(modelOriginal)) {
							System.out.println("Model contains errors!");

							File newModelFile = getFileCopyInDestination(destinationFolder, f);
							Files.copy(f.toPath(), newModelFile.toPath());

							System.out.println("Copied faulty model.");
						} else {
							System.out.println("No errors, skipping model.");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						System.out.println("Error occured while handling files.");
					}
				} else {
					System.out.println("The specified file is not an Ecore-file. Ignoring...");
				}
				System.out.println();
			}
		}
		System.out.println("Finised copying broken models.");
		if (fh != null)
			fh.close();
		System.exit(0);
	}

	/**
	 * Opens a dialog that lets the user select multiple files and returns these.
	 * 
	 * @return a list of selected files, or an empty array if nothing is selected
	 */
	private static File[] getFilesFromUser() {
		fc.setDialogTitle("Select the files to check");
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFiles();
		} else {
			System.out.println("Cancelled by user.");
			return new File[0];
		}
	}

	/**
	 * Opens a dialog that lets the user select the folder to store the mutated
	 * models.
	 * 
	 * @return a folder to store the models in, or null if the selected file is not
	 *         a model or operation is cancelled
	 */
	private static File getDestinationFolder() {
		fc.setDialogTitle("Select the destination folder");
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);

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
	 * Checks that the parameter file is an ecore file
	 * 
	 * @param file
	 * @return true if the file is an Ecore file, false otherwise
	 */
	private static boolean isEcore(File file) {
		return file.getName().contains(".ecore");
	}

	/**
	 * Gets a resource from the specified resource set specified by the file.
	 * 
	 * @param resourceSet
	 * @param file
	 * @return the corresponding resource
	 */
	private static Resource getResourceFromFile(ResourceSet resourceSet, File file) {
		URI fileURI = URI.createFileURI(file.getAbsolutePath());
		return resourceSet.getResource(fileURI, true);
	}

	/**
	 * Gets a new file in destination with the same name as the original
	 * 
	 * @param destination
	 * @param original
	 * @return a new file in the destination
	 */
	private static File getFileCopyInDestination(File destination, File original) {
		return new File(destination.getAbsolutePath() + "/" + original.getName());
	}

	/**
	 * Extracts the errors from the provided model.
	 * 
	 * @param model
	 * @return a list of errors found in the model
	 */
	private static boolean errorsExistIn(Resource model) {
		Diagnostic diagnostic = validateMode(model);
		String errorLog = "Containtng errors:";
		if (diagnostic.getSeverity() != Diagnostic.OK) {
			for (Diagnostic d : diagnostic.getChildren()) {
				errorLog = errorLog + "\n<error>\nCode:" + d.getCode() + "\nMessage:" + d.getMessage() + "\n</error>";
			}
		}
		logger.info(errorLog);
		return (diagnostic.getSeverity() != Diagnostic.OK);
	}

	/**
	 * Validates the model
	 * 
	 * @param model
	 * @return the diagnostic for the model
	 */
	private static Diagnostic validateMode(Resource model) {
		EObject object = model.getContents().get(0);
		return Diagnostician.INSTANCE.validate(object);
	}

}
