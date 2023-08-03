package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    Button logOutButton,findUsButton, contactUsButton;
    TextView nameTextView, emailTextView;
    BottomNavigationView navbar;
    FirebaseUser user;
    DatabaseReference databaseName;
    String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logOutButton = view.findViewById(R.id.logOutButton); //Get Button from view.
        findUsButton = view.findViewById(R.id.findUsButton); //Get Button from view.
        contactUsButton = view.findViewById(R.id.contactUsButton); //Get Button from view.
        nameTextView = view.findViewById(R.id.profileNameTextView); //Get TextView from view.
        emailTextView = view.findViewById(R.id.emailTextView); //Get TextView from view.
        navbar = getActivity().findViewById(R.id.nav_bar); //Get BottomNavigationBar from view.

        //Getting Firebase Realtime Database user, by connecting the application to the Firebase Realtime Database.
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseName = FirebaseDatabase.getInstance().getReference("Users");
        uid = user.getUid();

        //Displaying the user's name on the profile fragment.
        databaseName.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userProfile = snapshot.getValue(User.class); //Snapshotting the data of the user.

                if(userProfile != null){
                    nameTextView.setText(userProfile.name); //Displaying the user's name.
                    emailTextView.setText(userProfile.email); //Displaying the user's email.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //If the process got cancelled, the application will pop up a Toast message.
                Toast.makeText(getActivity(),"Something wrong happened! Please re-login.", Toast.LENGTH_LONG).show();
            }
        });

        //When user click on the findUsButton, the application will direct the user to Google Maps.
        findUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String address = "Jln Damai Impian 5, Alam Damai, 56000 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur";

                String url = "https://www.google.com/maps/search/?api=1&query="+address;

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        //When user click on the contactUsButton, the application will direct the user to the Phone App.
        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);

                intent.setData(Uri.parse("tel:+60189734805"));

                startActivity(intent);
            }
        });

        /*When user click on the logOutButton, the application will pop up a AlertDialog to confirm
          if the user wants to log out or not.
         */
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setMessage("Do you want to log out?").setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(getActivity(),"Log Out Successful!",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(),loginActivity.class));
                            }
                        });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();


            }
        });

        return view;
    }
}