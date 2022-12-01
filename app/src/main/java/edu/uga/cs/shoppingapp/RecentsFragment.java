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
    private CartRecyclerAdapter recyclerAdapter;

    private List<Item> itemsList;

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
        itemsList = new ArrayList<Item>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with items is empty at first; it will be updated later
        recyclerAdapter = new CartRecyclerAdapter( itemsList, getActivity(), getChildFragmentManager());
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("cart");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                itemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Log.d( DEBUG_TAG, "ValueEventListener: " + postSnapshot.getValue(Item.class) );
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

//    public void updateCartItem( int position, Item item, int action ) {
//        if( action == EditCartItemDialogFragment.SAVE ) {
//            Log.d( DEBUG_TAG, "Updating item at: " + position + "(" + item.getName() + ")" );
//
//            // Update the recycler view to show the changes in the updated item in that view
//            recyclerAdapter.notifyItemChanged( position );
//
//            // Update this job lead in Firebase
//            // Note that we are using a specific key (one child in the list)
//            DatabaseReference ref = database
//                    .getReference()
//                    .child( "cart" )
//                    .child( item.getKey() );
//
//            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
//            // to maintain items.
//            ref.addListenerForSingleValueEvent( new ValueEventListener() {
//                @Override
//                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
//                    dataSnapshot.getRef().setValue( item ).addOnSuccessListener( new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d( DEBUG_TAG, "updated item at: " + position + "(" + item.getName() + ")" );
//                            Toast.makeText(getActivity(), "item updated for " + item.getName(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled( @NonNull DatabaseError databaseError ) {
//                    Log.d( DEBUG_TAG, "failed to update item at: " + position + "(" + item.getName() + ")" );
//                    Toast.makeText(getActivity(), "Failed to update " + item.getName(),
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        else if( action == EditCartItemDialogFragment.DELETE ) {
//
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference("items");
//
//            // First, a call to push() appends a new node to the existing list (one is created
//            // if this is done for the first time).  Then, we set the value in the newly created
//            // list node to store the new item.
//            // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
//            myRef.push().setValue( item )
//                    .addOnSuccessListener( new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            // Reposition the RecyclerView to show the JobLead most recently added (as the last item on the list).
//                            // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
//                            // reposition the item into view (show the last item on the list).
//                            // the post method adds the argument (Runnable) to the message queue to be executed
//                            // by Android on the main UI thread.  It will be done *after* the setAdapter call
//                            // updates the list items, so the repositioning to the last item will take place
//                            // on the complete list of items.
//                            recyclerView.post( new Runnable() {
//                                @Override
//                                public void run() {
//                                    recyclerView.smoothScrollToPosition( itemsList.size()-1 );
//                                }
//                            } );
//
//                            Log.d( DEBUG_TAG, "item saved: " + item );
//                            // Show a quick confirmation
////                            Toast.makeText( getActivity() , "Item" + delItem.getName(),
////                                    Toast.LENGTH_SHORT).show();
//
//                        }
//                    })
//                    .addOnFailureListener( new OnFailureListener() {
//                        @Override
//                        public void onFailure( @NonNull Exception e ) {
////                            Toast.makeText( getActivity(), "Failed to create a item for " + delItem.getName(),
////                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//
//            Log.d( DEBUG_TAG, "Deleting item at: " + position + "(" + item.getName() + ")" );
//
//            // remove the deleted item from the list (internal list in the App)
//            itemsList.remove( position );
//
//            // Update the recycler view to remove the deleted item from that view
//            recyclerAdapter.notifyItemRemoved( position );
//
//            // Delete this item in Firebase.
//            // Note that we are using a specific key (one child in the list)
//            DatabaseReference ref = database
//                    .getReference()
//                    .child( "cart" )
//                    .child( item.getKey() );
//
//            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
//            // to maintain items.
//            ref.addListenerForSingleValueEvent( new ValueEventListener() {
//                @Override
//                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
//                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d( DEBUG_TAG, "deleted item at: " + position + "(" + item.getName() + ")" );
//                            Toast.makeText(getActivity(), "item removed from cart: " + item.getName(),
//                                    Toast.LENGTH_SHORT).show();                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled( @NonNull DatabaseError databaseError ) {
//                    Log.d( DEBUG_TAG, "failed to remove item from cart: " + position + "(" + item.getName() + ")" );
//                    Toast.makeText(getActivity(), "Failed to remove item from cart: " + item.getName(),
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
}