package mad.memobuddy.notes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Implementation of App Widget functionality.
 */
public class MemoBuddyWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        Log.d("memobudLOG", "updateAppWidget");
        //CharSequence widgetText = context.getString(R.string.appwidget_text);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.memo_buddy_widget);

        Intent intent = new Intent(context, NoteViewer.class);

        DBHelper dbh = new DBHelper(context);
        boolean widgetExists = dbh.doesWidgetExist(appWidgetId);
        NoteModel n;
//        Log.d("memobudLOGID", appWidgetId+"");
//        Log.d("memobudLOGEX", widgetExists+"");
        n = dbh.getNoteByWidgetId(appWidgetId);

//        n = new NoteModel(
//                "-1",
//                "[This Note has already been deleted]",
//                "[This Note has already been deleted]",
//                "-",
//                "-",
//                0
//        );
//        if(!widgetExists) {
//
//        }else {
//            n = dbh.getNoteByWidgetId(appWidgetId);
//        }
        intent.putExtra("id", n.noteId);

        views.setCharSequence(
                R.id.widget_title,
                "setText",
                ReadFromFile("MBNoteTitle"+paddedZeros(Integer.parseInt(n.noteId)), context));
        views.setCharSequence(
                R.id.widget_content,
                "setText",
                ReadFromFile("MBNoteContent"+paddedZeros(Integer.parseInt(n.noteId)), context));
        views.setInt(R.id.widget, "setBackgroundResource", returnColorInt(n.noteColor));


        intent.putExtra("to", "update");
        intent.putExtra("fromWidget", "true");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE);

        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        //Toast.makeText(context, "widgetupdated", Toast.LENGTH_SHORT).show();
//        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

//        Log.d("memobudLOG", "onUpdate");
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.memo_buddy_widget);
//
//            NoteModel n = dbh.getNoteByWidgetId(appWidgetId);
//            Intent intent = new Intent(context, NoteViewer.class);
//            intent.putExtra("to", "update");
//            intent.putExtra("id", n.noteId);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE);
//
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            views.setCharSequence(R.id.widget_title, "setText", n.noteTitle);
//            views.setCharSequence(R.id.widget_content, "setText", n.noteContent);
//            views.setInt(R.id.widget, "setBackgroundResource", returnColorInt(n.noteColor));

            DBHelper dbh = new DBHelper(context);
            boolean widgetExists = dbh.doesWidgetExist(appWidgetId);
            if (widgetExists){
                updateAppWidget(context, appWidgetManager, appWidgetId);

            }
        }
    }
    private static int returnColorInt(int x){

        if(x==0){
            return R.color.white;

        } else if (x==1) {
            return R.color.red;

        } else if (x==2) {
            return R.color.green;

        } else if (x==3) {
            return R.color.blue;

        } else if (x==4) {
            return R.color.yellow;

        }else return R.color.gray;

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
//        Log.d("memobudLOG", "enabled");


    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
//        Log.d("memobudLOG", "disabled");
    }
    private static String paddedZeros(int id){
        String s = ""+id;
        String a = "";
        if(s.length() >= 5){
            return s;
        }else {
            for (int q = 0 ; q < 5-s.length() ; q++){
                a = a + "0";
            }
            return a+s;
        }
    }
    private static String ReadFromFile(String fileName, Context context){
        String line,line1 = "";
        File filePath = new File(context.getExternalFilesDir(null) + "/" + fileName);
        try{
            if(filePath.exists()) filePath.createNewFile();
            else filePath = new File(context.getExternalFilesDir(null) + "/" + fileName);

            InputStream instream = new FileInputStream(filePath);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                try {
                    while ((line = buffreader.readLine()) != null)
                        line1= line1 + line + "\n";
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            instream.close();
            //Log.e("TAG", "Update to file: "+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return line1;
    }


}