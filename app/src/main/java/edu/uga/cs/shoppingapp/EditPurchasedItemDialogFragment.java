package edu.uga.cs.shoppingapp;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

// This is a DialogFragment to handle edits to a item.
// The edits are: updates and deletions of existing items.
public class EditPurchasedItemDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing item
    public static final int DELETE = 2; // delete an existing item
    public static final int ADD = 3;    // add an existing item to cart

    private TextView itemView;
    private TextView costView;
    private Button btn;

    int position;     // the position of the edited item on the list of items
    String item;
    String key;
    String userEmail;
    String buyer;
    Double cost;
    String creator;

    private boolean textChanged = false;

    FragmentManager frag;

    // A callback listener interface to finish up the editing of a item
    // ReviewItemActivity implements this listener interface, as it will
    // need to update the list of JobLeads and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditPurchasedItemDialogListener {
        void updateItem(int position, Item item, int action);
    }

    public static EditPurchasedItemDialogFragment newInstance(int position, String key, String item, String creator,  String userEmail, Double cost) {
        EditPurchasedItemDialogFragment dialog = new EditPurchasedItemDialogFragment();

        // Supply item values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("item", item);
        args.putString("userEmail", userEmail);
        args.putDouble("cost", cost);
        args.putString("creator", creator);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        item = getArguments().getString( "item" );
        userEmail = getArguments().getString("userEmail");
        cost = getArguments().getDouble("cost");
        creator = getArguments().getString("creator");
        frag = getParentFragmentManager();


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.edit_purchase_item_dialog, getActivity().findViewById( R.id.root ) );

        itemView = layout.findViewById(R.id.ItemText);
        costView = layout.findViewById(R.id.CostText);

        String costStr = "$ " + cost.toString();

        itemView.setText( item );
        costView.setText( costStr );

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "View Item" );

        // The Cancel button handler
        builder.setPositiveButton( "CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            Item delItem = new Item(item, cost, userEmail, null);
            delItem.setKey( key );

            Log.d("Edit Item", String.valueOf(frag));

            // get the Activity's listener to add the new item
            EditPurchasedItemDialogFragment.EditPurchasedItemDialogListener listener = (EditPurchasedItemDialogFragment.EditPurchasedItemDialogListener) getParentFragment();            // add the new job lead
            listener.updateItem( position, delItem, DELETE );
            // close the dialog
            dismiss();
        }
    }

}
