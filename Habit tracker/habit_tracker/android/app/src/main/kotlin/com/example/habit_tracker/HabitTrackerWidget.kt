package com.example.habit_tracker // CHANGE THIS ACC TO YOUR PROJECT NAME

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import es.antonborri.home_widget.HomeWidgetPlugin

// -----------------------------------------------------------
// IMPORTANT: Verify your package name from AndroidManifest.xml
// If your manifest says "com.example.habit_tracker", keep this.
// If it implies "com.example", remove ".habit_tracker"
import com.example.habit_tracker.R 
// -----------------------------------------------------------

class HabitTrackerWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val widgetData = HomeWidgetPlugin.getData(context)
            
            // 1. Get Views (using your correct XML layout)
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // 2. Retrieve Data sent from Flutter
            // We look for "title" (String) and "filename" (for the image)
            val title = widgetData.getString("title", "Overview")
            val imagePath = widgetData.getString("filename", null)

            // 3. Update the Title (Using the ID "view_title" from your XML)
            views.setTextViewText(R.id.view_title, title)

            // 4. Update the Image (Using the ID "widget_image_snapshot" from your XML)
            if (imagePath != null) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                views.setImageViewBitmap(R.id.widget_image_snapshot, bitmap)
                views.setViewVisibility(R.id.widget_image_snapshot, View.VISIBLE)
            } else {
                // If no image is sent yet, we can hide it or leave it empty
                // views.setViewVisibility(R.id.widget_image_snapshot, View.GONE)
            }

            // 5. Apply changes
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}