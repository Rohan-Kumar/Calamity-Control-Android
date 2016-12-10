package com.nest.calamitycontrol;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class VolunteerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RVAdapter mAdapter;
    ArrayList<VolunteerData> dataModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Register new Volunteer", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setupRecyclerView();
        getData();

    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VolunteerActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RVAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    private void getData() {
        DatabaseReference databaseRef;

        databaseRef = FirebaseDatabase.getInstance().getReference("volunteers");


        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    VolunteerData datamodel = new VolunteerData();
                    datamodel.setName(data.child("name").getValue(String.class));
                    datamodel.setNumber(data.child("number").getValue(String.class));
                    datamodel.setPlace(data.child("place").getValue(String.class));
                    dataModelArrayList.add(datamodel);
                }
                Collections.reverse(dataModelArrayList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    class RVAdapter extends RecyclerView.Adapter<RVAdapter.Holder> {
        @Override
        public RVAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RVAdapter.Holder(LayoutInflater.from(VolunteerActivity.this).inflate(R.layout.single_donation, parent, false));
        }

        @Override
        public void onBindViewHolder(RVAdapter.Holder holder, int position) {
            holder.name.setText(dataModelArrayList.get(position).getName());
            holder.place.setText(dataModelArrayList.get(position).getPlace());
            holder.number.setText(dataModelArrayList.get(position).getNumber());

        }

        @Override
        public int getItemCount() {
            return dataModelArrayList.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView name, number, place;

            public Holder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                number = (TextView) itemView.findViewById(R.id.number);
                place = (TextView) itemView.findViewById(R.id.location);
            }

        }

    }

}