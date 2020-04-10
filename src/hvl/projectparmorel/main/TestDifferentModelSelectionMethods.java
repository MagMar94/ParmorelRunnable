package hvl.projectparmorel.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.modules.BestWeightStrategy;
import hvl.projectparmorel.modules.BestWeightStrategy02;
import hvl.projectparmorel.modules.BestWeightStrategy045;
import hvl.projectparmorel.modules.BestWeightStrategy136;
import hvl.projectparmorel.modules.BestWeightStrategy15;
import hvl.projectparmorel.modules.BestWeightStrategy2;
import hvl.projectparmorel.modules.BestWeightStrategy4;
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
//		experiments.add(new BestWeightStrategy02(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy045(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy136(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy15(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy2(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy4(fixedModelFolderName));
		
		long startTime = System.currentTimeMillis();
		for(Strategy experiment : experiments) {
			System.out.println("Starting new experiment: " + experiment.getClass().getName());
			ParmorelUtils.deleteExistingKnowledge();
			experiment.repairModels(brokenModels);
		}
		long endTime = System.currentTimeMillis();
		long executionTimme = (endTime - startTime);
		System.out.println("TOTAL TIME: " + executionTimme);
	}
}
