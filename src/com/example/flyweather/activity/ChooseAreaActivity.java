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
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/**
	 * ���б�
	 */
	private List<City> cityList;
	/**
	 * ���б�
	 */
	private List<Country> countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	/**
	 * �Ƿ��WeatherActivity����ת������
	 */
	private boolean isFromWeatherActivity;
	
	/*
	 * ���ڳ�ʼ��LiteHttp
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
		//�Ѿ�ѡ���˳��ж��Ҳ��Ǵ�WeatherActivity��ת�������Ż�ֱ����ת��WeatherActivity
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		
		listView = (ListView) findViewById(R.id.list_view);//����ѡ���ListView
		titleText = (TextView) findViewById(R.id.title_text);//��ҳTitle
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);//����Adapter
		flyWeatherDb = FlyWeatherDb.getInstance(this);//������ݿ�ʵ��
		//����ѡ�����ListView��ÿһ�е���¼�
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {//��ǰѡ�еļ�����ʡ
					selectedProvince = provinceList.get(index);//���ѡ�е�ʡ��
					queryCities();//����ѡ�е�ʡ��Id��selectedProvince������ѯʡ����ĳ���
				} else if (currentLevel == LEVEL_CITY) {//ͬʡ���߼�
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {//���ֳ���֮�µ������б��
					String countyCode = countyList.get(index).getCountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);//intent��������ı�ŵ�WeatherActivityҳ��
					startActivity(intent);//��ʼ��ת
					finish();
				}
			}
		});
		queryProvinces();  // ����ʡ������
	}
	
	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
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
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * ��ѯѡ��ʡ�����е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
	 */
	private void queryCities() {
		cityList = flyWeatherDb.loadCities(selectedProvince.getId());//�����ݿ����ѡ�е�ʡId��ȡĳʡ�����еĳ�����Ϣ
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			//notifyDataSetChanged()�������޸��������󶨵������
			//��������ˢ��Activity��֪ͨActivity����ListView
			adapter.notifyDataSetChanged();//������ListViweˢ��Ϊ���е�����
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			//�ӷ������ϵ�������
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ�������ݡ�
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
        					// ͨ��runOnUiThread()�����ص����̴߳����߼�
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
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
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
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
										"����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});*/
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳���
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
     * ���� keep an singleton instance of litehttp
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
