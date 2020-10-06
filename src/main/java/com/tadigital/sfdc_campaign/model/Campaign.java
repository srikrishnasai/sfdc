package com.tadigital.sfdc_campaign.model;

import com.google.gson.annotations.SerializedName;

public class Campaign {
	@SerializedName("Name")
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
