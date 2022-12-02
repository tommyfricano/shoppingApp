package edu.uga.cs.shoppingapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.uga.cs.shoppingapp.Item.Item;
import edu.uga.cs.shoppingapp.R;

// A DialogFragment class to handle job lead additions from the job lead review activity
// It uses a DialogFragment to allow the input of a new job lead.
public class AddItemDialogFragment extends DialogFragment {

    private EditText itemView;
    private String userEmail;

    public interface AddItemDialogListener {
        void addItem(Item item);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_item_dialog, getActivity().findViewById(R.id.root));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();

        // get the view objects in the AlertDialog
        itemView = layout.findViewById( R.id.editText1);

        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        // Set its view (inflated above).
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "New Item" );
        // Provide the negative button listener
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });
        // Provide the positive button listener
        builder.setPositiveButton( android.R.string.ok, new AddItemListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }


    private class AddItemListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String addedItem = itemView.getText().toString();

            Item item = new Item( addedItem, 0.0, userEmail,null );

            AddItemDialogListener listener = (AddItemDialogListener) getParentFragment();

            listener.addItem( item );

            // close the dialog
            dismiss();
        }
    }
}
