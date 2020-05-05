package hvl.projectparmorel.main.strategies;

import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.ModelFixer;
import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.qlearning.QSolution;
import hvl.projectparmorel.reward.PreferenceOption;

/**
 * This strategy selects the model with lowest maintainability
 * 
 * @author Angela
 */
public class MaintainabilityStrategy extends Strategy {

	public MaintainabilityStrategy(String fixedModelFolderName) {
		super(fixedModelFolderName + "/maintainability");
	}

	@Override
	protected QSolution selectSolution(List<QSolution> possibleSolutions) {
		if (possibleSolutions.isEmpty()) {
			return null;
		}

		QSolution optimalSolution = possibleSolutions.get(0);
		double bestMaint = optimalSolution.calculateMaintainability();

		for (int i = 1; i < possibleSolutions.size(); i++) {
			double maint = possibleSolutions.get(i).calculateMaintainability();
			if (!isMeasurable(bestMaint) && isMeasurable(maint)) {
				bestMaint = maint;
				optimalSolution = possibleSolutions.get(i);
			} else if (isMeasurable(maint)
					&& ((maint < bestMaint || !isMeasurable(bestMaint)))) {
				optimalSolution = possibleSolutions.get(i);
				bestMaint = maint;
			} else if (bestMaint == maint
					&& optimalSolution.getWeight() > possibleSolutions.get(i).getWeight()) {
				optimalSolution = possibleSolutions.get(i);
			}
		}
		if (!isMeasurable(bestMaint)) {
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
		preferences.add(PreferenceOption.PREFER_MAINTAINABILITY);
		return new EcoreQModelFixer(preferences);
	}

}
