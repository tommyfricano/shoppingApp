package edu.uga.cs.shoppingapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uga.cs.shoppingapp.Adapters.PurchasedItemsRecyclerAdapter;
import edu.uga.cs.shoppingapp.Item.Item;
import edu.uga.cs.shoppingapp.R;
import edu.uga.cs.shoppingapp.User.User;

// This is a DialogFragment to handle edits to a item.
// The edits are: updates and deletions of existing items.
public class PurchasedItemsDialogFragment extends DialogFragment implements EditPurchasedItemDialogFragment.EditPurchasedItemDialogListener{

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing item
    public static final int DELETE = 2; // delete an existing item
    public static final int ADD = 3;    // add an existing item to cart

    private EditText itemView;
    private Button btn;
    public static final String DEBUG_TAG =" PurchaseDialog";


    private RecyclerView recyclerView;
    private PurchasedItemsRecyclerAdapter recyclerAdapter;
    private TextView buyer;
    private EditText costText;

    private ArrayList<Item> itemsList;

    private FirebaseDatabase database;

    int position;     // the position of the edited item on the list of items
    String item;
    String key;
    String userEmail;
    Double cost;
    String userCheck;

    String userId;

    FragmentManager frag;

    public interface PurchasedItemsDialogListener {
        void updateItem(int position, User user, int action);
    }

    // A callback listener interface to finish up the editing of a item
    // ReviewItemActivity implements this listener interface, as it will
    // need to update the list of JobLeads and also update the RecyclerAdapter to reflect the
    // changes.

    public static PurchasedItemsDialogFragment newInstance(int position, String key, String email, double cost) {
        PurchasedItemsDialogFragment dialog = new PurchasedItemsDialogFragment();

        // Supply item values as an argument.
        Bundle args = new Bundle();
        args.putInt( "position", position );
        args.putString("key", key);
        args.putString("email", email);
        args.putDouble("cost", cost);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {

        frag = getParentFragmentManager();
        key = getArguments().getString("key");
        userEmail = getArguments().getString("email");
        cost = getArguments().getDouble("cost");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.purchased_dialog, getActivity().findViewById( R.id.root ) );

        recyclerView = layout.findViewById( R.id.recyclerView1);
        buyer = layout.findViewById(R.id.textView5);
        costText = layout.findViewById(R.id.editText6);

        buyer.setText(userEmail);
        costText.setText(cost.toString());

        // initialize the items list
        itemsList = new ArrayList<Item>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with items is empty at first; it will be updated later
        recyclerAdapter = new PurchasedItemsRecyclerAdapter( itemsList, getActivity(), getChildFragmentManager());
        recyclerView.setAdapter( recyclerAdapter );

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(DEBUG_TAG, "onAuth: " + user.getUid());
        userId = user.getUid();
        userCheck = user.getEmail();

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("purchased/"+key+"/items");

        Log.d(DEBUG_TAG, "onAuth: " );

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                itemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Log.d( DEBUG_TAG, "ValueEventListener: " + postSnapshot.getValue( Item.class ) );

                    Item item = postSnapshot.getValue( Item.class );
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


        // Pre-fill the edit texts with the current values for this item.
        // The user will be able to modify them.

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        builder.setView(layout);
        // Set the title of the AlertDialog
        builder.setTitle( "Purchased items" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });


