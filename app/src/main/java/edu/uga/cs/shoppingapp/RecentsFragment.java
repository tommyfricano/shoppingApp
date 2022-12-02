package edu.uga.cs.shoppingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentsFragment extends Fragment {

    public static final String DEBUG_TAG = "RecentsFragment";


    private RecyclerView recyclerView;
    private PurchaseRecyclerAdapter recyclerAdapter;

    private List<Item> itemsList;
    private List<User> userList;

    private FirebaseDatabase database;


    public RecentsFragment() {
        // Required empty public constructor
    }

    public static RecentsFragment newInstance(String param1, String param2) {
        RecentsFragment fragment = new RecentsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        recyclerView = getView().findViewById( R.id.recyclerView1);

        // initialize the items list
//        itemsList = new ArrayList<Item>();

        userList = new ArrayList<User>();
        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with items is empty at first; it will be updated later
        recyclerAdapter = new PurchaseRecyclerAdapter( userList, getActivity(), getChildFragmentManager() );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("purchased");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        myRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                userList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Log.d( DEBUG_TAG, "ValueEventListener: " + postSnapshot.getValue(User.class));
//                    Item item = postSnapshot.getValue(Item.class);
                    User user = postSnapshot.getValue(User.class);
                    user.setKey( postSnapshot.getKey() );
                    userList.add( user );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + user );
                    Log.d( DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey() );
                }

                Log.d( DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter" );
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }
}