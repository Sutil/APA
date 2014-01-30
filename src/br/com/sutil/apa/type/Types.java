package br.com.sutil.apa.type;

public enum Types {
	
	REAL("REAL"), 
	INTEGER("INTEGER"), 
	NUMERIC("NUMERIC"), 
	TEXT("TEXT");
	
	private String value;
	
	private Types(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

}
