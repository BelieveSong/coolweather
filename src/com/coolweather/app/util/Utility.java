package com.coolweather.app.util;

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
}
