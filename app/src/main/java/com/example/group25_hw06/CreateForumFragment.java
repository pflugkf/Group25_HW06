package com.example.group25_hw06;

/**
 * Assignment #: HW06
 * File Name: Group25_HW06 CreateForumFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateForumFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    public CreateForumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_forum, container, false);
    }

    EditText forumName, forumDescriptionText;
    Button submit, cancel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.create_forum_fragment_title);

        mAuth = FirebaseAuth.getInstance();

        forumName = view.findViewById(R.id.create_forumTitleTextbox);
        forumDescriptionText = view.findViewById(R.id.create_forumDescriptionTextbox);

        submit = view.findViewById(R.id.create_submitButton);
        cancel = view.findViewById(R.id.create_cancelButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String forumTitle = forumName.getText().toString();
                String forumDescription = forumDescriptionText.getText().toString();

                if(forumTitle.isEmpty()) {
                    //alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid forum title")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else if(forumDescription.isEmpty()) {
                    //alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid forum description")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    Forum newForum = new Forum();
                    FirebaseUser user = mAuth.getCurrentUser();
                    newForum.setTitle(forumTitle);
                    newForum.setDescription(forumDescription);
                    newForum.setTimeCreated(new SimpleDateFormat().format(new Date()));
                    newForum.setUserID(user.getUid());
                    newForum.setAuthor(user.getDisplayName());
                    newForum.setUserLikes(new ArrayList<String>());

                    database = FirebaseFirestore.getInstance();

                    database.collection("forums")
                            .add(newForum)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    newForum.setForumID(documentReference.getId());
                                    database.collection("forums").document(documentReference.getId())
                                            .update("forumID", newForum.getForumID())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("q/test", "forumID added to new forum");
                                                }
                                            });
                                    Toast.makeText(getActivity(), "New forum created!", Toast.LENGTH_SHORT).show();
                                    mListener.submitNewForum();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                    b.setTitle("Error")
                                            .setMessage(e.getMessage())
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                    b.create().show();
                                }
                            });

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelToForums();
            }
        });
    }

    CreateForumFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (CreateForumFragmentListener) context;
    }

    interface CreateForumFragmentListener {
        void cancelToForums();
        void submitNewForum();
    }
}