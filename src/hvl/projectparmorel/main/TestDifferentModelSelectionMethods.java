
 package hvl.projectparmorel.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.main.strategies.BestWeightStrategy;
import hvl.projectparmorel.main.strategies.BestWeightStrategy02;
import hvl.projectparmorel.main.strategies.BestWeightStrategy045;
import hvl.projectparmorel.main.strategies.BestWeightStrategy136;
import hvl.projectparmorel.main.strategies.BestWeightStrategy15;
import hvl.projectparmorel.main.strategies.BestWeightStrategy2;
import hvl.projectparmorel.main.strategies.BestWeightStrategy4;
import hvl.projectparmorel.main.strategies.ClosestDistanceStrategy;
import hvl.projectparmorel.main.strategies.ComplexityCloseDistanceStrategy;
import hvl.projectparmorel.main.strategies.MaintainabilityCloseDistanceStrategy;
import hvl.projectparmorel.main.strategies.MaintainabilityStrategy;
import hvl.projectparmorel.main.strategies.RelaxationCloseDistanceStrategy;
import hvl.projectparmorel.main.strategies.ReuseCloseDistanceStrategy;
import hvl.projectparmorel.main.strategies.Strategy;
import hvl.projectparmorel.main.strategies.UnderstandabilityCloseDistanceStrategy;
import hvl.projectparmorel.utils.ParmorelUtils;

public class TestDifferentModelSelectionMethods {
	
	public static final String FILE_PREFIX = "././";

	public static void main(String[] args) {
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
//		experiments.add(new BestWeightStrategy(fixedModelFolderName));
//		experiments.add(new ClosestDistanceStrategy(fixedModelFolderName));
//		experiments.add(new MaintainabilityStrategy(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy02(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy045(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy136(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy15(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy2(fixedModelFolderName));
//		experiments.add(new BestWeightStrategy4(fixedModelFolderName));
		experiments.add(new MaintainabilityCloseDistanceStrategy(fixedModelFolderName));
		experiments.add(new UnderstandabilityCloseDistanceStrategy(fixedModelFolderName));
		experiments.add(new ComplexityCloseDistanceStrategy(fixedModelFolderName));
		experiments.add(new ReuseCloseDistanceStrategy(fixedModelFolderName));
		experiments.add(new RelaxationCloseDistanceStrategy(fixedModelFolderName));
		
		long startTime = System.currentTimeMillis();
		for(Strategy experiment : experiments) {
			long experimentStartTime = System.currentTimeMillis();
			System.out.println("Starting new experiment: " + experiment.getClass().getName());
			ParmorelUtils.deleteExistingKnowledge();
			experiment.repairModels(brokenModels);
			experiment.setExperimentTime(System.currentTimeMillis() - experimentStartTime);
		}
		long endTime = System.currentTimeMillis();
		long executionTimme = (endTime - startTime);
		long hours = (executionTimme / (1000 * 60 * 60)) % 24;
		long minutes = (executionTimme / (1000 * 60)) % 60;
		long seconds = (executionTimme / 1000) % 60;
		long millis = executionTimme % 1000;
		
		System.out.println("TOTAL TIME: " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds and " + millis + " ms. (" + executionTimme + " ms)");
		
		for(Strategy experiment : experiments) {
			System.out.println("Time for experiment " + experiment.getFolderName() + ": " + experiment.getExperimentTime() + " ms");
		}
	}
}
