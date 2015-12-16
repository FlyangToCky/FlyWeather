package com.example.flyweather.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

import com.example.flyweather.R;
import com.example.flyweather.db.FlyWeatherDb;
import com.example.flyweather.model.City;
import com.example.flyweather.model.Country;
import com.example.flyweather.model.Province;
import com.example.flyweather.util.Utility;

import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.utils.HttpUtil;
import com.litesuits.http.response.Response;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	
	private ArrayAdapter<String> adapter;
	private FlyWeatherDb flyWeatherDb;
	private List<String> dataList = new ArrayList<String>();
	
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 市列表
	 */
	private List<City> cityList;
	/**
	 * 县列表
	 */
	private List<Country> countyList;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	/**
	 * 是否从WeatherActivity中跳转过来。
	 */
	private boolean isFromWeatherActivity;
	
	/*
	 * 用于初始化LiteHttp
	 */
	protected static LiteHttp liteHttp;
	protected Activity activity = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		activity=this;
		initLiteHttp();
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//已经选择了城市而且不是从WeatherActivity跳转过来，才会直接跳转到WeatherActivity
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		
		listView = (ListView) findViewById(R.id.list_view);//城市选择的ListView
		titleText = (TextView) findViewById(R.id.title_text);//首页Title
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);//设置Adapter
		flyWeatherDb = FlyWeatherDb.getInstance(this);//获得数据库实例
		//处理选择城市ListView的每一行点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {//当前选中的级别是省
					selectedProvince = provinceList.get(index);//获得选中的省份
					queryCities();//根据选中的省份Id（selectedProvince），查询省下面的城市
				} else if (currentLevel == LEVEL_CITY) {//同省份逻辑
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {//出现城市之下的乡镇列表后
					String countyCode = countyList.get(index).getCountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);//intent带上乡镇的编号到WeatherActivity页面
					startActivity(intent);//开始跳转
					finish();
				}
			}
		});
		queryProvinces();  // 加载省级数据
	}
	
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryProvinces() {
		provinceList = flyWeatherDb.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCities() {
		cityList = flyWeatherDb.loadCities(selectedProvince.getId());//从数据库根据选中的省Id读取某省下所有的城市信息
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			//notifyDataSetChanged()可以在修改适配器绑定的数组后，
			//不用重新刷新Activity，通知Activity更新ListView
			adapter.notifyDataSetChanged();//到这里ListViwe刷新为城市的数组
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			//从服务器上地区数据
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCounties() {
		countyList = flyWeatherDb.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (Country county : countyList) {
				dataList.add(county.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据。
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		final StringRequest request = new StringRequest(address).setHttpListener(
                new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                    	/*HttpUtil.showTips(activity, "LiteHttp2.0", s);
                        response.printInfo();*/
                    	boolean result = false;
                    	if ("province".equals(type)) {
        					result = Utility.handleProvincesResponse(flyWeatherDb,
        							s);
        				} else if ("city".equals(type)) {
        					result = Utility.handleCitiesResponse(flyWeatherDb,
        							s, selectedProvince.getId());
        				} else if ("county".equals(type)) {
        					result = Utility.handleCountiesResponse(flyWeatherDb,
        							s, selectedCity.getId());
        				}
                    	if (result) {
        					// 通过runOnUiThread()方法回到主线程处理逻辑
        					runOnUiThread(new Runnable() {
        						@Override
        						public void run() {
        							closeProgressDialog();
        							if ("province".equals(type)) {
        								queryProvinces();
        							} else if ("city".equals(type)) {
        								queryCities();
        							} else if ("county".equals(type)) {
        								queryCounties();
        							}
        						}
        					});
        				}
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                }
        );
        // 1.1 execute async, nothing returned.
        liteHttp.executeAsync(request);
        // 1.2 perform async, future task returned.
        FutureTask<String> task = liteHttp.performAsync(request);
        task.cancel(true);
        
		/*HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(flyWeatherDb,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(flyWeatherDb,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(flyWeatherDb,
							response, selectedCity.getId());
				}
				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
										"加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});*/
	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
	/**
     * 单例 keep an singleton instance of litehttp
     */
    private void initLiteHttp() {
        if (liteHttp == null) {
            HttpConfig config = new HttpConfig(activity) // configuration quickly
                    .setDebugged(true)                   // log output when debugged
                    .setDetectNetwork(true)              // detect network before connect
                    .setDoStatistics(true)               // statistics of time and traffic
                    .setUserAgent("Mozilla/5.0 (...)")   // set custom User-Agent
                    .setTimeOut(10000, 10000);             // connect and socket timeout: 10s
            liteHttp = LiteHttp.newApacheHttpClient(config);
        } else {
            liteHttp.getConfig()                        // configuration directly
                    .setDebugged(true)                  // log output when debugged
                    .setDetectNetwork(true)             // detect network before connect
                    .setDoStatistics(true)              // statistics of time and traffic
                    .setUserAgent("Mozilla/5.0 (...)")  // set custom User-Agent
                    .setTimeOut(10000, 10000);            // connect and socket timeout: 10s
        }
    }
	
	
	
}