        builder.setPositiveButton("SAVE", new PurchasedItemsDialogFragment.SaveButtonClickListener());

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(itemsList.size() == 0){
                Toast.makeText(getActivity(), "Must have at least one purchased item to save purchase!",
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
            else if(userEmail.equals(userCheck)) {
                String cost = costText.getText().toString();
                Double spent = Double.parseDouble(cost);
                User dbUser = new User(userEmail, spent, itemsList);
                dbUser.setKey(key);

                // get the Activity's listener to add the new item
                PurchasedItemsDialogFragment.PurchasedItemsDialogListener listener = (PurchasedItemsDialogFragment.PurchasedItemsDialogListener) getParentFragment();
                listener.updateItem(position, dbUser, SAVE);

                // close the dialog
                dismiss();
            }
            else {
                Toast.makeText(getActivity(), "Cannot edit other users purchases",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateItem( int position, Item item, int action ) {
        if( action == EditCartItemDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating item at: " + position + "(" + item.getName() + ")" );

            // Update the recycler view to show the changes in the updated item in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update this job lead in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "purchased/" + key + "/items")
                    .child( item.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain items.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( item ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated item at: " + position + "(" + item.getName() + ")" );
                            Toast.makeText(getActivity(), "item updated for " + item.getName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update item at: " + position + "(" + item.getName() + ")" );
                    Toast.makeText(getActivity(), "Failed to update " + item.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditCartItemDialogFragment.DELETE ) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("items");

                // First, a call to push() appends a new node to the existing list (one is created
                // if this is done for the first time).  Then, we set the value in the newly created
                // list node to store the new item.
                // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
                myRef.push().setValue(item)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                // Reposition the RecyclerView to show the JobLead most recently added (as the last item on the list).
                                // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                                // reposition the item into view (show the last item on the list).
                                // the post method adds the argument (Runnable) to the message queue to be executed
                                // by Android on the main UI thread.  It will be done *after* the setAdapter call
                                // updates the list items, so the repositioning to the last item will take place
                                // on the complete list of items.
                                recyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.smoothScrollToPosition(itemsList.size());
                                    }
                                });

                                Log.d(DEBUG_TAG, "item saved: " + item);
                                // Show a quick confirmation
//                            Toast.makeText( getActivity() , "Item" + delItem.getName(),
//                                    Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                            Toast.makeText( getActivity(), "Failed to create a item for " + delItem.getName(),
//                                    Toast.LENGTH_SHORT).show();
                            }
                        });


                Log.d(DEBUG_TAG, "Deleting item at: " + position + "(" + item.getName() + ")");

                // remove the deleted item from the list (internal list in the App)
                itemsList.remove(position);

                // Update the recycler view to remove the deleted item from that view
                recyclerAdapter.notifyItemRemoved(position);

                // Delete this item in Firebase.
                // Note that we are using a specific key (one child in the list)
                DatabaseReference ref = database
                        .getReference()
                        .child("purchased/" + key + "/items")
                        .child(item.getKey());

                // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
                // to maintain items.
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(DEBUG_TAG, "deleted item at: " + position + "(" + item.getName() + ")");
                                Toast.makeText(getActivity(), "item removed from cart: " + item.getName(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(DEBUG_TAG, "failed to remove item from cart: " + position + "(" + item.getName() + ")");
                        Toast.makeText(getActivity(), "Failed to remove item from cart: " + item.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            if (itemsList.size() == 0) {
                DatabaseReference delref = database
                        .getReference()
                        .child("purchased")
                        .child(key);
                delref.removeValue();
            }
            else {

                recyclerAdapter.notifyItemChanged(position);

                String cost = costText.getText().toString();
                Double spent = Double.parseDouble(cost) - item.getCost();
                User dbUser = new User(userEmail, spent, itemsList);
                dbUser.setKey(key);

                // Update this job lead in Firebase
                // Note that we are using a specific key (one child in the list)
                DatabaseReference newRef = database
                        .getReference()
                        .child("purchased/")
                        .child(dbUser.getKey());

                // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
                // to maintain items.
                newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().setValue(dbUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                            Log.d( DEBUG_TAG, "updated item at: " + position + "(" + user.getEmail() + ")" );
//                            Toast.makeText(getActivity(), "item updated for " + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.d( DEBUG_TAG, "failed to update item at: " + position + "(" + user.getEmail() + ")" );
//                    Toast.makeText(getActivity(), "Failed to update " + user.getEmail(),
//                            Toast.LENGTH_SHORT).show();
                    }
                });
                costText.setText(spent.toString());

            }
        }
    }
}
