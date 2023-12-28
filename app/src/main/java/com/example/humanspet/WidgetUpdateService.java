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
            String action = intent.getAction();
//            Log.d("위젯", "onHandleIntent: Action received: " + action);

            if ("com.example.UPDATE_WIDGET".equals(action)) {
                int speedValue = intent.getIntExtra("speedValue", 0);
                Log.d("위젯", "onHandleIntent: " + speedValue);
                updateWidget(speedValue);
            }
        }
    }

    private void updateWidget(int speedValue) {
        Log.d("위젯", "updateWidget: Updating widget with speedValue: " + speedValue);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, Widget.class));

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
            views.setTextViewText(R.id.widgetSpeed, speedValue + "km/h");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
