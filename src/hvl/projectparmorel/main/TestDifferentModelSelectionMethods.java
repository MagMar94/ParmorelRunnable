package hvl.projectparmorel.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.modules.BestWeightStrategy;
import hvl.projectparmorel.modules.ClosestDistanceStrategy;
import hvl.projectparmorel.modules.Strategy;
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
		
		List<Strategy> experiments = new ArrayList<>();
		experiments.add(new BestWeightStrategy(fixedModelFolderName));
		experiments.add(new ClosestDistanceStrategy(fixedModelFolderName));
		
		long startTime = System.currentTimeMillis();
		for(Strategy experiment : experiments) {
			ParmorelUtils.deleteExistingKnowledge();
			experiment.repairModels(brokenModels, 0);
		}
		long endTime = System.currentTimeMillis();
		long executionTimme = (endTime - startTime);
		System.out.println("TOTAL TIME: " + executionTimme);
	}
}
