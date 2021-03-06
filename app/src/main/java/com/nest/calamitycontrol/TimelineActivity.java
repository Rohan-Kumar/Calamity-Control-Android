package com.nest.calamitycontrol;

import android.*;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TimelineActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RVAdapter mAdapter;
    ArrayList<TimelineData> dataModelArrayList = new ArrayList<>();
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        FirebaseDatabase.getInstance().getReference("reports").keepSynced(true);

        setupRecyclerView();
        getData();

        dialog = new ProgressDialog(TimelineActivity.this);
        dialog.setIndeterminate(true);
        dialog.setMessage("please wait...");
        dialog.show();
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RVAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    private void getData() {
        DatabaseReference databaseRef;

        databaseRef = FirebaseDatabase.getInstance().getReference("reports");

/*
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                dataModelArrayList.clear();
                Log.d("TAG", "onChildAdded: ");
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("description").exists()) {
                        TimelineData datamodel = new TimelineData();
                        datamodel.setName(data.child("calamity").getValue(String.class));
                        datamodel.setDesc(data.child("description").getValue(String.class));
                        datamodel.setTime(data.child("time").getValue(String.class));
//                    datamodel.setPlace(data.child("place").getValue(String.class));


                        try {
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(TimelineActivity.this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(data.child("lat").getValue(Double.class), data.child("lng").getValue(Double.class), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            String city = addresses.get(0).getLocality(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String country = addresses.get(0).getCountryName();
                            String place = city + "," + country;
                            datamodel.setPlace(place);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        dataModelArrayList.add(datamodel);

                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/


        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("description").exists()) {
                        TimelineData datamodel = new TimelineData();
                        datamodel.setName(data.child("calamity").getValue(String.class));
                        datamodel.setDesc(data.child("description").getValue(String.class));
                        datamodel.setTime(data.child("time").getValue(String.class));
                        datamodel.setArea(data.child("area").getValue(String.class));
                        datamodel.setCity(data.child("city").getValue(String.class));
                        datamodel.setLandmark(data.child("landmark").getValue(String.class));
                        datamodel.setLevel(data.child("level").getValue(String.class));
                        datamodel.setPhone(data.child("phone").getValue(String.class));

//                    datamodel.setPlace(data.child("place").getValue(String.class));


//                        try {
//                            Geocoder geocoder;
//                            List<Address> addresses;
//                            geocoder = new Geocoder(TimelineActivity.this, Locale.getDefault());
//                            addresses = geocoder.getFromLocation(data.child("lat").getValue(Double.class), data.child("lng").getValue(Double.class), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                            String city = addresses.get(0).getLocality(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                            String country = addresses.get(0).getCountryName();
//                            String place = city + "," + country;
//                            datamodel.setPlace(place);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (IndexOutOfBoundsException i){
//                            i.printStackTrace();
//                        }


                        dataModelArrayList.add(datamodel);

                    }
                }
                Collections.reverse(dataModelArrayList);
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    class RVAdapter extends RecyclerView.Adapter<RVAdapter.Holder> {
        @Override
        public RVAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RVAdapter.Holder(LayoutInflater.from(TimelineActivity.this).inflate(R.layout.single_card, parent, false));
        }

        @Override
        public void onBindViewHolder(RVAdapter.Holder holder, final int position) {
            holder.name.setText(dataModelArrayList.get(position).getName());
            holder.desc.setText("Danger Level : "+dataModelArrayList.get(position).getLevel()+"\n"+dataModelArrayList.get(position).getDesc());
//            String place;
//            if(dataModelArrayList.get(position).landmark.equals("")){
//                place = dataModelArrayList.get(position).landmark+",\n"+dataModelArrayList.get(position).getArea()+",\n"+dataModelArrayList.get(position).getCity();
//            }else{
//                place = dataModelArrayList.get(position).getArea()+",\n"+dataModelArrayList.get(position).getCity();
//            }
            holder.place.setText(dataModelArrayList.get(position).landmark+"\n"+dataModelArrayList.get(position).getArea()+",\n"+dataModelArrayList.get(position).getCity());
            holder.time.setText(dataModelArrayList.get(position).getTime());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        if(dataModelArrayList.get(position).getPhone()!=null){
                            final String num = dataModelArrayList.get(position).getPhone();
                            if(!num.equals("")){
                                new AlertDialog.Builder(TimelineActivity.this).setTitle("Contact").setMessage(num).setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                        phoneIntent.setData(Uri.parse("tel:"+num));
                                        if (ActivityCompat.checkSelfPermission(TimelineActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

            TextView name, desc, place, time;

            public Holder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                desc = (TextView) itemView.findViewById(R.id.desc);
                time = (TextView) itemView.findViewById(R.id.timestamp);
                place = (TextView) itemView.findViewById(R.id.place);
            }

        }

    }

}
