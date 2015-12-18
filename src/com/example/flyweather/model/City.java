package com.example.flyweather.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
@Table("City")
public class City{
	public static final String COL_PROVINCEID="_provinceId";
	// 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
	private int id;
    @Column("cityName")
	private String cityName;
    @Column("cityCode")
	private String cityCode;
    @Column(COL_PROVINCEID)
	private int provinceId;
    
    
	public City() {
		super();
	}
	public City(int id, String cityName, String cityCode, int provinceId) {
		super();
		this.id = id;
		this.cityName = cityName;
		this.cityCode = cityCode;
		this.provinceId = provinceId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public int getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	@Override
	public String toString() {
		return "City [id=" + id + ", cityName=" + cityName + ", cityCode="
				+ cityCode + ", provinceId=" + provinceId + "]";
	}
	
}
