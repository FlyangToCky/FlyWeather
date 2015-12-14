package com.example.flyweather.util;

public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
