//package com.example.notedApp;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.widget.EditText;
//import android.widget.ImageButton;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.DocumentReference;
//
//public class NoteDetailsActivity extends AppCompatActivity {
//
//    EditText titleEditText, contentEditText;
//    ImageButton saveNoteBtn;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_note_details);
//
//
//         contentEditText = findViewById(R.id.notes_content_text);
//         saveNoteBtn = findViewById(R.id.save_note_btn);
//         titleEditText = findViewById(R.id.notes_content_text);
//
//         saveNoteBtn.setOnClickListener((v)->savenote());
//    }
//
//    void savenote(){
//        String noteTitle = titleEditText.getText().toString();
//        String noteContent = contentEditText.getText().toString();
//        if(noteTitle==null || noteTitle.isEmpty()){
//            titleEditText.setError("Title is required");
//            return;
//        }
//        Note note = new Note();
//        note.setTitle(noteTitle);
//        note.setContent(noteContent);
//        note.setTimestamp(Timestamp.now());
//
//        saveNoteToFirebase(note);
//
//
//    }
//
//    void saveNoteToFirebase(Note note){
//        DocumentReference documentReference;
//        documentReference = Note_Utility.getCollectionReferenceforNotes().document();
//
//        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    //note is added
//                    Note_Utility.showToast(NoteDetailsActivity.this,"Note added successfully");
//                    finish();
//                }else{
//                    Note_Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");
//                }
//            }
//        });
//
//
//    }
//}
