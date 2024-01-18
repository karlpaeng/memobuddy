package mad.memobuddy.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "memo.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (" +
                "note_id TEXT PRIMARY KEY, " +
                "note_title TEXT, " +
                "note_content TEXT, " +
                "note_color INTEGER, " +
                "note_created TEXT, " +
                "note_modified TEXT, " +
                "widget_id INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public String getLastPrimKey(){
        String str = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT note_id FROM notes ORDER BY note_id DESC LIMIT 1;", null);
        if(cursor.moveToFirst()) {
            str = cursor.getString(0);
        }else{ str = "0"; }
        cursor.close();
        db.close();
        return str;
    }
    public void newNote(NoteModel note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("note_id", note.noteId);
        cv.put("note_title", note.noteTitle);
        cv.put("note_content", note.noteContent);
        cv.put("note_created", note.dateCreated);
        cv.put("note_modified", note.dateModified);
        cv.put("note_color", note.noteColor);
        long i = db.insert("notes", null, cv);

        db.close();

    }
    public NoteModel getNoteById(int id){

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notes WHERE note_id = '" + id + "';", null);

        cursor.moveToFirst();
        NoteModel note = new NoteModel(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getInt(3)

        );

        db.close();
        cursor.close();

        return note;
    }
    public Boolean doesNoteExist(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notes WHERE note_id = '" + id + "';", null);
        boolean retBool = cursor.moveToFirst();

        db.close();
        cursor.close();
        return retBool;
    }
    public NoteModel getNoteByWidgetId(int widgetId){

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notes WHERE widget_id = " + widgetId + ";", null);

        cursor.moveToFirst();
        NoteModel note = new NoteModel(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getInt(3)

        );

        db.close();
        cursor.close();

        return note;
    }
    public void setWidgetId(int noteId, int widgetId){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE notes SET widget_id = " + widgetId + " WHERE note_id = '" + noteId + "';");
        db.close();

    }
    public boolean doesWidgetExist(int widgetId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notes WHERE widget_id = '" + widgetId + "';", null);
        boolean retBool = cursor.moveToFirst();

        db.close();
        cursor.close();
        return retBool;
    }
    public int getWidgetId(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT widget_id FROM notes WHERE note_id = '" + id + "';", null);
        int retInt;
        if(cursor.moveToFirst()){
            retInt = cursor.getInt(0);

        }else{
            retInt = -1;
        }

        db.close();
        cursor.close();
        return retInt;
    }
    public ArrayList<NoteModel> getAllNotes(){
        ArrayList<NoteModel> notes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notes ORDER BY note_modified DESC;", null);

        if (cursor.moveToFirst()){
            do{
                notes.add(new NoteModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(3)

                ));
            }while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return notes;
    }
    public int getSelectedColorForId(int id){
        int retInt = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT note_color FROM notes WHERE note_id = '" + id + "';", null);

        cursor.moveToFirst();
        retInt = cursor.getInt(0);

        db.close();
        cursor.close();

        return retInt;
    }
    public void updateNote(NoteModel note){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE notes SET " +

                "note_title = '"+ note.noteTitle +"', " +
                "note_content = '"+ note.noteContent +"', " +
                "note_color = "+ note.noteColor + ", " +
                "note_modified = '"+ note.dateModified + "' " +
                "WHERE note_id = '"+ note.noteId +"'; ");
        db.close();
    }
    public void deleteNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM notes WHERE note_id = '" + (id) + "';");
        db.close();
    }
}
