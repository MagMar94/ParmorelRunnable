package hvl.projectparmorel.main.strategies;

import java.util.ArrayList;
import java.util.List;

import no.hvl.projectparmorel.ModelFixer;
import no.hvl.projectparmorel.qlearning.QSolution;
import no.hvl.projectparmorel.qlearning.ecore.EcoreQModelFixer;
import no.hvl.projectparmorel.qlearning.reward.PreferenceOption;

public class MaintainabilityCloseDistanceStrategy extends Strategy {

	public MaintainabilityCloseDistanceStrategy(String fixedModelFolderName) {
		super(fixedModelFolderName + "/MaintainabilityCloseDistance");
	}

	@Override
	protected QSolution selectSolution(List<QSolution> possibleSolutions) {
		return possibleSolutions.get(0);
	}

	
	@Override
	protected ModelFixer getModelFixer() {
		List<PreferenceOption> preferences = new ArrayList<PreferenceOption>();
		preferences.add(PreferenceOption.PREFER_CLOSE_DISTANCE_TO_ORIGINAL);
		preferences.add(PreferenceOption.PREFER_MAINTAINABILITY);
		return new EcoreQModelFixer(preferences);
	}
}
