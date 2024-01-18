package mad.memobuddy.notes;

public class NoteModel {
    public String noteId, noteTitle, noteContent, dateCreated, dateModified;
    int noteColor;

    public NoteModel(String noteId, String noteTitle, String noteContent, String dateCreated, String dateModified, int noteColor) {
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.noteColor = noteColor;
    }


    @Override
    public String toString() {
        return "NoteModel{" +
                "noteId='" + noteId + '\'' +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", dateModified='" + dateModified + '\'' +
                ", noteColor=" + noteColor +
                '}';
    }
}
