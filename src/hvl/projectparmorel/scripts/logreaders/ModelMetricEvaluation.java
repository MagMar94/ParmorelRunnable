package hvl.projectparmorel.scripts.logreaders;

public class ModelMetricEvaluation {
	public static char CSV_SEPARATION_SIGN = ';';
	public final static String CSV_COLUMN_HEADER = "Filename" + CSV_SEPARATION_SIGN + "Unidirectional refs"
			+ CSV_SEPARATION_SIGN + "Opposite refs" + CSV_SEPARATION_SIGN + "Number of metaclasses"
			+ CSV_SEPARATION_SIGN + "Number of references" + CSV_SEPARATION_SIGN + "Number of attributes"
			+ CSV_SEPARATION_SIGN + "DIT" + CSV_SEPARATION_SIGN + "HAgg" + CSV_SEPARATION_SIGN + "Maintainability"
			+ CSV_SEPARATION_SIGN + "Understantability" + CSV_SEPARATION_SIGN + "Complexity" + CSV_SEPARATION_SIGN
			+ "Reuse of the metamodel" + CSV_SEPARATION_SIGN + "Relaxation index of the metamodel"
			+ CSV_SEPARATION_SIGN;

	private String name;
	private Integer unidirectionalRef;
	private Integer oppositeRef;
	private Integer numberOfMetaclasses;
	private Integer numberOfReferences;
	private Integer numberOfAttributes;
	private Integer dit;
	private Integer hagg;
	private Double maintainability;
	private Double understandability;
	private Double complexity;
	private Double metamodelReuse;
	private Double relaxationIndexOfMetamodel;

	public ModelMetricEvaluation() {
	}

	/**
	 * Puts the variable on a CSV-format
	 * 
	 * @return the evaluation on a CSV-string
	 */
	public String toCsvString() {
		String csvString = name + CSV_SEPARATION_SIGN + unidirectionalRef + CSV_SEPARATION_SIGN + oppositeRef + CSV_SEPARATION_SIGN
				+ numberOfMetaclasses + CSV_SEPARATION_SIGN + numberOfReferences + CSV_SEPARATION_SIGN
				+ numberOfAttributes + CSV_SEPARATION_SIGN + dit + CSV_SEPARATION_SIGN + hagg + CSV_SEPARATION_SIGN
				+ maintainability + CSV_SEPARATION_SIGN + understandability + CSV_SEPARATION_SIGN + complexity
				+ CSV_SEPARATION_SIGN + metamodelReuse + CSV_SEPARATION_SIGN + relaxationIndexOfMetamodel
				+ CSV_SEPARATION_SIGN;
		return csvString.replace("null", "");
	}

	public Integer getNumberOfMetaclasses() {
		return numberOfMetaclasses;
	}

	public void setNumberOfMetaclasses(Integer numberOfMetaclasses) {
		this.numberOfMetaclasses = numberOfMetaclasses;
	}

	public Integer getNumberOfReferences() {
		return numberOfReferences;
	}

	public void setNumberOfReferences(Integer numberOfReferences) {
		this.numberOfReferences = numberOfReferences;
	}

	public Integer getNumberOfAttributes() {
		return numberOfAttributes;
	}

	public void setNumberOfAttributes(Integer numberOfAttributes) {
		this.numberOfAttributes = numberOfAttributes;
	}

	public Integer getDit() {
		return dit;
	}

	public void setDit(Integer dit) {
		this.dit = dit;
	}

	public Integer getHagg() {
		return hagg;
	}

	public void setHagg(Integer hagg) {
		this.hagg = hagg;
	}

	public Double getMaintainability() {
		return maintainability;
	}

	public void setMaintainability(Double maintainability) {
		this.maintainability = maintainability;
	}

	public Double getUnderstandability() {
		return understandability;
	}

	public void setUnderstandability(Double understandability) {
		this.understandability = understandability;
	}

	public Double getComplexity() {
		return complexity;
	}

	public void setComplexity(Double complexity) {
		this.complexity = complexity;
	}

	public Double getMetamodelReuse() {
		return metamodelReuse;
	}

	public void setMetamodelReuse(Double metamodelReuse) {
		this.metamodelReuse = metamodelReuse;
	}

	public Double getRelaxationIndexOfMetamodel() {
		return relaxationIndexOfMetamodel;
	}

	public void setRelaxationIndexOfMetamodel(Double relaxationIndexOfMetamodel) {
		this.relaxationIndexOfMetamodel = relaxationIndexOfMetamodel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUnidirectionalRef() {
		return unidirectionalRef;
	}

	public void setUnidirectionalRef(Integer unidirectionalRef) {
		this.unidirectionalRef = unidirectionalRef;
	}

	public Integer getOppositeRef() {
		return oppositeRef;
	}

	public void setOppositeRef(Integer oppositeRef) {
		this.oppositeRef = oppositeRef;
	}

}
