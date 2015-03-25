package eu.mgolisch.exacteditions;

public class TitleComboItem {
	private String name;
	private String titleid;
	
	public TitleComboItem(String name,String titleid) {
		this.name = name;
		this.titleid = titleid;
	}
	
	public String getTitleId() {
		return titleid;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
	
	
}
