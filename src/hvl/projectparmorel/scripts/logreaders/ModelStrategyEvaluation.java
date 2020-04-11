package hvl.projectparmorel.scripts.logreaders;

import hvl.projectparmorel.scripts.Evaluation;

public class ModelStrategyEvaluation implements Evaluation {
	public static char CSV_SEPARATION_SIGN = ';';
	public final static String CSV_COLUMN_HEADER = "Filename" + CSV_SEPARATION_SIGN + "Time (ms)" + CSV_SEPARATION_SIGN;

	private String name;
	private int time;

	public ModelStrategyEvaluation() {
	}
	
	public ModelStrategyEvaluation(String name, int time) {
		this.name = name;
		this.time = time;
	}
	
	@Override
	public String toCsvString() {
		if(time < 0){
			return name + CSV_SEPARATION_SIGN + CSV_SEPARATION_SIGN;
		}
		return name + CSV_SEPARATION_SIGN + time + CSV_SEPARATION_SIGN;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
