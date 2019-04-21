package it.antoniocasto.connecto.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import it.antoniocasto.connecto.R;
import it.antoniocasto.connecto.model.User;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactsViewHolder>{

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private String mEmail;
    private ArrayList<DataSnapshot> mDataSnapshot; //istanza che contiene dati provenienti da Firebase. E' il tipo di dato che usa firebase quando li manda

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //quando viene aggiunto un nuovo figlio -> messaggio

            mDataSnapshot.add(dataSnapshot);
            notifyDataSetChanged(); //notifico
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ContactsListAdapter(Activity activity, DatabaseReference ref, String name, String email){
        mActivity = activity;
        mEmail = email;
        mDatabaseReference = ref.child("users").child(email.replace('@', '+').replace('.', ':')).child("contacts");
        mDisplayName = name;
        mDataSnapshot = new ArrayList<>(); //creo un array list vuoto in cui salvo la lista dei contatti
        mDatabaseReference.addChildEventListener(mListener);
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.contact_row, parent, false);
        ContactsViewHolder vh = new ContactsViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        DataSnapshot snapshot = mDataSnapshot.get(position);

        User utente = snapshot.getValue(User.class);

        holder.email.setText(utente.getUserEmail());
        holder.username.setText(utente.getUserUsername());

    }

    public int getItemCount() {
        return mDataSnapshot.size();
    }

    public void clean(){
        mDatabaseReference.removeEventListener(mListener);
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        TextView email;
        LinearLayout.LayoutParams params; //per gestire il layout

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            //inizializzo elementi del ViewHolder
            username = itemView.findViewById(R.id.tv_contanct_row_username);
            email = itemView.findViewById(R.id.tv_contact_row_email);
            params = (LinearLayout.LayoutParams) username.getLayoutParams();
        }
    }
}
