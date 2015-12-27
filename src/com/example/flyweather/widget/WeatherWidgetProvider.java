package com.example.flyweather.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class WeatherWidgetProvider extends AppWidgetProvider{
	// onUpdate() 在更新 widget 时，被执行，
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		Toast.makeText(context, "咩哈哈:onUpdate", Toast.LENGTH_SHORT).show();
	}
	
	 // 当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用 
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
		Toast.makeText(context, "咩哈哈，初次见面", Toast.LENGTH_SHORT).show();
	}
	
	// widget被删除时调用
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}
	
	// 第一个widget被创建时调用
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Toast.makeText(context, "咩哈哈，创建了", Toast.LENGTH_SHORT).show();
	}
	
	 // 最后一个widget被删除时调用  
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}
	
	 // 接收广播的回调函数
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}
}
