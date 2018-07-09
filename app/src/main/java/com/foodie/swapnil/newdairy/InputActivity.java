package com.foodie.swapnil.newdairy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class InputActivity extends AppCompatActivity {

    int i = 0;
    private EditText etCowId;
    private EditText etMilk;
    private Button btnSubmit;
    private Spinner spinner;
    private ConstraintLayout layout;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        etCowId = findViewById(R.id.etId);
        etMilk = findViewById(R.id.etMilk);
        btnSubmit = findViewById(R.id.btnSubmit);
        spinner = findViewById(R.id.spinner);
        layout = findViewById(R.id.constraint);
        textView = findViewById(R.id.textView);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        layout.setBackgroundResource(R.drawable.slant_background_2);

        etMilk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    Double quantity = Double.parseDouble(s.toString());
                    if (quantity > 30) {
                        etMilk.setError("Milk should be less than 30 Liters");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error1 = true;
                boolean error2 = true;
                boolean error3 = true;

                String cow_number = etMilk.getText().toString();
                String milk = etMilk.getText().toString();

                if (cow_number.isEmpty() || etCowId.getText().toString() == null) {
                    etCowId.setError("Enter the cow number");
                    error1 = false;
                }

                if (milk.isEmpty() || milk == null) {
                    etMilk.setError("Enter the quantity");
                    error2 = false;
                } else if (milk.equals(".") || milk.equals("0")) {
                    etMilk.setError("You should enter vaild quantity");
                    error3 = false;
                } else if (Double.parseDouble(milk) > 30) {
                    etMilk.setError("Milk should be less than 30 Liters");
                    error3 = false;
                }

                if (error1 == true && error2 == true && error3 == true) {
                    addValues();
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputActivity.this, DisplayActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    private void addValues() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String UID = currentUser.getUid();
        FirebaseDatabase database = Utils.getDatabase();

        final String id = etCowId.getText().toString();
        final Date date = Calendar.getInstance().getTime();
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date_1 = df.format("dd-MM-yyyy", new java.util.Date()).toString();
        final DatabaseReference databaseReference = database.getReference().child("" + UID).child(date_1).child(id);

        databaseReference.keepSynced(true);

        final DairyModel dairyModel = new DairyModel();
        String period = spinner.getSelectedItem().toString();

        if (period.equals("Morning")) {
            dairyModel.setMilk_Morn(etMilk.getText().toString());
            dairyModel.setMilk_Even("0.00");
            databaseReference.setValue(dairyModel);

            Toast.makeText(this, "Value Added", Toast.LENGTH_SHORT).show();
            etCowId.getText().clear();
            etMilk.getText().clear();
        } else {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("milk_Morn").exists()) {
                        databaseReference.child("milk_Even").setValue(etMilk.getText().toString());
                        etCowId.getText().clear();
                        etMilk.getText().clear();
                    } else {
                        databaseReference.child("milk_Morn").setValue("0.00");
                        databaseReference.child("milk_Even").setValue(etMilk.getText().toString());
                        etCowId.getText().clear();
                        etMilk.getText().clear();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

/*
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i("occurrence",String.valueOf(i++));
                    if (dataSnapshot.child("milk_Morn").exists() && dataSnapshot.child("milk_Even").getValue().toString()== "0.0") {
                        databaseReference.child("milk_Even").setValue(etMilk.getText().toString());
                        etCowId.getText().clear();
                        etMilk.getText().clear();
                    } else {
                        databaseReference.child("milk_Morn").setValue("0.00");
                        databaseReference.child("milk_Even").setValue(etMilk.getText().toString());
                        etCowId.getText().clear();
                        etMilk.getText().clear();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
*/

            Toast.makeText(this, "Value Added", Toast.LENGTH_SHORT).show();
        }
    }
}
