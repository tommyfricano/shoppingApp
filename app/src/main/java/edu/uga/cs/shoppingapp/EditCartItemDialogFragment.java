package edu.uga.cs.shoppingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

// This is a DialogFragment to handle edits to a item.
// The edits are: updates and deletions of existing items.
public class EditCartItemDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing item
    public static final int DELETE = 2; // delete an existing item
    public static final int ADD = 3;    // add an existing item to cart

    private EditText itemView;
    private EditText costView;

    int position;     // the position of the edited item on the list of items
    String item;
    Double cost;
    String key;
    String userEmail;
    String buyer;

    FragmentManager frag;

    // A callback listener interface to finish up the editing of a item
    // ReviewItemActivity implements this listener interface, as it will
    // need to update the list of JobLeads and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditCartItemDialogListener {
        void updateCartItem(int position, Item item, int action);
    }

    public static EditCartItemDialogFragment newInstance(int position, String key, String item, String userEmail, String buyer, double cost) {
        EditCartItemDialogFragment dialog = new EditCartItemDialogFragment();

        // Supply item values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("item", item);
        args.putDouble("cost", cost);
        args.putString("userEmail", userEmail);
        args.putString("buyer", buyer);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        item = getArguments().getString( "item" );
        cost = getArguments().getDouble("cost");
        userEmail = getArguments().getString("userEmail");
        buyer = getArguments().getString("buyer");
        frag = getParentFragmentManager();


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.edit_cart_item_dialog, getActivity().findViewById( R.id.root ) );

        itemView = layout.findViewById(R.id.ItemText);
        costView = layout.findViewById(R.id.CostText);

        // Pre-fill the edit texts with the current values for this item.
        // The user will be able to modify them.

        itemView.setText( item );
        costView.setText( cost.toString() );

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Edit Item" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("SAVE", new SaveButtonClickListener() );

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String item = itemView.getText().toString();
            String cost = costView.getText().toString();
            Double costValue = Double.parseDouble(cost);

            Item dbItem = new Item(item, costValue, userEmail, buyer);
            dbItem.setKey( key );

            // get the Activity's listener to add the new item
            EditCartItemDialogListener listener = (EditCartItemDialogFragment.EditCartItemDialogListener) getParentFragment();
            listener.updateCartItem(position, dbItem, SAVE);

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            Item delItem = new Item(item, 0.0, userEmail, null);
            delItem.setKey( key );

            Log.d("Edit Item", String.valueOf(frag));

            // get the Activity's listener to add the new item
            EditCartItemDialogFragment.EditCartItemDialogListener listener = (EditCartItemDialogFragment.EditCartItemDialogListener) getParentFragment();            // add the new job lead
            listener.updateCartItem( position, delItem, DELETE );
            // close the dialog
            dismiss();
        }
    }

}
