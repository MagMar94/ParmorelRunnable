package hvl.projectparmorel.modules;

import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.general.ModelFixer;
import hvl.projectparmorel.modelrepair.Solution;
import hvl.projectparmorel.reward.PreferenceOption;

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
			if (!isMeasurable(shortestFromOriginal) && isMeasurable(distanceFromOriginal)) {
				shortestFromOriginal = distanceFromOriginal;
				optimalSolution = possibleSolutions.get(i);
			} else if (isMeasurable(distanceFromOriginal)
					&& ((distanceFromOriginal < shortestFromOriginal || !isMeasurable(shortestFromOriginal)))) {
				optimalSolution = possibleSolutions.get(i);
				shortestFromOriginal = distanceFromOriginal;
			} else if (shortestFromOriginal == distanceFromOriginal
					&& optimalSolution.getWeight() > possibleSolutions.get(i).getWeight()) {
				optimalSolution = possibleSolutions.get(i);
			}
		}
		if (!isMeasurable(shortestFromOriginal)) {
			return null;
		}

		return optimalSolution;
	}

	/**
	 * Since @see
	 * {@link hvl.projectparmorel.modelrepair.Solution#calculateDistanceFromOriginal()}
	 * returns -1 if the distance can not be measured, this method returns true if
	 * the distance is a valid measurable distance and false otherwise.
	 * 
	 * @param distance
	 * @return true if the distance is a valid measurable distance, false otherwise.
	 */
	private boolean isMeasurable(double distance) {
		return distance != -1;
	}
	
	@Override
	protected ModelFixer getModelFixer() {
		List<PreferenceOption> preferences = new ArrayList<PreferenceOption>();
		preferences.add(PreferenceOption.PREFER_CLOSE_DISTANCE_TO_ORIGINAL);
		return new EcoreQModelFixer(preferences);
	}

}
