package hvl.projectparmorel.scripts;

import java.util.List;

import hvl.projectparmorel.qlearning.Error;

class ModelEval implements Evaluation {
	public final static String CSV_SEPARATION_SIGN = ";";
	public final static String CSV_HEADER = "Name" + CSV_SEPARATION_SIGN + "Supported" + CSV_SEPARATION_SIGN
			+ "Number of errors" + CSV_SEPARATION_SIGN + "Supported error codes" + CSV_SEPARATION_SIGN + "Unupported error codes" + CSV_SEPARATION_SIGN;
	private String modelName;
	private List<Error> supportedErrors;
	private List<Error> unsupportedErrors;

	ModelEval(String modelName, List<Error> supportedErrors, List<Error> unsupportedErrors) {
		this.modelName = modelName;
		this.supportedErrors = supportedErrors;
		this.unsupportedErrors = unsupportedErrors;
	}

	@Override
	public String getName() {
		return modelName;
	}

	@Override
	public String toCsvString() {
		return modelName + CSV_SEPARATION_SIGN + unsupportedErrors.isEmpty() + CSV_SEPARATION_SIGN
				+ supportedErrors.size() + CSV_SEPARATION_SIGN + getErrorCodesAsString(supportedErrors) + CSV_SEPARATION_SIGN + getErrorCodesAsString(unsupportedErrors) + CSV_SEPARATION_SIGN;
	}
	
	private String getErrorCodesAsString(List<Error> errors) {
		String errorList = "";
		
		for(Error e : errors) {
			errorList += e.getCode() + " :: ";
		}
		
		return errorList;
	}
}
