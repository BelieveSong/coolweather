package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

public class CoolWeatherDB {

	private static final String DB_NAME = "cool_weather";
	private static final Integer version = 1;

	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper openHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, version);
		db = openHelper.getWritableDatabase();
	}

	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			return new CoolWeatherDB(context);
		} else {
			return coolWeatherDB;
		}
	}

	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("province", null, values);
		}
	}

	public List<Province> getProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("province", null, null, null, null, null, null);
		if (cursor.moveToNext()) {
			Province province = new Province();
			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
			province.setProvinceCode(cursor.getString(cursor
					.getColumnIndex("province_code")));
			province.setProvinceName(cursor.getString(cursor
					.getColumnIndex("province_name")));
			list.add(province);
		}
		return list;
	}

	public void saveCity(City city) {
		ContentValues values = new ContentValues();
		values.put("city_code", city.getCityCode());
		values.put("city_name", city.getCityName());
		values.put("province_Id", city.getProvinceId());
		db.insert("city", null, values);
	}

	public List<City> getCities(Integer id) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("city", null, "province_id=?", new String[]{String.valueOf(id)}, null, null, null);
		if (cursor.moveToNext()) {
			City city = new City();
			city.setCityCode(cursor.getString(cursor
					.getColumnIndex("city_code")));
			city.setCityName(cursor.getString(cursor
					.getColumnIndex("city_name")));
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setProvinceId(cursor.getInt(cursor
					.getColumnIndex("province_id")));
			list.add(city);
		}
		return list;
	}

	public void saveCountry(Country country) {
		ContentValues values = new ContentValues();
		values.put("country_name", country.getCountryName());
		values.put("country_code", country.getCountryCode());
		values.put("city_Id", country.getCityId());
		db.insert("country", null, values);
	}

	public List<Country> getCountries(Integer id) {
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.query("country", null, "city_id=?", new String[]{String.valueOf(id)}, null, null, null);
		if (cursor.moveToNext()) {
			Country country = new Country();
			country.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
			country.setCountryCode(cursor.getString(cursor
					.getColumnIndex("country_code")));
			country.setCountryName(cursor.getString(cursor
					.getColumnIndex("country_name")));
			country.setId(cursor.getInt(cursor.getColumnIndex("id")));
			list.add(country);
		}
		return list;
	}
}
