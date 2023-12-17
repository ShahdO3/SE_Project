package com.example.notedApp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Noted extends AppCompatActivity implements Notes_interface {

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menubtn;


    void showMenu(){
        //TODO display menu
    }

    @Override
    public void oncreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_page);

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.notes_recycler);
        menubtn = findViewById(R.id.dropdown_menu);

        addNoteBtn.setOnClickListener((v)-> startActivities(new Intent[]{new Intent(Noted.this, NoteDetailsActivity.class)}));
        menubtn.setOnClickListener((v)->showMenu());
    }


    void setupRecyclerView(){


    }
}
