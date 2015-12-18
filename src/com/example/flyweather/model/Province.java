package com.example.flyweather.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
@Table("Province")
public class Province {
	@PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
	private int id;
	@Column("provinceName")
	private String provinceName;
	@Column("provinceCode")
	private String provinceCode;
	
	
	
	
	public Province() {
		super();
	}
	public Province(int id, String provinceName, String provinceCode) {
		super();
		this.id = id;
		this.provinceName = provinceName;
		this.provinceCode = provinceCode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	@Override
	public String toString() {
		return "Province [id=" + id + ", provinceName=" + provinceName
				+ ", provinceCode=" + provinceCode + "]";
	}
	
}
