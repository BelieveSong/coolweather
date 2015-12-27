package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

public class Utility {

	public synchronized static boolean handleProvinceResponse(CoolWeatherDB db,
			String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvince = response.split(",");
			if (allProvince != null && allProvince.length > 0) {
				for (String pro : allProvince) {
					String[] array = pro.split("\\|");
					Province bean = new Province();
					bean.setProvinceCode(array[0]);
					bean.setProvinceName(array[1]);
					db.saveProvince(bean);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean handleCityResponse(CoolWeatherDB db,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCity = response.split(",");
			if (allCity.length > 0 && allCity != null) {
				for (String city : allCity) {
					String[] array = city.split("\\|");
					City bean = new City();
					bean.setCityCode(array[0]);
					bean.setCityName(array[1]);
					bean.setProvinceId(provinceId);
					db.saveCity(bean);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean handleCountryResponse(CoolWeatherDB db,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String []allCountry=response.split(",");
			if(allCountry.length>0 && allCountry!=null){
				for(String country:allCountry){
					String []array=country.split(",");
					Country bean=new Country();
					bean.setCityId(cityId);
					bean.setCountryCode(array[0]);
					bean.setCountryName(array[1]);
					db.saveCountry(bean);
				}
				return true;
			}
		}
		return false;
	}
	
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat format=new SimpleDateFormat("yyyyƒÍM‘¬d»’");
		SharedPreferences.Editor edit=PreferenceManager.getDefaultSharedPreferences(context).edit();
		edit.putBoolean("city_selected", true);
		edit.putString("city_name", cityName);
		edit.putString("weather_code", weatherCode);
		edit.putString("temp1", temp1);
		edit.putString("temp2", temp2);
		edit.putString("weather_desp", weatherDesp);
		edit.putString("publish_time", publishTime);
		edit.putString("current_date", format.format(new Date()));
		edit.commit();
	}
}
