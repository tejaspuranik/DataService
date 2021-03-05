package enums;

public enum ExcelSection {

	Data("Data");//, //
	//PROFIT("Profit");

	final String typeValue;

	private ExcelSection(final String typeValue) {
		this.typeValue = typeValue;
	}

	public String getName() {
		return name();
	}

	public String getValue() {
		return typeValue;
	}

	@Override
	public String toString() {
		return name();
	}

}
