package hvl.projectparmorel.main.strategies;

import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.ModelFixer;
import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.qlearning.QSolution;
import hvl.projectparmorel.reward.PreferenceOption;

/**
 * This strategy selects the model with highest relaxation
 * 
 * @author Angela
 */
public class RelaxationStrategy extends Strategy {

	public RelaxationStrategy(String fixedModelFolderName) {
		super(fixedModelFolderName + "/relaxation");
	}

	@Override
	protected QSolution selectSolution(List<QSolution> possibleSolutions) {
		if (possibleSolutions.isEmpty()) {
			return null;
		}

		QSolution optimalSolution = possibleSolutions.get(0);
		double bestMetric = optimalSolution.calculateRelaxation();

		for (int i = 1; i < possibleSolutions.size(); i++) {
			double metric = possibleSolutions.get(i).calculateRelaxation();
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
		preferences.add(PreferenceOption.PREFER_RELAXATION);
		return new EcoreQModelFixer(preferences);
	}

}
