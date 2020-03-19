package hvl.projectparmorel.main;

import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.general.ModelFixer;

public class TestDifferentModelSelectionMethods {

	public static void main(String[] args) {
		ParmorelUtils.deleteExistingKnowledge();
		ModelFixer ql = new EcoreQModelFixer(ParmorelUtils.generateUserSettings(0));
		
	}
}
