package mad.memobuddy.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mad.memobuddy.notes.databinding.ActivityHomeBinding;
import mad.memobuddy.notes.databinding.ActivityWidgetCfgBinding;

public class WidgetCfg extends AppCompatActivity {
    ActivityWidgetCfgBinding binding;
    TextView tvEmptyIndicator;
    GridAdapter gridAdapter;
    DBHelper dbh = new DBHelper(WidgetCfg.this);
    ArrayList<NoteModel> list;



    public static final String SHARED_PREFS = "prefs";
    public static final String KEY_BUTTON_TEXT = "memoBudKeyButtonText";

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWidgetCfgBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = dbh.getAllNotes();

        //
        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        //

        tvEmptyIndicator = findViewById(R.id.tvWidgetEmptyIndicator);


        if (!list.isEmpty()){
            tvEmptyIndicator.setVisibility(View.INVISIBLE);
        }

        gridAdapter = new GridAdapter(WidgetCfg.this, list);
        binding.gridViewWidget.setAdapter(gridAdapter);
//        Toast.makeText(WidgetCfg.this, "list size:"+list.size(), Toast.LENGTH_SHORT).show();


        binding.gridViewWidget.setOnItemClickListener((parent, view, position, id) -> {
            //
            NoteModel n = list.get(position);
//            Toast.makeText(WidgetCfg.this, "note id is: " + list.get(position).noteId, Toast.LENGTH_SHORT).show();


//            boolean widgetExists = dbh.doesWidgetExist(appWidgetId);
//            if(widgetExists) {
//                n = dbh.getNoteByWidgetId(appWidgetId);
//
//            }else{
//                n = new NoteModel(
//                        "-1",
//                        "[This Note has already been deleted]",
//                        "[This Note has already been deleted]",
//                        "-",
//                        "-",
//                        0
//                );
//            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

            Intent intent = new Intent(this, NoteViewer.class);
            intent.putExtra("to", "update");
            intent.putExtra("fromWidget", "true");
            intent.putExtra("id", n.noteId);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE);

            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.memo_buddy_widget);

//            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
//            views.setOnClickPendingIntent(R.id.widget_content, pendingIntent);

            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            views.setCharSequence(
                    R.id.widget_title,
                    "setText",
                    ReadFromFile("MBNoteTitle"+paddedZeros(Integer.parseInt(n.noteId)), getApplicationContext()));
            views.setCharSequence(
                    R.id.widget_content,
                    "setText",
                    ReadFromFile("MBNoteContent"+paddedZeros(Integer.parseInt(n.noteId)), getApplicationContext()));
            views.setInt(R.id.widget, "setBackgroundResource", returnColorInt(n.noteColor));


            appWidgetManager.updateAppWidget(appWidgetId, views);

            SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_BUTTON_TEXT + appWidgetId, n.noteId+"");
            editor.apply();

            Intent resValue = new Intent();
            resValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resValue);
//            Toast.makeText(WidgetCfg.this, "widgetId:"+appWidgetId, Toast.LENGTH_SHORT).show();
            dbh.setWidgetId(Integer.parseInt(n.noteId), appWidgetId);
            finish();

        });


    }
    private String paddedZeros(int id){
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
    private String ReadFromFile(String fileName, Context context){
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
    private int returnColorInt(int x){

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
}