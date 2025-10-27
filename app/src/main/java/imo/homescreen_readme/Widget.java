package imo.homescreen_readme;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {
    public static final String ACTION_WIDGET_UPDATE = "ACTION_WIDGET_UPDATE";

    static void updateWidget(Context context, AppWidgetManager manager, int appWidgetId) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        SharedPreferences sp = context.getSharedPreferences("hehe", Context.MODE_PRIVATE);
        String savedString = sp.getString(MainActivity.WIDGET_STRING_KEY, "");
        
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
		int width = 500;
		int height = 500;
		Bitmap bmp = MarkdownRender.renderMarkdownToBitmap(context, savedString, width, height);
		views.setImageViewBitmap(R.id.widget_image, bmp);
		
        manager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) updateWidget(context, manager, appWidgetId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (! ACTION_WIDGET_UPDATE.equals(intent.getAction())) return;
            
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, Widget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        onUpdate(context, appWidgetManager, allWidgetIds);

        super.onReceive(context, intent);
    }
}
