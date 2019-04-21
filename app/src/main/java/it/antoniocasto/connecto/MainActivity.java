package it.antoniocasto.connecto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //ACTIVITY DOVE DEVE COMPARIRE LA CHAT CON UN CONTATTO

    //Autenticazione con FireBase
    private FirebaseAuth mAuth;



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI();
    }

    private void updateUI() {
        // TODO: Se utente è loggato si va in MainActivity

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            //Non c'è utente loggato

            Intent intToLogin = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intToLogin);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Todo 2: ricevere dati dall'intent ed estrarli con il metodo getExtras()
        Bundle b = getIntent().getExtras();
        String extra = b.getString("msg");

        mAuth = FirebaseAuth.getInstance();

        //Setto il nome utente nella barra
        setTitle(mAuth.getCurrentUser().getDisplayName());
    }
}
