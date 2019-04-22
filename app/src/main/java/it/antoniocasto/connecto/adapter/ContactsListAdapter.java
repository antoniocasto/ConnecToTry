package it.antoniocasto.connecto.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private ArrayList<DataSnapshot> mDataSnaphot;

    //Definisco una variabile childeventlistener
    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded( DataSnapshot dataSnapshot,  String s) {
            //quando viene aggiunto un nuovo figlio

            mDataSnaphot.add(dataSnapshot);
            Log.i("ContactsActivity", Integer.toString(mDataSnaphot.size()));
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //quando un figlio cambia
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            //figlio rimosso
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //Costruttore dell'adapter
    public ContactsListAdapter(Activity activity, DatabaseReference ref){
        mActivity = activity;
        mDatabaseReference = ref.child("users");
        mDataSnaphot = new ArrayList<>();

        mDatabaseReference.addChildEventListener(mListener);
    }

    @Override
    public ContactsViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.contact_row, parent, false);
        ContactsViewHolder vh = new ContactsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        //Riempio il viewholder con i dati provenienti dal database

        DataSnapshot snapshot = mDataSnaphot.get(position);

        User utente = snapshot.getValue(User.class);

        holder.username.setText(utente.getUsername());
        holder.email.setText(utente.getEmail());


    }

    @Override
    public int getItemCount() {
        return mDataSnaphot.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        TextView email;
        LinearLayout.LayoutParams params;

        public ContactsViewHolder( View itemView) {
            super(itemView);

            //inizializzo elementi del ViewHolder
            username = (TextView) itemView.findViewById(R.id.tv_contact_row_username);
            email = (TextView) itemView.findViewById(R.id.tv_contact_row_email);
            params = (LinearLayout.LayoutParams) username.getLayoutParams();
        }
    }

    public void clean(){
        mDatabaseReference.removeEventListener(mListener);
    }
}
