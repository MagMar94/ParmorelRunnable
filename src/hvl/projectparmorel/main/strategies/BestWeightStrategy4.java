package hvl.projectparmorel.main.strategies;

import java.util.List;

import hvl.projectparmorel.ModelFixer;
import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.qlearning.QSolution;
import hvl.projectparmorel.utils.ParmorelUtils;

/**
 * This strategy selects the largest weight from all the results.
 *  
 * @author Magnus
 */
public class BestWeightStrategy4 extends Strategy {

	public BestWeightStrategy4(String fixedModelFolderName) {
		super(fixedModelFolderName + "/bestWeight4");
	}
	
	@Override
	protected ModelFixer getModelFixer() {
		return new EcoreQModelFixer(ParmorelUtils.generateUserSettings(3));
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
