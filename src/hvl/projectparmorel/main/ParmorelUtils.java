package hvl.projectparmorel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public static List<Integer> generateUserSettings(int user) {
		switch (user) {
		case 0:
			return new ArrayList<Integer>(Arrays.asList(new Integer[] { 2, 4 }));
		case 1:
			return new ArrayList<Integer>(Arrays.asList(new Integer[] { 0, 2 }));
		case 2:
			return new ArrayList<Integer>(Arrays.asList(new Integer[] { 1, 3, 6 }));
		case 3:
			return new ArrayList<Integer>(Arrays.asList(new Integer[] { 4 }));
		case 4:
			return new ArrayList<Integer>(Arrays.asList(new Integer[] { 0, 4, 5 }));
		case 5:
			return new ArrayList<Integer>(Arrays.asList(new Integer[] { 1, 5 }));
		case 6:
			return new ArrayList<Integer>(Arrays.asList(new Integer[] { 2 }));
		default:
			return null;
		}
	}
}
