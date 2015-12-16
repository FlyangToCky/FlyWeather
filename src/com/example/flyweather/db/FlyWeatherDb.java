package com.example.flyweather.db;

import java.util.ArrayList;
import java.util.List;

import com.example.flyweather.model.City;
import com.example.flyweather.model.Country;
import com.example.flyweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FlyWeatherDb {
	/*
	 * ���ݿ���
	 */
	public static final String DB_NAME="fly_weather";
	
	/*
	 * ���ݿ�汾
	 */
	public static final int VERSION=1;
	
	private static FlyWeatherDb flyWeatherDB;
	
	private SQLiteDatabase db;
	/*
	 * ���췽��˽�л�
	 */
	private FlyWeatherDb(Context context) {
		FlyWeatherOpenHelper dbHelper= 
				new FlyWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	/*
	 * ���FlyWeatherDb��ʵ��
	 */
	public synchronized static FlyWeatherDb getInstance(Context context){
		if(flyWeatherDB == null){
			flyWeatherDB = new FlyWeatherDb(context);
		}
		return flyWeatherDB;
	}
	
	/*
	 * ��Provinceʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values= new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/**
	 * �����ݿ��ȡȫ�����е�ʡ����Ϣ��
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	/**
	 * ��Cityʵ���洢�����ݿ⡣
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * �����ݿ��ȡĳʡ�����еĳ�����Ϣ��
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * ��Countyʵ���洢�����ݿ⡣
	 */
	public void saveCountry(Country county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("country_name", county.getCountryName());
			values.put("country_code", county.getCountryCode());
			values.put("city_id", county.getCityId());
			db.insert("Country", null, values);
		}
	}

	/**
	 * �����ݿ��ȡĳ���������е�����Ϣ��
	 */
	public List<Country> loadCounties(int cityId) {
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.query("Country", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Country county = new Country();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountryName(cursor.getString(cursor
						.getColumnIndex("country_name")));
				county.setCountryCode(cursor.getString(cursor
						.getColumnIndex("country_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
}
