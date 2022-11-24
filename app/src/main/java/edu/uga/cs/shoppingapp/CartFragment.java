package edu.uga.cs.shoppingapp;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements AddItemDialogFragment.AddItemDialogListener{
//        EditJobLeadDialogFragment.EditJobLeadDialogListener{

    public static final String DEBUG_TAG = "ReviewJobLeadsActivity";

    private RecyclerView recyclerView;
    private ItemRecyclerAdapter recyclerAdapter;

    private List<Item> itemsList;

    private FirebaseDatabase database;

    private View cartView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d( DEBUG_TAG, "onCreate()" );
        // Inflate the layout for this fragment
        return cartView = inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        recyclerView = getView().findViewById( R.id.recyclerView );

        FloatingActionButton floatingButton = (FloatingActionButton) getView().findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddItemDialogFragment();
                newFragment.show( getChildFragmentManager(), null);
            }
        });

        // initialize the Job Lead list
        itemsList = new ArrayList<Item>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with job leads is empty at first; it will be updated later
        recyclerAdapter = new ItemRecyclerAdapter( itemsList, getActivity());
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("items");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
        // to maintain job leads.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                itemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Item item = postSnapshot.getValue(Item.class);
                    item.setKey( postSnapshot.getKey() );
                    itemsList.add( item );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + item );
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

    public void addItem(Item item) {
        // add the new job lead
        // Add a new element (JobLead) to the list of job leads in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("items");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new job lead.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintain job leads.
        myRef.push().setValue( item )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Reposition the RecyclerView to show the JobLead most recently added (as the last item on the list).
                        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                        // reposition the item into view (show the last item on the list).
                        // the post method adds the argument (Runnable) to the message queue to be executed
                        // by Android on the main UI thread.  It will be done *after* the setAdapter call
                        // updates the list items, so the repositioning to the last item will take place
                        // on the complete list of items.
                        recyclerView.post( new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition( itemsList.size()-1 );
                            }
                        } );

                        Log.d( DEBUG_TAG, "item saved: " + item );
                        // Show a quick confirmation
                        Toast.makeText( getActivity() , "Item created " + item.getName(),
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText( getActivity(), "Failed to create a item for " + item.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    public void updateItem( int position, Item item, int action ) {
//        if( action == EditItemDialogFragment.SAVE ) {
//            Log.d( DEBUG_TAG, "Updating job lead at: " + position + "(" + jobLead.getCompanyName() + ")" );
//
//            // Update the recycler view to show the changes in the updated job lead in that view
//            recyclerAdapter.notifyItemChanged( position );
//
//            // Update this job lead in Firebase
//            // Note that we are using a specific key (one child in the list)
//            DatabaseReference ref = database
//                    .getReference()
//                    .child( "jobleads" )
//                    .child( jobLead.getKey() );
//
//            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
//            // to maintain job leads.
//            ref.addListenerForSingleValueEvent( new ValueEventListener() {
//                @Override
//                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
//                    dataSnapshot.getRef().setValue( jobLead ).addOnSuccessListener( new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d( DEBUG_TAG, "updated job lead at: " + position + "(" + jobLead.getCompanyName() + ")" );
//                            Toast.makeText(getApplicationContext(), "Job lead updated for " + jobLead.getCompanyName(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled( @NonNull DatabaseError databaseError ) {
//                    Log.d( DEBUG_TAG, "failed to update job lead at: " + position + "(" + jobLead.getCompanyName() + ")" );
//                    Toast.makeText(getApplicationContext(), "Failed to update " + jobLead.getCompanyName(),
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        else if( action == EditJobLeadDialogFragment.DELETE ) {
//            Log.d( DEBUG_TAG, "Deleting job lead at: " + position + "(" + jobLead.getCompanyName() + ")" );
//
//            // remove the deleted job lead from the list (internal list in the App)
//            jobLeadsList.remove( position );
//
//            // Update the recycler view to remove the deleted job lead from that view
//            recyclerAdapter.notifyItemRemoved( position );
//
//            // Delete this job lead in Firebase.
//            // Note that we are using a specific key (one child in the list)
//            DatabaseReference ref = database
//                    .getReference()
//                    .child( "jobleads" )
//                    .child( jobLead.getKey() );
//
//            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
//            // to maintain job leads.
//            ref.addListenerForSingleValueEvent( new ValueEventListener() {
//                @Override
//                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
//                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d( DEBUG_TAG, "deleted job lead at: " + position + "(" + jobLead.getCompanyName() + ")" );
//                            Toast.makeText(getApplicationContext(), "Job lead deleted for " + jobLead.getCompanyName(),
//                                    Toast.LENGTH_SHORT).show();                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled( @NonNull DatabaseError databaseError ) {
//                    Log.d( DEBUG_TAG, "failed to delete job lead at: " + position + "(" + jobLead.getCompanyName() + ")" );
//                    Toast.makeText(getApplicationContext(), "Failed to delete " + jobLead.getCompanyName(),
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
}