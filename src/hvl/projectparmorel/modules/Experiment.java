package hvl.projectparmorel.modules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.general.ModelFixer;
import hvl.projectparmorel.knowledge.Knowledge;
import hvl.projectparmorel.modelrepair.Solution;
import hvl.projectparmorel.utils.ParmorelUtils;

public abstract class Experiment {

	private String fixedModelFolderName;

	public Experiment(String fixedModelFolderName) {
		this.fixedModelFolderName = fixedModelFolderName + "/bestWeight";
		File experimentResultFolder = new File(this.fixedModelFolderName);
		if (!experimentResultFolder.exists()) {
			experimentResultFolder.mkdir();
		}
	}

	/**
	 * Repair the brokenModels with user settings.
	 * 
	 * Users:
	 * <ol start="0">
	 * <li>Prefer repairing higher in the error hierarchy and punish deletion</li>
	 * <li>Prefer repairing higher in the error hierarchy and shorter sequences of
	 * actions</li>
	 * <li>Prefer longer sequences of actions, repairing lower in the context
	 * hierarchy and reward modification of the original model</li>
	 * <li>Punish deletion</li>
	 * <li>Prefer shorter sequences of actions, punish deletion and punish
	 * modification of the original model</li>
	 * <li>Prefer longer sequences of actions and punish modification of the
	 * original model</li>
	 * <li>Prefer to repair higher in the context hierarchy</li>
	 * </ol>
	 * 
	 * @param brokenModels
	 * @param usersettings
	 */
	public void repairModels(File[] brokenModels, int usersettings) {
		ModelFixer ql = new EcoreQModelFixer(ParmorelUtils.generateUserSettings(usersettings));
		Logger logger = Logger.getLogger("MyLog");
		FileHandler fh = null;

		for (int i = 0; i < brokenModels.length; i++) {
			String iterationSpecificFolderName = fixedModelFolderName + "/model" + i;
			File fixedModelFolder = new File(iterationSpecificFolderName);
			if (!fixedModelFolder.exists()) {
				fixedModelFolder.mkdir();
			}
			try {
				fh = new FileHandler(iterationSpecificFolderName + "/LogFile.log");
				logger.addHandler(fh);
				SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter); 
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			ql.fixModel(brokenModels[i]);
			List<Solution> solutions = ql.getPossibleSolutions();

			Solution solution = selectSolution(solutions);
			solution.reward(true);

			File knowledgeFile = new File(Knowledge.KNOWLEDGE_FILE_NAME);
			File fixedModelFile = new File(iterationSpecificFolderName + "/" + brokenModels[i].getName());
			try {
				Files.copy(solution.getModel().toPath(), fixedModelFile.toPath());
				Files.copy(knowledgeFile.toPath(),
						new File(iterationSpecificFolderName + "/" + knowledgeFile.getName()).toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (fh != null) {
				logger.removeHandler(fh);
				fh.close();
			}
				
		}

		try {
			File knowledgeFile = new File(Knowledge.KNOWLEDGE_FILE_NAME);
			Files.move(knowledgeFile.getAbsoluteFile().toPath(),
					new File(fixedModelFolderName + "/" + knowledgeFile.getName()).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The selection strategy for the given experiment.
	 * 
	 * @param possibleSolutions
	 * @return chosen solution.
	 */
	protected abstract Solution selectSolution(List<Solution> possibleSolutions);

}
