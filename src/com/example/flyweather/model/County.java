package com.example.flyweather.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
@Table("County")
public class County{
	public static final String COL_CITYID="_cityId";
	@PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
	private int id;
	@Column("countyName")
	private String countyName;
	@Column("countyCode")
	private String countyCode;
	@Column(COL_CITYID)
	private int cityId;
	
	
	
	public County() {
		super();
	}
	public County(int id, String countyName, String countyCode, int cityId) {
		super();
		this.id = id;
		this.countyName = countyName;
		this.countyCode = countyCode;
		this.cityId = cityId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCountyName() {
		return countyName;
	}
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	public String getCountyCode() {
		return countyCode;
	}
	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	@Override
	public String toString() {
		return "County [id=" + id + ", countyName=" + countyName
				+ ", countyCode=" + countyCode + ", cityId=" + cityId + "]";
	}
	
	
}
