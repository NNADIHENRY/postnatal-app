package com.example.myapplication;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView myContactList;

    private DatabaseReference contactsRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactList = contactsView.findViewById(R.id.contacts_list);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts")/*.child("currentUserID")*/;
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ContactsContract.Contacts>()
                .setQuery(contactsRef, ContactsContract.Contacts.class)
                .build();

        FirebaseRecyclerAdapter<ContactsContract.Contacts, ContactsViewHolder > adapter = new FirebaseRecyclerAdapter<ContactsContract.Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull ContactsContract.Contacts model) {
                String usersIDs = getRef(position).getKey();

                usersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")){
                            String userImage = dataSnapshot.child("image").getValue().toString();
                            String profileStatus = dataSnapshot.child("status").getValue().toString();
                            String profileName = dataSnapshot.child("name").getValue().toString();

                            holder.userName.setText(profileName);
                            holder.userStatus.setText(profileStatus);
                            Picasso.get().load(userImage).placeholder(R.drawable.default_image).into(holder.profileImage);
                        }else{
                            String profileStatus = dataSnapshot.child("status").getValue().toString();
                            String profileName = dataSnapshot.child("name").getValue().toString();

                            holder.userName.setText(profileName);
                            holder.userStatus.setText(profileStatus);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.users_display_layout, viewGroup, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{


        TextView userName, userStatus;
        CircleImageView profileImage;
//        Button acceptBtn, cancelBtn;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
//            acceptBtn = itemView.findViewById(R.id.req_accept_btn);
//            cancelBtn = itemView.findViewById(R.id.req_reject_btn);
        }
    }
}
