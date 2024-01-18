package mad.memobuddy.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<NoteModel> noteList;

    LayoutInflater inflater;

    public GridAdapter(Context context, ArrayList<NoteModel> list) {
        this.context = context;
        this.noteList = list;
    }
    public void removeItem(NoteModel note){
        noteList.remove(note);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = inflater.inflate(R.layout.grid_item, null);
        }

        LinearLayout linearLayout = convertView.findViewById(R.id.llGrid);
        switch (noteList.get(position).noteColor){
            case 0:
                linearLayout.setBackground(context.getDrawable(R.drawable.shape_round_gray_s));
                break;
            case 1:
                linearLayout.setBackground(context.getDrawable(R.drawable.shape_round_r_s));
                break;
            case 2:
                linearLayout.setBackground(context.getDrawable(R.drawable.shape_round_g_s));
                break;
            case 3:
                linearLayout.setBackground(context.getDrawable(R.drawable.shape_round_b_s));
                break;
            case 4:
                linearLayout.setBackground(context.getDrawable(R.drawable.shape_round_y_s));
                break;
        }
        //linearLayout.setBackground(context.getDrawable(R.drawable.shape_round_g_s));
        //
        TextView tvTitle = convertView.findViewById(R.id.note_title);
        TextView tvContent = convertView.findViewById(R.id.note_content);
        TextView tvDateTime = convertView.findViewById(R.id.note_datetime);
        String dateMod = noteList.get(position).dateModified;

        String outStr;

        outStr = "Created " +
                dateMod.substring(0,4) + " " +
                getMonthByNum(dateMod.substring(5,7)) + " " +
                dateMod.substring(8,10) + ", ";
        int tempHr = Integer.parseInt(dateMod.substring(11,13));
        if(tempHr > 12){
            tempHr = tempHr - 12;
        }
        if (tempHr == 0){
            tempHr = 12;
        }
        outStr = outStr + tempHr + dateMod.substring(13,16) + " " + dateMod.substring(20);
        tvDateTime.setText(outStr);

        String txtTitle = ReadFromFile("MBNoteTitle"+paddedZeros(Integer.parseInt(noteList.get(position).noteId)));
        String txtContent = ReadFromFile("MBNoteContent"+paddedZeros(Integer.parseInt(noteList.get(position).noteId)));
        if (txtTitle.equals("")){
            tvTitle.setText("No Title");
            tvTitle.setTextColor(ContextCompat.getColor(context, R.color.darkgray));
        }else{
            tvTitle.setText(txtTitle);
        }

        if (txtContent.equals("")){
            tvContent.setText("No Content");
            tvContent.setTextColor(ContextCompat.getColor(context, R.color.darkgray));
        }else {
            String contentShort = txtContent;
            if(txtContent.length() >= 30) {
                contentShort = txtContent.substring(0, 30) + "...";
            }
            tvContent.setText(contentShort);
        }
//        tvTitle.setText(noteList.get(position).noteTitle);
//        tvContent.setText(noteList.get(position).noteContent);
        return convertView;
    }
    private String ReadFromFile(String fileName){
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
}
