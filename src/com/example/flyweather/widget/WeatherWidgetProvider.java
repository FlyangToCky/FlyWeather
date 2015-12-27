package com.example.flyweather.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class WeatherWidgetProvider extends AppWidgetProvider{
	// onUpdate() �ڸ��� widget ʱ����ִ�У�
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		Toast.makeText(context, "�����:onUpdate", Toast.LENGTH_SHORT).show();
	}
	
	 // �� widget ��������� ���� �� widget �Ĵ�С���ı�ʱ�������� 
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
		Toast.makeText(context, "����������μ���", Toast.LENGTH_SHORT).show();
	}
	
	// widget��ɾ��ʱ����
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}
	
	// ��һ��widget������ʱ����
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Toast.makeText(context, "�������������", Toast.LENGTH_SHORT).show();
	}
	
	 // ���һ��widget��ɾ��ʱ����  
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}
	
	 // ���չ㲥�Ļص�����
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}
}
