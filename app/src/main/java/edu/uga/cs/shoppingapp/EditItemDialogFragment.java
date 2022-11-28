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
public class EditItemDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing item
    public static final int DELETE = 2; // delete an existing item

    private EditText itemView;

    int position;     // the position of the edited item on the list of items
    String item;
    String key;

    FragmentManager frag;

    // A callback listener interface to finish up the editing of a item
    // ReviewItemActivity implements this listener interface, as it will
    // need to update the list of JobLeads and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditItemDialogListener {
        void updateItem(int position, Item item, int action);
    }

    public static EditItemDialogFragment newInstance(int position, String key, String item ) {
        EditItemDialogFragment dialog = new EditItemDialogFragment();

        // Supply item values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("item", item);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        item = getArguments().getString( "item" );
        frag = getParentFragmentManager();


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.add_item_dialog, getActivity().findViewById( R.id.root ) );

        itemView = layout.findViewById( R.id.editText1 );

        // Pre-fill the edit texts with the current values for this item.
        // The user will be able to modify them.
        itemView.setText( item );

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

        // The Save button handler
        builder.setPositiveButton( "SAVE", new SaveButtonClickListener() );

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String item = itemView.getText().toString();
            Item dbItem = new Item(item, 0.0);
            dbItem.setKey( key );

            // get the Activity's listener to add the new item
            EditItemDialogListener listener = (EditItemDialogFragment.EditItemDialogListener) getParentFragment();
            listener.updateItem( position, dbItem, SAVE );

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            Item delItem = new Item(item, 0.0);
            delItem.setKey( key );

            Log.d("Edit Item", String.valueOf(frag));

            // get the Activity's listener to add the new item
            EditItemDialogFragment.EditItemDialogListener listener = (EditItemDialogFragment.EditItemDialogListener) getParentFragment();            // add the new job lead
            listener.updateItem( position, delItem, DELETE );
            // close the dialog
            dismiss();
        }
    }
}
