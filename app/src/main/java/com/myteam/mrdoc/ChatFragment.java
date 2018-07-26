package com.myteam.mrdoc;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {



    private RecyclerView users_list_view;
    private List<Users> users_list;
    private FirebaseFirestore firebaseFirestore;
    private UsersRecyclerAdapter usersRecyclerAdapter;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private String current_user_id;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_chat, container, false);
        users_list = new ArrayList<Users>();
        users_list_view = view.findViewById(R.id.user_list);
        usersRecyclerAdapter = new UsersRecyclerAdapter(users_list);
        users_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        users_list_view.setAdapter(usersRecyclerAdapter);
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        users_list_view.setHasFixedSize(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        users_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {

                    loadMorePost();

                }

            }
        });

        if (firebaseAuth.getCurrentUser() != null) {
            Query firstQuery = firebaseFirestore.collection("users").limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            users_list.clear();

                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {


                                    Users blogPost = doc.getDocument().toObject(Users.class);

                                    if (isFirstPageFirstLoad) {

                                        users_list.add(blogPost);

                                    } else {

                                        users_list.add(0, blogPost);

                                    }


                                    usersRecyclerAdapter.notifyDataSetChanged();

                                }
                            }

                            isFirstPageFirstLoad = false;

                        }

                    }

            });



        }

        // Inflate the layout for this fragment
        return view;


    }
    public void loadMorePost(){

        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("users").startAfter(lastVisible)
                    .limit(3);;

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {


                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {




                                    Users blogPost = doc.getDocument().toObject(Users.class);
                                    users_list.add(blogPost);

                                    usersRecyclerAdapter.notifyDataSetChanged();


                            }
                        }
                    }
                }
            });

        }

    }

}

