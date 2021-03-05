package models;

public class Domain {
	private int id;
	private String partnerPurchasedPlanID;
	private String domain;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPartnerPurchasedPlanID() {
		return partnerPurchasedPlanID;
	}
	public void setPartnerPurchasedPlanID(String partnerPurchasedPlanID) {
		this.partnerPurchasedPlanID = partnerPurchasedPlanID;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
}
