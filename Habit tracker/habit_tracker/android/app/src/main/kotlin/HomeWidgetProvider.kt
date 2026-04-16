package com.example.habit_tracker // <--- VERIFY THIS MATCHES YOUR FOLDER STRUCTURE

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import es.antonborri.home_widget.HomeWidgetPlugin
import java.io.File // Import File checking

// Import your R class
import com.example.habit_tracker.R

class HomeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            try {
                // 1. Get Data
                val widgetData = HomeWidgetPlugin.getData(context)
                val views = RemoteViews(context.packageName, R.layout.widget_layout)

                // 2. Get Filename
                val imagePath = widgetData.getString("filename", null)

                // 3. Logic: Show Image OR Show Text
                if (imagePath != null && File(imagePath).exists()) {
                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    views.setImageViewBitmap(R.id.widget_image_snapshot, bitmap)
                    
                    // Show Image, Hide Text
                    views.setViewVisibility(R.id.widget_image_snapshot, View.VISIBLE)
                    views.setViewVisibility(R.id.view_empty_text, View.GONE)
                } else {
                    // Show Text, Hide Image
                    views.setViewVisibility(R.id.widget_image_snapshot, View.GONE)
                    views.setViewVisibility(R.id.view_empty_text, View.VISIBLE)
                }

                // 4. Update
                appWidgetManager.updateAppWidget(appWidgetId, views)
                
            } catch (e: Exception) {
                // If it crashes, do nothing. (Prevents the App Report popup)
                e.printStackTrace()
            }
        }
    }
}