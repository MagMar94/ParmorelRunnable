package hvl.projectparmorel.scripts.logreaders;

import java.io.File;

import javax.swing.JFileChooser;

public class StrategyLogReader {

	private static final JFileChooser fc = new JFileChooser();
	
	public static void main(String[] args) {
		System.out.println("Getting experiment result folder...");
		File topFolder = getLogFile();
		if(topFolder != null && topFolder.isDirectory()) {
			File[] subFiles = topFolder.listFiles();
			for(File file : subFiles) {
				if(file.isDirectory()) {
					File logFile = new File(file.getAbsoluteFile() + "/LogFile.log");
				}
			}
		}
	}

	private static File getLogFile() {
		fc.setDialogTitle("Select the experiment result folder");
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			if(folder.isDirectory()) {
				return folder;
			}
			System.out.println("The selected option is not a folder.");
		} else {
			System.out.println("Cancelled by user.");
		}
		return null;
	}

}
