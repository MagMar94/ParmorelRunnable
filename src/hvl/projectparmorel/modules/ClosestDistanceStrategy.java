package hvl.projectparmorel.modules;

import java.util.List;

import hvl.projectparmorel.modelrepair.Solution;

/**
 * This strategy selects the closest distance from the original. If multiple
 * models have the same distance, the one with the largest weight is chosen.
 * 
 * @author Magnus
 */
public class ClosestDistanceStrategy extends Strategy {

	public ClosestDistanceStrategy(String fixedModelFolderName) {
		super(fixedModelFolderName + "/closestDistance");
	}

	@Override
	protected Solution selectSolution(List<Solution> possibleSolutions) {
		if (possibleSolutions.isEmpty()) {
			return null;
		}

		Solution optimalSolution = possibleSolutions.get(0);
		double shortestFromOriginal = optimalSolution.calculateDistanceFromOriginal();

		for (int i = 1; i < possibleSolutions.size(); i++) {
			double distanceFromOriginal = possibleSolutions.get(i).calculateDistanceFromOriginal();
			if (distanceFromOriginal < shortestFromOriginal) {
				optimalSolution = possibleSolutions.get(i);
				shortestFromOriginal = distanceFromOriginal;
			} else if (shortestFromOriginal == distanceFromOriginal
					&& optimalSolution.getWeight() > possibleSolutions.get(i).getWeight()) {
				optimalSolution = possibleSolutions.get(i);
			}
		}

		return optimalSolution;
	}

}
