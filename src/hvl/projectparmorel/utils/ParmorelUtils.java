package hvl.projectparmorel.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hvl.projectparmorel.knowledge.Knowledge;
import hvl.projectparmorel.reward.PreferenceOption;

public class ParmorelUtils {

	/**
	 * Generate user settings to run the algorithm with.
	 * 
	 * Users:
	 * <ol start="0">
	 * <li>Prefer repairing higher in the error hierarchy and punish deletion</li>
	 * <li>Prefer repairing higher in the error hierarchy and shorter sequences of
	 * actions</li>
	 * <li>Prefer longer sequences of actions, repairing lower in the context
	 * hierarchy and reward modification of the original model</li>
	 * <li>Punish deletion</li>
	 * <li>Prefer shorter sequences of actions, punish deletion and punish
	 * modification of the original model</li>
	 * <li>Prefer longer sequences of actions and punish modification of the
	 * original model</li>
	 * <li>Prefer to repair higher in the context hierarchy</li>
	 * </ol>
	 * 
	 * @param user (between 0 and 6) to generate settings for. The same integer till
	 *             always return the same settings.
	 * @return settings for the user, null if undefined user
	 */
	public static List<PreferenceOption> generateUserSettings(int user) {
		switch (user) {
		case 0:
			return new ArrayList<PreferenceOption>(Arrays.asList(new PreferenceOption[] { PreferenceOption.REPAIR_HIGH_IN_CONTEXT_HIERARCHY, PreferenceOption.PUNISH_DELETION }));
		case 1:
			return new ArrayList<PreferenceOption>(Arrays.asList(new PreferenceOption[] { PreferenceOption.SHORT_SEQUENCES_OF_ACTIONS, PreferenceOption.REPAIR_HIGH_IN_CONTEXT_HIERARCHY }));
		case 2:
			return new ArrayList<PreferenceOption>(Arrays.asList(new PreferenceOption[] { PreferenceOption.LONG_SEQUENCES_OF_ACTIONS, PreferenceOption.REPAIR_LOW_IN_CONTEXT_HIERARCHY, PreferenceOption.REWARD_MODIFICATION_OF_MODEL }));
		case 3:
			return new ArrayList<PreferenceOption>(Arrays.asList(new PreferenceOption[] { PreferenceOption.PUNISH_DELETION }));
		case 4:
			return new ArrayList<PreferenceOption>(Arrays.asList(new PreferenceOption[] { PreferenceOption.SHORT_SEQUENCES_OF_ACTIONS, PreferenceOption.PUNISH_DELETION, PreferenceOption.PUNISH_MODIFICATION_OF_MODEL }));
		case 5:
			return new ArrayList<PreferenceOption>(Arrays.asList(new PreferenceOption[] { PreferenceOption.LONG_SEQUENCES_OF_ACTIONS, PreferenceOption.PUNISH_MODIFICATION_OF_MODEL }));
		case 6:
			return new ArrayList<PreferenceOption>(Arrays.asList(new PreferenceOption[] { PreferenceOption.REPAIR_HIGH_IN_CONTEXT_HIERARCHY }));
		default:
			return null;
		}
	}
	
	/**
	 * Deletes the knowledge file.
	 */
	public static void deleteExistingKnowledge() {
		URI knowledgeUri = URI.create("file:///" + Knowledge.KNOWLEDGE_FILE_NAME);
		Path knowledgePath = Path.of(knowledgeUri);
		try {
			Files.deleteIfExists(knowledgePath);
		} catch (IOException e) {
			// Problem deleting
		}
	}

	/**
	 * Removes all files that are not ecore files from the list.
	 * 
	 * @param files
	 * @return a list containing all the ecore-files from the input-list.
	 */
	public static File[] removeFilesThatAreNotEcore(File[] files) {
		List<File> ecoreModels = new ArrayList<>();
		int numberOfEcoreFiles = 0;
		for(File file : files) {
			if(file.getName().contains(".ecore")) {
				ecoreModels.add(file);
				numberOfEcoreFiles++;
			}
		}
		
		File[] list = new File[numberOfEcoreFiles];
		return ecoreModels.toArray(list);
	}
}
