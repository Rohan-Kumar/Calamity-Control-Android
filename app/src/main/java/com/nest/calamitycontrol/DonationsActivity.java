package com.nest.calamitycontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
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

public class DonationsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RVAdapter mAdapter;
    ArrayList<DonationsData> dataModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DonationsActivity.this,DonateActivity.class));
//
//                Snackbar.make(view, "Add donation", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        setupRecyclerView();
        getData();
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DonationsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RVAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    private void getData() {
        DatabaseReference databaseRef;

        databaseRef = FirebaseDatabase.getInstance().getReference("donations");


        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    DonationsData datamodel = new DonationsData();
                    datamodel.setName(data.child("name").getValue(String.class));
                    datamodel.setDonationItem(data.child("item").getValue(String.class));
                    datamodel.setPlace(data.child("place").getValue(String.class));
                    datamodel.setNumber(data.child("number").getValue(String.class));
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
            return new Holder(LayoutInflater.from(DonationsActivity.this).inflate(R.layout.single_donation, parent, false));
        }

        @Override
        public void onBindViewHolder(RVAdapter.Holder holder, final int position) {
            holder.name.setText(dataModelArrayList.get(position).getName());
            holder.place.setText(dataModelArrayList.get(position).getPlace());
            holder.donation.setText(dataModelArrayList.get(position).getDonationItem());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dataModelArrayList.get(position).getNumber()!=null){
                        final String num = dataModelArrayList.get(position).getNumber();
                        if(!num.equals("")){
                            new AlertDialog.Builder(DonationsActivity.this).setTitle("Contact").setMessage(num).setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                    phoneIntent.setData(Uri.parse("tel:"+num));
                                    if (ActivityCompat.checkSelfPermission(DonationsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    startActivity(phoneIntent);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return dataModelArrayList.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView name, donation, place;

            public Holder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                donation = (TextView) itemView.findViewById(R.id.item);
                place = (TextView) itemView.findViewById(R.id.location);
            }

        }

    }
}
