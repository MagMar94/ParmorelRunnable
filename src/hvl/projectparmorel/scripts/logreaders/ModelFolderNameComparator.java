package hvl.projectparmorel.scripts.logreaders;

import java.io.File;
import java.util.Comparator;

public class ModelFolderNameComparator implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {
		String name1 = o1.getName();
		String name2 = o2.getName();
		
		if(name1.subSequence(0, 5).equals("model") && name2.subSequence(0, 5).equals("model")) {
			return compare(name1, name2);
		}

		return o1.compareTo(o2);
		
	}
	
	private int compare(String o1, String o2) {
		String s1 = (String) o1;
		String s2 = (String) o2;
		String s1modelnumber = s1.substring(5);
		String s2modelnumber = s2.substring(5);
		int s1number = Integer.parseInt(s1modelnumber);
		int s2number = Integer.parseInt(s2modelnumber);

		return s1number - s2number;
	}

}
