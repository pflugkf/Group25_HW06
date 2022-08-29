package com.example.group25_hw06;

/**
 * Assignment #: HW06
 * File Name: Group25_HW06 ForumDetailsFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumDetailsFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FORUM_ID = "ARG_FORUM_ID";

    // TODO: Rename and change types of parameters
    private String forumID;

    public ForumDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param forum Parameter 1.
     * @return A new instance of fragment ForumDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForumDetailsFragment newInstance(String forum) {
        ForumDetailsFragment fragment = new ForumDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FORUM_ID, forum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forumID = getArguments().getString(ARG_FORUM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum_details, container, false);
    }

    Button postButton;
    TextView forumTitle, forumAuthor, forumText;
    TextView forumCommentCount;
    EditText commentTextbox;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CommentsRecyclerViewAdapter adapter;
    ArrayList<Comment> commentsList;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.forum_details_fragment_title);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        commentsList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.details_commentList);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CommentsRecyclerViewAdapter(commentsList);
        recyclerView.setAdapter(adapter);
        getComments();

        forumTitle = view.findViewById(R.id.details_forumTitle);
        forumAuthor = view.findViewById(R.id.details_forumAuthor);
        forumText = view.findViewById(R.id.details_forumText);
        forumCommentCount = view.findViewById(R.id.details_forumCommentCountLabel);
        commentTextbox = view.findViewById(R.id.details_commentTextbox);

        database.collection("forums").document(forumID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Forum forumDisplay = new Forum();
                        forumDisplay = documentSnapshot.toObject(Forum.class);

                        forumTitle.setText(forumDisplay.getTitle());
                        forumAuthor.setText(forumDisplay.getAuthor());
                        forumText.setText(forumDisplay.getDescription());
                    }
                }).addOnFailureListener(new OnFailureListener() {
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

        postButton = view.findViewById(R.id.details_postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = commentTextbox.getText().toString();

                if(commentText.isEmpty()){
                    //alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid comment!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    //make comment
                    Comment newComment = new Comment();
                    FirebaseUser user = mAuth.getCurrentUser();

                    newComment.setCommentText(commentText);
                    newComment.setPosterName(user.getDisplayName());
                    newComment.setPosterID(user.getUid());
                    newComment.setDatePosted(new SimpleDateFormat().format(new Date()));

                    database.collection("forums").document(forumID)
                            .collection("comments")
                            .add(newComment)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    newComment.setCommentID(documentReference.getId());

                                    database.collection("forums").document(forumID)
                                            .collection("comments")
                                            .document(documentReference.getId())
                                            .update("commentID", newComment.getCommentID())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("q/test", "commentID added to new comment");
                                                }
                                            });
                                    Toast.makeText(getActivity(), "New comment created!", Toast.LENGTH_SHORT).show();
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

                    commentTextbox.getText().clear();
                }
            }
        });
    }

    void getComments() {
        database.collection("forums").document(forumID)
                .collection("comments")
                .orderBy("datePosted", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        commentsList.clear();

                        for(QueryDocumentSnapshot doc : value) {
                            Comment comment = doc.toObject(Comment.class);
                            comment.setCommentID(doc.getId());

                            commentsList.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                        if(commentsList.size() == 1) {
                            forumCommentCount.setText(commentsList.size() + " Comment");
                        } else {
                            forumCommentCount.setText(commentsList.size() + " Comments");
                        }

                    }
                });
    }

    class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.CommentsViewHolder> {
        ArrayList<Comment> commentArrayList;

        public CommentsRecyclerViewAdapter(ArrayList<Comment> comments) {
            this.commentArrayList = comments;
        }

        @NonNull
        @Override
        public CommentsRecyclerViewAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
            CommentsViewHolder commentsViewHolder = new CommentsRecyclerViewAdapter.CommentsViewHolder(view);

            return commentsViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CommentsRecyclerViewAdapter.CommentsViewHolder holder, int position) {
            if(commentArrayList.size() != 0) {
                Comment comment = commentArrayList.get(position);
                holder.commentAuthor.setText(comment.getPosterName());
                holder.commentText.setText(comment.getCommentText());
                holder.commentDatePosted.setText(comment.getDatePosted());
                holder.commentID = comment.getCommentID();

                //adds delete button to user's posted comments only
                FirebaseUser user = mAuth.getCurrentUser();
                String id = user.getUid();

                if(comment.getPosterID().equals(id)){
                    holder.deleteButton.setClickable(true);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteButton.setClickable(false);
                    holder.deleteButton.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return commentArrayList.size();
        }

        class CommentsViewHolder extends RecyclerView.ViewHolder {
            TextView commentAuthor;
            TextView commentText;
            TextView commentDatePosted;
            ImageView deleteButton;
            String commentID;

            public CommentsViewHolder(@NonNull View itemView) {
                super(itemView);

                commentAuthor = itemView.findViewById(R.id.comment_authorName);
                commentText = itemView.findViewById(R.id.comment_commentText);
                commentDatePosted = itemView.findViewById(R.id.comment_datePosted);
                deleteButton = itemView.findViewById(R.id.comment_deleteButton);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.collection("forums").document(forumID)
                                .collection("comments").document(commentID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Comment successfully deleted!", Toast.LENGTH_SHORT).show();
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
            }
        }
    }
}