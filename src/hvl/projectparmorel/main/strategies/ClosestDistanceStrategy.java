package hvl.projectparmorel.main.strategies;

import java.util.ArrayList;
import java.util.List;

import no.hvl.projectparmorel.ModelFixer;
import no.hvl.projectparmorel.exceptions.DistanceUnavailableException;
import no.hvl.projectparmorel.qlearning.QSolution;
import no.hvl.projectparmorel.qlearning.ecore.EcoreQModelFixer;
import no.hvl.projectparmorel.qlearning.reward.PreferenceOption;

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
	protected QSolution selectSolution(List<QSolution> possibleSolutions) {
		if (possibleSolutions.isEmpty()) {
			return null;
		}

		QSolution optimalSolution = possibleSolutions.get(0);
		double shortestFromOriginal;
		try {
			shortestFromOriginal = optimalSolution.calculateDistanceFromOriginal();
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
		} catch (DistanceUnavailableException e) {
			// Could not measure distance
		}

		

		return optimalSolution;
	}

	/**
	 * Since @see
	 * {@link hvl.projectparmorel.Solution#calculateDistanceFromOriginal()}
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
		preferences.add(PreferenceOption.PUNISH_DELETION);
		return new EcoreQModelFixer(preferences);
	}

}
