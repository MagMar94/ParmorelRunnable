package hvl.projectparmorel.modules;

import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.general.ModelFixer;
import hvl.projectparmorel.modelrepair.Solution;
import hvl.projectparmorel.reward.PreferenceOption;

/**
 * This strategy selects the model with highest reuse
 * 
 * @author Angela
 */
public class ReuseStrategy extends Strategy {

	public ReuseStrategy(String fixedModelFolderName) {
		super(fixedModelFolderName + "/reuse");
	}

	@Override
	protected Solution selectSolution(List<Solution> possibleSolutions) {
		if (possibleSolutions.isEmpty()) {
			return null;
		}

		Solution optimalSolution = possibleSolutions.get(0);
		double bestMetric = optimalSolution.calculateReuse();

		for (int i = 1; i < possibleSolutions.size(); i++) {
			double metric = possibleSolutions.get(i).calculateReuse();
			if (!isMeasurable(bestMetric) && isMeasurable(metric)) {
				bestMetric = metric;
				optimalSolution = possibleSolutions.get(i);
			} else if (isMeasurable(metric)
					&& ((metric < bestMetric || !isMeasurable(bestMetric)))) {
				optimalSolution = possibleSolutions.get(i);
				bestMetric = metric;
			} else if (bestMetric == metric
					&& optimalSolution.getWeight() > possibleSolutions.get(i).getWeight()) {
				optimalSolution = possibleSolutions.get(i);
			}
		}
		if (!isMeasurable(bestMetric)) {
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
		preferences.add(PreferenceOption.PREFER_REUSE);
		return new EcoreQModelFixer(preferences);
	}

}