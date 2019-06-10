package com.kevin.kafka.pojo;

public class IPInfo {
	private String ipAddress;
	private int ipCount;
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getIpCount() {
		return ipCount;
	}
	public void setIpCount(int ipCount) {
		this.ipCount = ipCount;
	}
	@Override
	public String toString() {
		return "IPInfo [ipAddress=" + ipAddress + ", ipCount=" + ipCount + "]";
	}

}
