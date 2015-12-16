package com.example.flyweather.activity;


import java.util.concurrent.FutureTask;

import com.example.flyweather.R;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;

public class Test extends Activity implements OnClickListener{
	protected static LiteHttp liteHttp;
    protected Activity activity = null;
    public static final String url = "http://www.weather.com.cn/data/list3/city.xml";
    private Button btn_liteHttp;
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.test_layout);
		activity = this;
		initLiteHttp();
		initViews();
		
	}
	private void initViews(){
		btn_liteHttp=(Button) findViewById(R.id.LiteHttp);
		btn_liteHttp.setOnClickListener(this);
	}
	
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.LiteHttp:
			// 1. Asynchronous Request

            // 1.0 init request
            final StringRequest request = new StringRequest(url).setHttpListener(
                    new HttpListener<String>() {
                        @Override
                        public void onSuccess(String s, Response<String> response) {
                            HttpUtil.showTips(activity, "LiteHttp2.0", s);
                            response.printInfo();
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
			break;

		default:
			break;
		}
		
	}
}
