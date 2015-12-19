package com.coolweather.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {

	private TextView tvTitle;
	private ListView listView;
	private Dialog processDialog;
	private ArrayAdapter<String> adapter;
	
	private CoolWeatherDB db;
	
	private static final int LEVEL_PROVINCE=0;
	private static final int LEVEL_CITY=1;
	private static final int LEVEL_COUNTY=2;
	
	private List<String> dataList=new ArrayList<String>();
	
	private List<Province> provinceList=new ArrayList<Province>();
	private List<City> cityList=new ArrayList<City>();
	private List<Country> countryList=new  ArrayList<Country>();
	
	private Province selectedProvince;
	private City selectedCity;
	private Country selectedCountry;
	
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		initView();
		initData();
		setListener();
		queryAllProvince();
	}

	private void queryAllProvince() {
		provinceList=db.getProvinces();
		if(provinceList!=null && provinceList.size()>0){
			dataList.clear();
			for(Province item:provinceList){
				dataList.add(item.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			tvTitle.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}

	private void queryFromServer(final String code,final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http:www.weather.com.cn/data/list3/city.xml";
		}
		showProcessDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvinceResponse(db, response);
				}else if("city".equals(type)){
					result=Utility.handleCityResponse(db, response, selectedProvince.getId());
				}else{
					result=Utility.handleCountryResponse(db, response, selectedCity.getId());
				}
				if(result){
					if("province".equals(type)){
						queryAllProvince();
					}else if("city".equals(type)){
						queryAllCity();
					}else if("country".equals(type)){
						queryAllCountry();
					}
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						closeProcessDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
		});
	}

	protected void closeProcessDialog() {
		if(processDialog!=null){
			processDialog.dismiss();
		}
	}

	private void showProcessDialog() {
		if(processDialog==null){
			processDialog=new Dialog(ChooseAreaActivity.this);
			processDialog.setTitle("正在加载...");
			processDialog.setCanceledOnTouchOutside(false);
		}
		processDialog.show();
	}

	private void setListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel==LEVEL_PROVINCE){
					selectedProvince=provinceList.get(position);
					queryAllCity();
				}else if(currentLevel==LEVEL_CITY){
					selectedCity=cityList.get(position);
					queryAllCountry();
				}
			}
		});
	}

	protected void queryAllCountry() {
		countryList=db.getCountries(selectedCity.getId());
		if(countryList!=null && countryList.size()>0){
			for(Country item:countryList){
				dataList.add(item.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			tvTitle.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}
	}

	protected void queryAllCity() {
		cityList=db.getCities(selectedProvince.getId());
		if(cityList!=null && cityList.size()>0){
			for(City item:cityList){
				dataList.add(item.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			tvTitle.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}
	}

	private void initData() {
		db=CoolWeatherDB.getInstance(ChooseAreaActivity.this);
		adapter=new ArrayAdapter<String>(ChooseAreaActivity.this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
	}

	private void initView() {
		listView=(ListView) findViewById(R.id.list_view);
		tvTitle=(TextView) findViewById(R.id.title_text);
	}
	
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_CITY){
			queryAllProvince();
		}else if(currentLevel==LEVEL_COUNTY){
			queryAllCity();
		}else{
			finish();
		}
	}

}
