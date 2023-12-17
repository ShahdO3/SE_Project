package com.example.notedApp;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter {
    class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, contentTextView, timestampTextView;
        public  NoteViewHolder (@NonNull View itemview){
            super(itemview);
            titleTextView = itemview.findViewById(R.id.note)
        }
    }
}
