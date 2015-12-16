package com.example.flyweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FlyWeatherOpenHelper extends SQLiteOpenHelper{
	
	/*
	 * Province��
	 */
	public static final String CREATE_PROVINCE="create table Province(" +
			"id integer primary key autoincrement," +
			"province_name text," +
			"province_code text)";
	/*
	 * City��
	 */
	public static final String CREATE_CITY="create table City(" +
			"id integer primary key autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer)";
	/*
	 * Country��
	 */
	public static final String CREATE_COUNTRY="create table County(" +
			"id integer primary key autoincrement," +
			"county_name text," +
			"county_code text," +
			"city_id integer)";

	public FlyWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);//����Province��
		db.execSQL(CREATE_CITY);//����City��
		db.execSQL(CREATE_COUNTRY);//����Country��
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int olVersion, int newVersion) {
		
	}

}
