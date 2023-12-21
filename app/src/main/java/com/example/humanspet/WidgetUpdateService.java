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

            if ("com.example.UPDATE_WIDGET".equals(action)) {
                int speedValue = intent.getIntExtra("speedValue", 0);
                Log.d("위젯", "onHandleIntent: "+speedValue);
                updateWidget(speedValue);
            }
        }
    }
    private void updateWidget(int speedValue) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, Widget.class));

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);

            // 여기에서 원하는 레이아웃 업데이트 로직을 추가합니다.
            views.setTextViewText(R.id.widgetSpeed, speedValue + "km/h");

            // AppWidgetManager를 사용하여 위젯을 업데이트합니다.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
