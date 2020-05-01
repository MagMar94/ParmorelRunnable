package hvl.projectparmorel.modules;

import java.util.List;

import hvl.projectparmorel.qlearning.QSolution;

/**
 * This strategy selects the largest weight from all the results.
 *  
 * @author Magnus
 */
public class BestWeightStrategy extends Strategy {

	public BestWeightStrategy(String fixedModelFolderName) {
		super(fixedModelFolderName + "/bestWeight24");
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
