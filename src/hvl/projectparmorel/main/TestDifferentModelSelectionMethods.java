package hvl.projectparmorel.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.modules.BestWeightExperiment;
import hvl.projectparmorel.modules.Experiment;
import hvl.projectparmorel.utils.ParmorelUtils;

public class TestDifferentModelSelectionMethods {
	
	public static final String FILE_PREFIX = "././";

	public static void main(String[] args) {
		ParmorelUtils.deleteExistingKnowledge();
		String originalModelsFolderName = FILE_PREFIX + "differentSelectionMethodsOriginals";
		File originalModelsFolder = new File(originalModelsFolderName);
		File[] brokenModels = originalModelsFolder.listFiles();
		brokenModels = ParmorelUtils.removeFilesThatAreNotEcore(brokenModels);

		String fixedModelFolderName = FILE_PREFIX + "differentSelectionMethodsFixed";
		File experimentResultFolder = new File(fixedModelFolderName);
		if(!experimentResultFolder.exists()) {
			experimentResultFolder.mkdir();
		}
		
		List<Experiment> experiments = new ArrayList<>();
		experiments.add(new BestWeightExperiment(fixedModelFolderName));
		
		for(Experiment experiment : experiments) {
			ParmorelUtils.deleteExistingKnowledge();
			experiment.repairModels(brokenModels, 0);
		}
	}
}
