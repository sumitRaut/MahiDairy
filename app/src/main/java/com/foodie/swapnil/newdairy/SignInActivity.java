package com.foodie.swapnil.newdairy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    ProgressBar progressBar;
   // private SignInButton btnSignIn;
    private Button btnSignIn;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // making notification bar transparent
        changeStatusBarColor();

        btnSignIn = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        cardView = findViewById(R.id.cardView);
        cardView.setBackgroundResource(R.drawable.input_background_2);

        progressBar.setVisibility(View.INVISIBLE);
        btnSignIn.clearFocus();
        auth = FirebaseAuth.getInstance();

        account = GoogleSignIn.getLastSignedInAccount(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    startActivityForResult(signInBuilder(), RC_SIGN_IN);
                    //signInBuilder();
                }else {
                    Toast.makeText(getApplicationContext(),"Check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (auth.getCurrentUser() != null) {
            // already signed in
            // Start your app main activity
//            Intent i = new Intent(this, DashboardActivity.class);
            Intent i = new Intent(this, InputActivity.class);

            startActivity(i);
            // close this activity
            finish();
        } else {
            // not signed in
            //startActivityForResult(signInBuilder(), RC_SIGN_IN);
            //signInBuilder();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private Intent signInBuilder() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);
        return signInIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("Failed", "Google sign in failed", e);
                    //updateUI(null);
                    //relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("AuthenticationId", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Log.i("Credentials", credential.toString());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignInSuccess", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            if (checkIfNewUser()) {
                                //user is new
                                createDatabaseForNewUser();
                            }
                            Intent i = new Intent(SignInActivity.this, InputActivity.class);
                            //i.putExtra("response", idpResponse);
                            startActivity(i);
                            progressBar.setVisibility(View.INVISIBLE);
                            // close this activity
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FailCredentials", "signInWithCredential:failure", task.getException());
                            progressBar.setVisibility(View.INVISIBLE);
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private boolean checkIfNewUser() {
        FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();
        return (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp());
    }

    private void createDatabaseForNewUser() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String UID = currentUser.getUid();
        FirebaseDatabase database = Utils.getDatabase();
        final DatabaseReference databaseReference = database.getReference("users/" + UID);
        databaseReference.keepSynced(true);
    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
