package com.uksw.khw.whatsapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupFragment extends Fragment
{
    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_group = new ArrayList<>();

    private DatabaseReference GroupRef;


    public GroupFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        groupFragmentView = inflater.inflate(R.layout.fragment_group,container,false);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        IntializeFields();

        RetrieveAndDisplayGroups();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName = parent.getItemAtPosition(position).toString();
                Intent grupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                grupChatIntent.putExtra("groupName", currentGroupName);
                startActivity(grupChatIntent);
            }
        });

        return groupFragmentView;
    }




    private void IntializeFields()
    {
        list_view = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_group);
        list_view.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroups()
    {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                list_of_group.clear();
                list_of_group.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

}
