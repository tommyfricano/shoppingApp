package edu.uga.cs.shoppingapp.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.shoppingapp.Adapters.CartRecyclerAdapter;
import edu.uga.cs.shoppingapp.Dialogs.EditCartItemDialogFragment;
import edu.uga.cs.shoppingapp.MainActivity;
import edu.uga.cs.shoppingapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CostFragment extends Fragment {

    public static final String DEBUG_TAG = "CostFragment";

    private List<User> userList;
    private FirebaseDatabase database;


    public CostFragment() {
        // Required empty public constructor
    }

    public static CostFragment newInstance() {
        CostFragment fragment = new CostFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cost, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);

        userList = new ArrayList<User>();

        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("purchased");
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
             //   userList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Log.d( DEBUG_TAG, "ValueEventListener: " + postSnapshot.getValue(User.class));
                    User user = postSnapshot.getValue(User.class);
                    user.setKey( postSnapshot.getKey() );
       /*             boolean copy = false;
                    for (int i = 0; i < userList.size(); i++) {
                        if (user.getEmail().equals(userList.get(i).getEmail())) {
                            copy = true;
                        }
                        System.out.println(copy);
                    }*/
                    userList.add( user );

                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );

        for(int i=0;i<userList.size();i++){
            Log.d( DEBUG_TAG, "item:" + userList.get(i).getEmail() +"spent: "+userList.get(i).getSpent());
        }


//        Todo: all values are in the userlist



//      get a Firebase DB instance reference
//       database = FirebaseDatabase.getInstance();
       DatabaseReference myRef = database.getReference("cart");
       Button button = getView().findViewById(R.id.button2);
       button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
       });
       Button costBtn = getView().findViewById(R.id.button5);
       TextView costs = getView().findViewById(R.id.textView3);

       costBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String results = "";
                double average = 0;
                int numOfUsers = 0;
                DatabaseReference ref;

                for (int i = 0; i < userList.size(); i++) {
                    for (int j = i + 1; j < userList.size(); j++) {
                        System.out.println("User " + i + " " + userList.get(i).getEmail());
                        if (userList.get(i).getEmail().equals(userList.get(j).getEmail())) {
                            userList.get(i).setSpent(userList.get(i).getSpent() + userList.get(j).getSpent());
                            userList.get(j).setEmail("bad");
                        }


                    }
                    if (!userList.get(i).getEmail().equals("bad")) {
                        numOfUsers++;
                        average += userList.get(i).getSpent();
                        results += (userList.get(i).
                                getEmail()
                                + " spent $"
                                + userList.get(i)
                                .getSpent()
                                + "\n"
                        );
                    }
                    database
                            .getReference()
                            .child( "purchased/")
                            .child( userList.get(i).getKey())
                            .removeValue();
                }
                results += "Average money spent: $" + average / numOfUsers;
                costs.setText(results);
            }
       });
    }

}