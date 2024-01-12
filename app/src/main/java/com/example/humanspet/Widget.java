package com.example.humanspet;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {
    static String TAG = "위젯";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.widgetSpeed, "0km/h");

        Log.d(TAG, "updateAppWidget: 들어옴");
        // 브로드캐스트를 트리거하는 PendingIntent를 설정합니다.
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction("com.example.UPDATE_WIDGET");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setOnClickPendingIntent(R.id.widgetDistance, PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
        // AppWidgetManager를 사용하여 위젯을 업데이트합니다.
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }


}