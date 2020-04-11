package hvl.projectparmorel.modules;

import java.util.List;

import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.general.ModelFixer;
import hvl.projectparmorel.modelrepair.Solution;
import hvl.projectparmorel.utils.ParmorelUtils;

/**
 * This strategy selects the largest weight from all the results.
 *  
 * @author Magnus
 */
public class BestWeightStrategy02 extends Strategy {

	public BestWeightStrategy02(String fixedModelFolderName) {
		super(fixedModelFolderName + "/bestWeight02");
	}
	
	@Override
	protected ModelFixer getModelFixer() {
		return new EcoreQModelFixer(ParmorelUtils.generateUserSettings(1));
	}

	@Override
	protected Solution selectSolution(List<Solution> possibleSolutions) {
		if(possibleSolutions.isEmpty()) {
			return null;
		}
		
		Solution optimalSolution = possibleSolutions.get(0);
		
		for(int i = 1; i < possibleSolutions.size(); i++) {
			if(possibleSolutions.get(i).getWeight() > optimalSolution.getWeight()) {
				optimalSolution = possibleSolutions.get(i);
			}
		}
		
		return optimalSolution;
	}

}
