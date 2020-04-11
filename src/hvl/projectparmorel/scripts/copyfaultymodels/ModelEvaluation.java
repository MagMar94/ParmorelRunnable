package hvl.projectparmorel.scripts.copyfaultymodels;

import java.util.List;

public class ModelEvaluation {
	public static char CSV_SEPARATION_SIGN = ';';
	public final static String CSV_COLUMN_HEADER = "Filename" + CSV_SEPARATION_SIGN + "Error code" + CSV_SEPARATION_SIGN
			+ "Message" + CSV_SEPARATION_SIGN;

	private String name;
	private List<Error> errors;

	public ModelEvaluation() {
	}

	/**
	 * Puts the variable on a CSV-format
	 * 
	 * @return the evaluation on a CSV-string
	 */
	public String toCsvString() {
		String csvString = "";
		for (Error e : errors) {
			csvString = csvString + name + CSV_SEPARATION_SIGN + e.getCode() + CSV_SEPARATION_SIGN + e.getMessage()
					+ CSV_SEPARATION_SIGN + "\n";
		}
		return csvString.replace("null", "");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> erros) {
		this.errors = erros;
	}
}
