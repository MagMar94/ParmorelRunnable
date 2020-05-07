package hvl.projectparmorel.main.strategies;

import java.util.List;

import hvl.projectparmorel.utils.ParmorelUtils;
import no.hvl.projectparmorel.ModelFixer;
import no.hvl.projectparmorel.qlearning.QSolution;
import no.hvl.projectparmorel.qlearning.ecore.EcoreQModelFixer;

/**
 * This strategy selects the largest weight from all the results.
 *  
 * @author Magnus
 */
public class BestWeightStrategy045 extends Strategy {

	public BestWeightStrategy045(String fixedModelFolderName) {
		super(fixedModelFolderName + "/bestWeight045");
	}
	
	@Override
	protected ModelFixer getModelFixer() {
		return new EcoreQModelFixer(ParmorelUtils.generateUserSettings(1));
	}

	@Override
	protected QSolution selectSolution(List<QSolution> possibleSolutions) {
		if(possibleSolutions.isEmpty()) {
			return null;
		}
		
		QSolution optimalSolution = possibleSolutions.get(0);
		
		for(int i = 1; i < possibleSolutions.size(); i++) {
			if(possibleSolutions.get(i).getWeight() > optimalSolution.getWeight()) {
				optimalSolution = possibleSolutions.get(i);
			}
		}
		
		return optimalSolution;
	}

}
