//package com.example.notedApp;
//
//import android.widget.Toast;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//
//public class Note_Utility {
//
//    static void showToast(NoteDetailsActivity noteDetailsActivity, String noteAddedSuccessfully){
//        Toast.makeText(noteDetailsActivity,noteAddedSuccessfully,Toast.LENGTH_SHORT).show();
//    }
//
//   static CollectionReference getCollectionReferenceforNotes(){
//       FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//       return FirebaseFirestore.getInstance().collection("notes").document(currentUser.getUid()).collection("my_notes");
//   }
//
//}
