package com.example.kyle.patiencetraining.MainUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.kyle.patiencetraining.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 3;
    static final String UID_EXTRA = "uid_string";
    static final String LOGIN_TASK_EXTRA = "login_task";
    static final int LOGIN_TASK = 66;
    static final int LOGOUT_TASK = 77;
    private GoogleSignInOptions gso;
    private TextView progressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressTextView = findViewById(R.id.progressTextView);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        int task = intent.getIntExtra(LOGIN_TASK_EXTRA, -1);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (task == LOGIN_TASK && currentUser != null) {
            progressTextView.setText(R.string.skip);
            intent.putExtra(UID_EXTRA,currentUser.getUid());
            setResult(RESULT_OK, intent);
            finish();
        }else if (task == LOGIN_TASK) {
            progressTextView.setText(R.string.goog_account);
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            signIn();
        }else if(task == LOGOUT_TASK){
            progressTextView.setText(R.string.success_signout);
            signOut();
        }
    }

    private void signIn() {
        GoogleSignInClient client = GoogleSignIn.getClient(this,gso);
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account);
                else
                    throw new ApiException(Status.RESULT_INTERNAL_ERROR);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                progressTextView.setText(R.string.goog_error);
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Initialize Firebase Auth

        progressTextView.setText(R.string.auth_serv);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressTextView.setText(R.string.success);
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user == null){
                                setResult(RESULT_CANCELED);
                                progressTextView.setText(R.string.serv_error);
                            }else {
                                intent.putExtra(UID_EXTRA, user.getUid());
                                setResult(RESULT_OK, intent);
                            }
//
//                            firestoreDB(user != null ? user.getUid() : "");
                        } else {
                            // If sign in fails, display a message to the user.
                            setResult(RESULT_CANCELED);
                            progressTextView.setText(R.string.serv_error);
                        }
                        finish();
                    }
                });
    }

    private void signOut(){
        mAuth.signOut();
        setResult(RESULT_OK);
        finish();
    }
}
