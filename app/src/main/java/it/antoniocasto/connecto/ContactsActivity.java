package it.antoniocasto.connecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.antoniocasto.connecto.adapter.ContactsListAdapter;
import it.antoniocasto.connecto.model.User;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    //UI

    private RecyclerView rvContactList;
    private ContactsListAdapter contactsListAdapter;



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
    protected void onStop() {
        super.onStop();

        contactsListAdapter.clean();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //Todo 2: ricevere dati dall'intent ed estrarli con il metodo getExtras()
        Bundle b = getIntent().getExtras();
        String extra = b.getString("msg");

        mAuth = FirebaseAuth.getInstance();

        //Setto il nome utente nella barra
        setTitle(mAuth.getCurrentUser().getDisplayName());

        Log.i(TAG, "Faccio la initUI");

        //Collego l'edit text dell'input del campo di cerca e il bottone per il cerca a java
        initUI();

        Log.i(TAG, "Carico la recycler view");

        //TODO: mettere in funzione la recycler view della lista contatti
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContactList.setLayoutManager(linearLayoutManager);

        Log.i(TAG, "Dopo setLayoutManager");

        //prendere riferimento nel database del proprio contatto
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(); //prendo il riferimento da un database in generale e non uno specifico definito tramite un nod ben preciso

        contactsListAdapter = new ContactsListAdapter(this, mDatabaseReference);
        rvContactList.setAdapter(contactsListAdapter);


        Log.i(TAG, "Preso riferimento db");

        //popolo la recyclerview on un listener nel db


        //Todo 3: Presentare dati attraverso un toast
        Toast.makeText(getApplicationContext(), "Utente : " + extra, Toast.LENGTH_LONG).show();
    }


    private void initUI() {

        //collego gli elementi della UI a java
        rvContactList = (RecyclerView) findViewById(R.id.rv_listacontatti);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //se questo metodo è presente crea il menu delle opzioni -> quello con i 3 pallini

        getMenuInflater().inflate(R.menu.layout_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //richiamato quando item del menu viene selezionato

        int id;

        id = item.getItemId();

        if( id == R.id.logoutItem){

            Log.i(TAG, "Logout selezionato");
            //TODO: Logout

            mAuth.signOut();
            updateUI();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void GetNomeOnPref() {
        SharedPreferences prefs;
        String nomeOnPrefs;

        prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);

        nomeOnPrefs = prefs.getString(RegisterActivity.NOME_KEY, null); //il secondo valore è quello di default se non trova nulla

        Log.i("GetNomeOnPrefs", nomeOnPrefs);
    }

}
