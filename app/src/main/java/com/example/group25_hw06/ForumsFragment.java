package com.example.group25_hw06;

/**
 * Assignment #: HW06
 * File Name: Group25_HW06 ForumsFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ForumsFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    public ForumsFragment() {
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
        return inflater.inflate(R.layout.fragment_forums, container, false);
    }

    Button logoutButton, createForumButton;
    RecyclerView forumListRecyclerView;
    LinearLayoutManager layoutManager;
    ForumRecyclerViewAdapter adapter;
    ArrayList<Forum> forumList;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.forums_fragment_title);
        mAuth = FirebaseAuth.getInstance();

        forumList = new ArrayList<>();

        forumListRecyclerView = view.findViewById(R.id.recyclerView);
        forumListRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        forumListRecyclerView.setLayoutManager(layoutManager);
        adapter = new ForumRecyclerViewAdapter(forumList);
        forumListRecyclerView.setAdapter(adapter);
        getForumData();

        logoutButton = view.findViewById(R.id.forum_logoutButton);
        createForumButton = view.findViewById(R.id.forum_newForumButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                mListener.logout();
            }
        });

        createForumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCreateForum();
            }
        });
    }

    void getForumData(){
        database = FirebaseFirestore.getInstance();

        //set to look for realtime updates
        database.collection("forums")
                .orderBy("timeCreated", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        forumList.clear();
                        for(QueryDocumentSnapshot document : value){
                            //set forum items here
                            Forum forum = document.toObject(Forum.class);
                            forum.setForumID(document.getId());

                            //add forum item to forum list
                            forumList.add(forum);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    class ForumRecyclerViewAdapter extends RecyclerView.Adapter<ForumRecyclerViewAdapter.ForumViewHolder> {
        ArrayList<Forum> forumArrayList;

        public ForumRecyclerViewAdapter(ArrayList<Forum> forums) {
            this.forumArrayList = forums;
        }

        @NonNull
        @Override
        public ForumRecyclerViewAdapter.ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_list_item, parent, false);
            ForumViewHolder forumsViewHolder = new ForumRecyclerViewAdapter.ForumViewHolder(view);

            return forumsViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
            if(forumArrayList.size() != 0) {
                Forum forum = forumArrayList.get(position);
                holder.forumTitle.setText(forum.getTitle());
                holder.forumAuthor.setText(forum.getAuthor());
                holder.forumDescription.setText(forum.getDescription());
                holder.forumInfo.setText(forum.getTimeCreated());
                int numLikes = forum.getUserLikes().size();
                if(numLikes == 1) {
                    holder.forumLikes.setText("1 Like | ");
                } else {
                    holder.forumLikes.setText(String.valueOf(numLikes) + " Likes | ");
                }

                holder.forumID = forum.getForumID();
                holder.likes = forum.getUserLikes();

                //adds delete button to user's posts only
                FirebaseUser user = mAuth.getCurrentUser();
                String id = user.getUid();

                if(forum.getUserID().equals(id)){
                    holder.deleteButton.setClickable(true);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteButton.setClickable(false);
                    holder.deleteButton.setVisibility(View.INVISIBLE);
                }

                ArrayList<String> listOfLikes = forum.getUserLikes();
                if(listOfLikes.contains(user.getUid())) {
                    holder.likeButton.setImageResource(R.drawable.like_favorite);
                } else {
                    holder.likeButton.setImageResource(R.drawable.like_not_favorite);
                }
            }
        }

        @Override
        public int getItemCount() {
            return this.forumArrayList.size();
        }

        class ForumViewHolder extends RecyclerView.ViewHolder {
            TextView forumTitle;
            TextView forumAuthor;
            TextView forumDescription;
            TextView forumInfo;
            TextView forumLikes;
            ImageView deleteButton;
            ImageView likeButton;
            String forumID;
            ArrayList<String> likes;

            public ForumViewHolder(@NonNull View itemView) {
                super(itemView);

                this.forumTitle = itemView.findViewById(R.id.forum_forumTitle);
                this.forumAuthor = itemView.findViewById(R.id.forum_forumAuthor);
                this.forumDescription = itemView.findViewById(R.id.forum_forumDescription);
                this.forumInfo = itemView.findViewById(R.id.forum_forumInfo);
                this.forumLikes = itemView.findViewById(R.id.forum_likesCount);
                this.deleteButton = itemView.findViewById(R.id.forum_deleteButton);
                this.likeButton = itemView.findViewById(R.id.forum_likeButton);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //delete using delete() method
                        database.collection("forums").document(forumID)
                                .collection("comments")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            String docToDelete = doc.getId();
                                            //delete subcollection docs here
                                            database.collection("forums").document(forumID)
                                                    .collection("comments").document(docToDelete).delete();
                                        }

                                        database.collection("forums").document(forumID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(), "Forum successfully deleted!", Toast.LENGTH_SHORT).show();
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
                });

                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userID = mAuth.getCurrentUser().getUid();
                        if(likes.contains(userID)) {
                            //remove userID from likes list
                            database.collection("forums").document(forumID)
                                    .update("userLikes", FieldValue.arrayRemove(userID));
                        } else {
                            //add userID to likes list
                            database.collection("forums").document(forumID)
                                    .update("userLikes", FieldValue.arrayUnion(userID));
                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.goToForumDetails(forumID);
                    }
                });
            }
        }
    }

    ForumFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumFragmentListener) context;
    }

    interface ForumFragmentListener {
        void logout();
        void goToCreateForum();
        void goToForumDetails(String forumID);
    }
}