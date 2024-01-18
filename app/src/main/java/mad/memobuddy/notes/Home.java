package mad.memobuddy.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import java.util.ArrayList;

import mad.memobuddy.notes.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    TextView tvBtnAdd, tvEmptyIndicator;
    GridAdapter gridAdapter;
    DBHelper dbh = new DBHelper(Home.this);
    ArrayList<NoteModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvEmptyIndicator = findViewById(R.id.tvEmptyIndicator);

//        ArrayList<NoteModel> list = new ArrayList<>();
//        list.add(new NoteModel("001", "T001", "C001", 0));
//        list.add(new NoteModel("002", "T002", "C002", 1));
//        list.add(new NoteModel("003", "T003", "C003", 3));
        list = dbh.getAllNotes();

        if (!list.isEmpty()){
            tvEmptyIndicator.setVisibility(View.INVISIBLE);
        }


        gridAdapter = new GridAdapter(Home.this, list);
        binding.gridView.setAdapter(gridAdapter);

        Intent intent = new Intent(Home.this, NoteViewer.class);
        binding.gridView.setOnItemClickListener((parent, view, position, id) -> {
            //
            //Toast.makeText(Home.this, "note id is: " + list.get(position).noteId, Toast.LENGTH_SHORT).show();
            intent.putExtra("to", "update");
            intent.putExtra("id", list.get(position).noteId);
            startActivity(intent);
            //gridAdapter.removeItem(list.get(position));


        });

        tvBtnAdd = findViewById(R.id.add);
        tvBtnAdd.setOnClickListener(v -> {
            //Toast.makeText(Home.this, "add a note, last key:"+dbh.getLastPrimKey(), Toast.LENGTH_SHORT).show();
            intent.putExtra("to", "new");
            intent.putExtra("id", dbh.getLastPrimKey()+"");
            startActivity(intent);
        });



    }


}