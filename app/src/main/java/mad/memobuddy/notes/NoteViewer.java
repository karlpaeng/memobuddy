package mad.memobuddy.notes;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NoteViewer extends AppCompatActivity {
    int selectedColor, noteID;
    View view;
    ConstraintLayout clColor;

    Boolean isNew, fromWidget, invalidWidget;
    String noteTitle, noteContent, noteContentFull;

    DBHelper dbh = new DBHelper(NoteViewer.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);

        view = findViewById(R.id.noteViewAct);


        //view.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

        EditText etTitle = findViewById(R.id.etTitle);
        EditText etContent = findViewById(R.id.etContent);

        clColor = findViewById(R.id.CLcolorSelector);

        TextView delete = findViewById(R.id.tvDelete);
        TextView cancel = findViewById(R.id.tvCancel);
        TextView save = findViewById(R.id.tvSave);
        TextView back = findViewById(R.id.tvBack);
        TextView dateCreated = findViewById(R.id.tvDateCreated);




        noteID = Integer.parseInt(getIntent().getStringExtra("id"));
//        Toast.makeText(NoteViewer.this, "note id:" + noteID, Toast.LENGTH_SHORT).show();


        //
        if(getIntent().getStringExtra("to").equals("new")){
            delete.setVisibility(View.INVISIBLE);
            setSelectedColor(0);
            dateCreated.setText("");
            isNew = true;
            fromWidget = false;
        }else if(getIntent().getStringExtra("to").equals("update")){
            if(dbh.doesNoteExist(noteID)){
                //WriteToFile("MBNoteTitle"+paddedZeros(noteID), noteTitle);
                etTitle.setText(ReadFromFile("MBNoteTitle"+paddedZeros(noteID)));
                etContent.setText(ReadFromFile("MBNoteContent"+paddedZeros(noteID)));
//            etTitle.setText("title for note # " + noteID);
//            etContent.setText("content for note # " + noteID);
                setSelectedColor(dbh.getSelectedColorForId(noteID));
                String dcstr = dbh.getNoteById(noteID).dateCreated;
                String outStr;

                outStr = "Created " +
                        dcstr.substring(0,4) + " " +
                        getMonthByNum(dcstr.substring(5,7)) + " " +
                        dcstr.substring(8,10) + ", ";
                int tempHr = Integer.parseInt(dcstr.substring(11,13));
                if(tempHr > 12){
                    tempHr = tempHr - 12;
                }
                if (tempHr == 0){
                    tempHr = 12;
                }
                outStr = outStr + tempHr + dcstr.substring(13,16) + " " + dcstr.substring(20);

                dateCreated.setText(outStr);
            }else{
                delete.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
                setSelectedColor(0);
                save.setText("Close");
                etTitle.setText("[This note does not exist]");
                etContent.setText("[This note does not exist]");
                setSelectedColor(0);
                dateCreated.setText("[This note does not exist]");
            }


            isNew = false;
            if(getIntent().getStringExtra("fromWidget") == null){

                fromWidget = false;
            }else{
                fromWidget = true;
//                if (getIntent().getStringExtra("fromWidget").equals("false")){
//                    delete.setVisibility(View.INVISIBLE);
//                    cancel.setVisibility(View.INVISIBLE);
//                    setSelectedColor(0);
//                    save.setText("Close");
//                    invalidWidget = true;
//                }else invalidWidget = false;
            }
        }



        delete.setOnClickListener(v -> {
            diaDelAlert();
//            Toast.makeText(NoteViewer.this, "deleted forever", Toast.LENGTH_SHORT).show();

        });
        cancel.setOnClickListener(v -> {
            if(fromWidget){
                finishAffinity();
            }else{
                finish();
            }
        });
        back.setOnClickListener(v -> {
            if(fromWidget){
                finishAffinity();
            }else{
                finish();
            }
        });
        save.setOnClickListener(v -> {
            //
            noteTitle = etTitle.getText().toString();
            noteContent = (etContent.getText().toString().length() > 30 ? etContent.getText().toString().substring(0,30)+"..." : etContent.getText().toString() );
            noteContentFull = etContent.getText().toString();
            NoteModel note;
            if(isNew){
                //
                noteID ++;
                note = new NoteModel(
                        noteID+"",
                        "",
                        "",
                        getCurrDateTime(),
                        getCurrDateTime(),
                        selectedColor
                );
                dbh.newNote(note);
                WriteToFile("MBNoteTitle"+paddedZeros(noteID), noteTitle);
                WriteToFile("MBNoteContent"+paddedZeros(noteID), noteContentFull);
            }else{
                //
                if(dbh.doesNoteExist(noteID)){
//                    finishAffinity();

                    note = new NoteModel(
                            noteID+"",
                            "",
                            "",
                            "",
                            getCurrDateTime(),
                            selectedColor
                    );
                    dbh.updateNote(note);
                    //update widgets
                    Intent intent = new Intent(this, MemoBuddyWidget.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

                    int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MemoBuddyWidget.class));

                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    sendBroadcast(intent);

                    WriteToFile("MBNoteTitle"+paddedZeros(noteID), noteTitle);
                    WriteToFile("MBNoteContent"+paddedZeros(noteID), noteContentFull);
                }


            }

//            WriteToFile("MBNoteNo"+paddedZeros(noteID), etContent.getText().toString());

            Intent i = new Intent(NoteViewer.this, Home.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            if(fromWidget){
                finishAffinity();
            }
            else {
                startActivity(i);
            }
            //finish();


        });

        clColor.setOnClickListener(v -> {
            diaColorSelect();
        });


    }
    private void diaDelAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteViewer.this);
        View v = getLayoutInflater().inflate(R.layout.dia_del_alert, null);
        TextView diaTitle = v.findViewById(R.id.diaTitle);
        TextView diaMsg = v.findViewById(R.id.diaMsg);
        TextView diaDelete = v.findViewById(R.id.diaDelete);
        TextView diaCancel = v.findViewById(R.id.diaCancel);

        builder.setView(v);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.getWindow().setGravity(Gravity.CENTER);
        //alertDialog.setCancelable(false);
        alertDialog.show();
        diaTitle.setText("Delete this Note?");
        diaMsg.setText("Are you sure you want to delete this Note forever?");
        diaDelete.setOnClickListener(v1 -> {
            //Toast.makeText(NoteViewer.this, "deleted forever", Toast.LENGTH_SHORT).show();
            //delete widget?
            int appWidgetId = dbh.getWidgetId(noteID);
            AppWidgetHost host = new AppWidgetHost(this, 0);
            if(appWidgetId > -1){
                host.deleteAppWidgetId(appWidgetId);
            }
            //
            dbh.deleteNote(noteID);
            DeleteFile("MBNoteNo"+paddedZeros(noteID));
            Intent i = new Intent(NoteViewer.this, Home.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            alertDialog.dismiss();
            if(fromWidget){
                finishAffinity();
            }
            else {
                startActivity(i);
            }
        });
        diaCancel.setOnClickListener(v1 -> {
            alertDialog.dismiss();
        });
    }
    private void diaColorSelect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteViewer.this);
        View v = getLayoutInflater().inflate(R.layout.dia_color_select, null);
        ConstraintLayout clgr, clr, clg, clb, cly;
        clgr = v.findViewById(R.id.CLgrayColor);
        clr = v.findViewById(R.id.CLredColor);
        clg = v.findViewById(R.id.CLgreenColor);
        clb = v.findViewById(R.id.CLblueColor);
        cly = v.findViewById(R.id.CLyellowColor);

        builder.setView(v);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.getWindow().setGravity(Gravity.TOP);
        alertDialog.show();

        clgr.setOnClickListener(v1 -> {

            setSelectedColor(0);
            alertDialog.dismiss();
        });
        clr.setOnClickListener(v1 -> {

            setSelectedColor(1);
            alertDialog.dismiss();
        });
        clg.setOnClickListener(v1 -> {

            setSelectedColor(2);
            alertDialog.dismiss();

        });
        clb.setOnClickListener(v1 -> {

            setSelectedColor(3);
            alertDialog.dismiss();

        });
        cly.setOnClickListener(v1 -> {

            setSelectedColor(4);
            alertDialog.dismiss();

        });


    }
    private void setSelectedColor(int c){
        switch (c){
            case 0:
                selectedColor = c;
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                clColor.setBackground(this.getDrawable(R.drawable.shape_round_gray_s));
                getWindow().setStatusBarColor(ContextCompat.getColor(NoteViewer.this, R.color.gray));
                getWindow().setNavigationBarColor(ContextCompat.getColor(NoteViewer.this, R.color.gray));
                break;
            case 1:
                selectedColor = c;
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                clColor.setBackground(this.getDrawable(R.drawable.shape_round_r_s));
                getWindow().setStatusBarColor(ContextCompat.getColor(NoteViewer.this, R.color.red));
                getWindow().setNavigationBarColor(ContextCompat.getColor(NoteViewer.this, R.color.red));
                break;
            case 2:
                selectedColor = c;
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                clColor.setBackground(this.getDrawable(R.drawable.shape_round_g_s));
                getWindow().setStatusBarColor(ContextCompat.getColor(NoteViewer.this, R.color.green));
                getWindow().setNavigationBarColor(ContextCompat.getColor(NoteViewer.this, R.color.green));
                break;
            case 3:
                selectedColor = c;
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
                clColor.setBackground(this.getDrawable(R.drawable.shape_round_b_s));
                getWindow().setStatusBarColor(ContextCompat.getColor(NoteViewer.this, R.color.blue));
                getWindow().setNavigationBarColor(ContextCompat.getColor(NoteViewer.this, R.color.blue));
                break;
            case 4:
                selectedColor = c;
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                clColor.setBackground(this.getDrawable(R.drawable.shape_round_y_s));
                getWindow().setStatusBarColor(ContextCompat.getColor(NoteViewer.this, R.color.yellow));
                getWindow().setNavigationBarColor(ContextCompat.getColor(NoteViewer.this, R.color.yellow));
                break;
        }
    }
    private String ReadFromFile(String fileName){
        String line,line1 = "";
        File filePath = new File(NoteViewer.this.getExternalFilesDir(null) + "/" + fileName);
        try{
            if(filePath.exists()) filePath.createNewFile();
            else filePath = new File(NoteViewer.this.getExternalFilesDir(null) + "/" + fileName);

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
    private void WriteToFile(String fileName, String content){
        File filePath = new File(NoteViewer.this.getExternalFilesDir(null) + "/" + fileName);
        try{
            if(filePath.exists()) filePath.createNewFile();
            else filePath = new File(NoteViewer.this.getExternalFilesDir(null) + "/" + fileName);

            FileOutputStream writer = new FileOutputStream(filePath);
            writer.write(content.getBytes());
            writer.flush();
            writer.close();
            //Log.e("TAG", "Wrote to file: "+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void DeleteFile(String fileName){
        File filePath = new File(NoteViewer.this.getExternalFilesDir(null) + "/" + fileName);
        filePath.delete();
//        getFilesDir();
//        NoteViewer.this.deleteFile(fileName);
    }
    private String getCurrDateTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss-a");
        return simpleDate.format(calendar.getTime());
    }

    private String getMonthByNum(String numericMonth){
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"};
        return months[Integer.parseInt(numericMonth)-1];
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
//    public void hideKB(){
//        try {
//            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }catch (NullPointerException e){
//            e.printStackTrace();
//        }
//    }
}