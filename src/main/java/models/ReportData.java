package models;

public class ReportData {
	private int partnerId;
	private String partnerGuid;
	private int accountId;
	private String accountGuid;
	private String username;
	private String domains;
	private String itemname;
	private String plan;
	private int itemType;
	private String partNumber;
	private int itemCount;
	
	public int getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}
	public String getPartnerGuid() {
		return partnerGuid;
	}
	public void setPartnerGuid(String partnerGuid) {
		this.partnerGuid = partnerGuid;
	}
	public String getAccountGuid() {
		return accountGuid;
	}
	public void setAccountGuid(String accountGuid) {
		this.accountGuid = accountGuid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDomains() {
		return domains;
	}
	public void setDomains(String domains) {
		this.domains = domains;
	}
	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
}
