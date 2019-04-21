package it.antoniocasto.connecto;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText mInputEmail; //per loggarsi inserisce l'email
    EditText mInputPassword;

    //Autenticazione Firebase
    private FirebaseAuth mAuth;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        // TODO: Se utente è loggato si va in MainActivity

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            //C'è utente loggato

            String email = user.getEmail();

            Intent intent3 = new Intent(this, ContactsActivity.class);
            intent3.putExtra("msg", email);
            finish();
            startActivity(intent3);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mInputEmail = findViewById(R.id.etLogUsername);
        mInputPassword = findViewById(R.id.etLogPassword);

        mAuth = FirebaseAuth.getInstance(); //mettere
    }

    public void btnLoginClick(View view) {
        String email, password;

        email = mInputEmail.getText().toString();

        password = mInputPassword.getText().toString();

        Log.d("LoginActivity", "Login Button Click");

        if(!(email.length()>7) || !(email.contains("@"))) {

            Toast.makeText(this, "Email non valida.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!(password.length()>7)){
            Toast.makeText(this, "Password non valida.", Toast.LENGTH_SHORT).show();
            return;
        }
        loginUser(email, password); //metodo per gestire il login con Firebase

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null); presento solo un toast in caso di fallimento
                        }

                        // ...
                    }
                });
    }

    public void tvRegistratiClick(View view) {
        Log.d("LoginActivity", "Registrati Button Click");

        Intent intent1 = new Intent(this, RegisterActivity.class);
        startActivity(intent1);
    }
}
