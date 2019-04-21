package it.antoniocasto.connecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.antoniocasto.connecto.model.User;

public class RegisterActivity extends AppCompatActivity {

    //COSTANTI
    static final String CHAT_PREFS = "ChatPrefs";
    static final String NOME_KEY = "username";



    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    EditText mConfPass;
    EditText mEmail, mPassword;
    EditText mNome;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        //controllo se utente già loggato
        //FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUI();
        Log.i("RegisterActivity", "onCreateRegiter");

        mAuth = FirebaseAuth.getInstance();


    }

    private void initUI() {
        mConfPass = findViewById(R.id.etRegPassword_conf);
        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etRegPassword);
        mNome = findViewById(R.id.etRegUsername);
    }


    public void tvLoginClick(View view) {
        Log.d("RegisterActivity", "Login cliccato");
        Intent intent2 = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent2);
    }

    public void btnRegistratiClick(View view) {
        Log.d("RegisterActivity", "Registrati cliccato");

        String nome = mNome.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        //Validazione Dati quando si clicca sul pulsante registrati
        if(!nomeValido(nome)){
            Toast.makeText(getApplicationContext(), "Nome deve contenere almeno 8 caratteri.", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!emailValida(email)){
                Toast.makeText(getApplicationContext(), "Email non valida, non contiene @.", Toast.LENGTH_SHORT).show();
            }else{
                if(!passwordValida(password)){
                    Toast.makeText(getApplicationContext(), "Password diversa dalla conferma o inferiore a 8 caratteri. Riprova.", Toast.LENGTH_SHORT).show();
                }else{
                    createFirebaseUser(email, password, nome);
                }
            }
        }

    }

    private boolean nomeValido(String nome){
        if(nome.length()>3) return true;
        else return false;
    }

    private boolean emailValida(String email){
        return email.contains("@");
    }

    private boolean passwordValida(String password){
        String confermaPassword = mConfPass.getText().toString();
        return confermaPassword.equals(password) && password.length()>=8;
    }

    private void createFirebaseUser(String email, String password, final String nome){ //funzione che permette di creare un nuovo utente firebase
        //nome deve essere di tipo final altrimenti da errore

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("ConnecTRegistration", "createUserWithEmail:success");
                            // FirebaseUser user = mAuth.getCurrentUser();

                            //showDialog("Successo", "Registrazione effettuata con successo!", android.R.drawable.ic_dialog_info);
                            //Ho scelto di commentare la showDialog perchè tanto se la registrazione è ok passo direttamente alla schermata di login


                            salvaNome(); //salva il nome nelle sharedpreferences

                            //TODO: Caricare nome in Firebase

                            setNome(nome);

                            //TODO: creare nodo con email nel db realtime
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            String idEmail = mEmail.getText().toString();
                            String idUser = idEmail;
                            idUser = idUser.replace('@', '+').replace('.', ':'); //su firbase mantengo questa formattazione
                            writeNewUser(idUser, nome, idEmail);



                            //vado nella schermata di LoginActivity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            finish(); //libera la memoria dell'activity RegisterActivity
                            startActivity(intent); //avvio l'activity

                            //L'utente viene già loggato

                            //Toast.makeText(RegisterActivity.this, "Authentication success.",
                            //Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("ConnecTRegistration", "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            // Toast.LENGTH_SHORT).show();

                            //Chiamo una dialog per avvertire
                            showDialog("Errore", "Errore nella Registrazione", android.R.drawable.ic_dialog_alert);
                        }

                        // ...
                    }
                });
    }

    private void salvaNome(){
        String nome;
        SharedPreferences prefs;

        nome = mNome.getText().toString();

        prefs = getSharedPreferences(CHAT_PREFS, 0);
        prefs.edit().putString(NOME_KEY, nome).apply();
    }

    private void setNome(String nome){
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build();
        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i("setNome", "Nome Caricato con successo");
                } else{
                    Log.i("setNome", "Errore nel caricamento del nome");
                }
            }
        });
    }

    //Salva un nuovo utente nel Realtime database
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);


        mDatabase.child("users").child(userId).child("profile").setValue(user);
        mDatabase.child("users").child(userId).child("contacts");

    }

    //TODO: Creare un alert dialog (piccola finestra di dialogo)da mostrare in caso di registrazione fallita

    private void showDialog(String title, String message, int icon){

        new AlertDialog.Builder(this)
                .setTitle(title) //setto il titolo
                .setMessage(message) //messaggio
                .setPositiveButton(android.R.string.ok, null) //setto un bottone positivo. prendo l'ok di default di android
                .setIcon(icon) //icona di default
                .show();
    }
}
