package com.example.humanspet;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

public class WidgetUpdateService extends IntentService {

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            Log.d("위젯", "onHandleIntent: Received Intent: " + intent.toString());

            String action = intent.getAction();
            Log.d("위젯", "onHandleIntent: Action received: " + action);

            if ("com.example.Widget".equals(action)) {
                double distanceValue = intent.getDoubleExtra("distanceValue", 0.0);
                double calorieValue = intent.getDoubleExtra("calorieValue",0.0);
                String timeValue = intent.getStringExtra("timeValue");
                int speedValue = intent.getIntExtra("speedValue",0);
                Log.d("위젯", "onHandleIntent: " + speedValue);
                updateWidget(speedValue,distanceValue,calorieValue,timeValue);
            }
        }
    }


    private void updateWidget(int speedValue,double distanceValue,double calorieValue, String timeValue) {
        Log.d("위젯", "updateWidget: Updating widget with speedValue: " + speedValue);

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.widgetDistance, String.format("%.0f",distanceValue)+"m");
        views.setTextViewText(R.id.widgetCalories,String.format("%.0f",calorieValue)+"kcal");
        views.setTextViewText(R.id.widgetTime,timeValue);
        views.setTextViewText(R.id.widgetSpeed,speedValue+"km/h");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(this, Widget.class);
        appWidgetManager.updateAppWidget(componentName, views);

    }
}
