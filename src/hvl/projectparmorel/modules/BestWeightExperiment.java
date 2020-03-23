package hvl.projectparmorel.modules;

import java.util.List;

import hvl.projectparmorel.modelrepair.Solution;

public class BestWeightExperiment extends Experiment {

	public BestWeightExperiment(String fixedModelFolderName) {
		super(fixedModelFolderName);
	}

	@Override
	protected Solution selectSolution(List<Solution> possibleSolutions) {
		if(possibleSolutions.isEmpty()) {
			return null;
		}
		
		Solution optimalSolution = possibleSolutions.get(0);
		
		for(int i = 1; i < possibleSolutions.size(); i++) {
			if(possibleSolutions.get(i).getWeight() > optimalSolution.getWeight()) {
				optimalSolution = possibleSolutions.get(i);
			}
		}
		
		return optimalSolution;
	}

}
