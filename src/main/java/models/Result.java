package models;

import java.util.ArrayList;
import java.util.HashMap;

public class Result {
	private ArrayList<Chargeable> chargeList;
    private ArrayList<Domain> domains;
    private HashMap<String,Integer> stats;
	public ArrayList<Chargeable> getChargeList() {
		return chargeList;
	}
	public void setChargeList(ArrayList<Chargeable> chargeList) {
		this.chargeList = chargeList;
	}
	public ArrayList<Domain> getDomains() {
		return domains;
	}
	public void setDomains(ArrayList<Domain> domains) {
		this.domains = domains;
	}
	public HashMap<String,Integer> getStats() {
		return stats;
	}
	public void setStats(HashMap<String,Integer> stats) {
		this.stats = stats;
	}
}
