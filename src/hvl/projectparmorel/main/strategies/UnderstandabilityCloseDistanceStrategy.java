package hvl.projectparmorel.main.strategies;

import java.util.ArrayList;
import java.util.List;

import hvl.projectparmorel.ModelFixer;
import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.qlearning.QSolution;
import hvl.projectparmorel.reward.PreferenceOption;

public class UnderstandabilityCloseDistanceStrategy extends Strategy {

	public UnderstandabilityCloseDistanceStrategy(String fixedModelFolderName) {
		super(fixedModelFolderName + "/UnderstandabilityCloseDistance");
	}

	@Override
	protected QSolution selectSolution(List<QSolution> possibleSolutions) {
		return possibleSolutions.get(0);
	}

	@Override
	protected ModelFixer getModelFixer() {
		List<PreferenceOption> preferences = new ArrayList<PreferenceOption>();
		preferences.add(PreferenceOption.PREFER_CLOSE_DISTANCE_TO_ORIGINAL);
		preferences.add(PreferenceOption.PREFER_UNDERSTANDABILITY);
		return new EcoreQModelFixer(preferences);
	}
}
