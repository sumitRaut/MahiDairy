package com.foodie.swapnil.newdairy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DisplayActivity extends AppCompatActivity {
    private ArrayList<DairyModel> dairyModelArrayList = new ArrayList<>();
    private RecyclerAdapter adapter3;
    private LinearLayoutManager layoutManager;
    private RecyclerView listView;
    private TextView tvTime;
    private TextView tvDay;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        listView = findViewById(R.id.listView);
        tvTime = findViewById(R.id.tvtime);
        tvDay = findViewById(R.id.tvDay);

        setTime();
        displaylist();

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            int positiveOffset = 0;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                positiveOffset = -verticalOffset;

                if (scrollRange + verticalOffset >= 0 && positiveOffset > (scrollRange / 1.5)) {
                    collapsingToolbarLayout.setTitle("Mahidairy");
                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDatePicker();
            }
        });
    }

    private void setTime() {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date_1 = df.format("dd-MM-yyyy", new java.util.Date()).toString();
        tvTime.setText(date_1);

        String dayOfWeek = df.format("EEEE", new java.util.Date()).toString();
        tvDay.setText(dayOfWeek);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //on selection of back arrow
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DisplayActivity.this, InputActivity.class));
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    /*
        Displays the list of milk entries
    */
    public void displaylist() {
        // Arraylist should be cleared otherwise previous data will also be displayed on screen
        dairyModelArrayList.clear();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String UID = currentUser.getUid();
        FirebaseDatabase database = Utils.getDatabase();
        final DatabaseReference databaseReference = database.getReference().child("" + UID).child(tvTime.getText().toString());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : children) {
                    DairyModel obj = snapshot.getValue(DairyModel.class);
                    obj.setId(snapshot.getKey());
                    if (obj != null) {
                        dairyModelArrayList.add(obj);
                        adapter3.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        layoutManager = new LinearLayoutManager(this);
        adapter3 = new RecyclerAdapter(dairyModelArrayList);
        listView.setLayoutManager(layoutManager);
        listView.setNestedScrollingEnabled(false);
        listView.setAdapter(adapter3);
    }

    public void createDatePicker() {
        // Variables to show current date in DatePicker
        final int cur_year = Calendar.getInstance().get(Calendar.YEAR);
        final int cur_month = Calendar.getInstance().get(Calendar.MONTH);
        final int cur_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                //in DatePicker, selected month is behind curent month by 1
                month++;

                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = format.parse(dayOfMonth + "-" + month + "-" + year);
                    android.text.format.DateFormat df = new android.text.format.DateFormat();

                    // To show date in dd-MM-yy
                    String date_1 = df.format("dd-MM-yyyy", date).toString();
                    tvTime.setText(date_1);

                    // To show day of Week
                    String dayOfWeek = df.format("EEEE", date).toString();
                    tvDay.setText(dayOfWeek);

                    displaylist();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, cur_year, cur_month, cur_day);
        datePickerDialog.show();
    }
}
