package com.tadigital.sfdc_campaign.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author saikrishna.sp
 *
 */
@Table(name = "sfdcacs.map_config")
@Entity
public class MapConfig {

	@Id
	@Column(name = "map_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int mapid;

	@Column(name = "user_id")
	int userid;

	@Column(name = "map_config_id")
	int mapconfigid;

	

	@Column(name = "mapped_pair")
	String mappedpair;

	@Column(name = "pair_belongs_to")
	String pairBelongsTo;	
	
	public String getPairBelongsTo() {
		return pairBelongsTo;
	}

	public void setPairBelongsTo(String pairBelongsTo) {
		this.pairBelongsTo = pairBelongsTo;
	}

	public int getMapconfigid() {
		return mapconfigid;
	}

	public void setMapconfigid(int mapconfigid) {
		this.mapconfigid = mapconfigid;
	}
	
	public int getMapid() {
		return mapid;
	}

	public void setMapid(int mapid) {
		this.mapid = mapid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getMappedpair() {
		return mappedpair;
	}

	public void setMappedpair(String mappedpair) {
		this.mappedpair = mappedpair;
	}

	@Override
	public String toString() {
		return "MapConfig [mapid=" + mapid + ", userid=" + userid + ", mapconfigid=" + mapconfigid + ", mappedpair="
				+ mappedpair + "]";
	}

}
