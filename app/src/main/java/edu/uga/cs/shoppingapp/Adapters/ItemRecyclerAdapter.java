package edu.uga.cs.shoppingapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.shoppingapp.Dialogs.EditItemDialogFragment;
import edu.uga.cs.shoppingapp.Item.Item;
import edu.uga.cs.shoppingapp.R;

/**
 * This is an adapter class for the RecyclerView to show all items.
 */
public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemHolder> {

    public static final String DEBUG_TAG = "RecyclerAdapter";

    private List<Item> itemList;
    private Context context;
    private FragmentManager child;
    private String userEmail;

    public ItemRecyclerAdapter(List<Item> itemList, Context context, FragmentManager child) {
        this.itemList = itemList;
        this.context = context;
        this.child = child;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ItemHolder extends RecyclerView.ViewHolder {

        TextView item;
        TextView creator;

        public ItemHolder(View itemView ) {
            super(itemView);
            creator = itemView.findViewById( R.id.itemName2 );
            item = itemView.findViewById( R.id.itemName );
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.item, parent, false );
        return new ItemHolder( view );
    }

    // This method fills in the values of the Views to show an item
    @Override
    public void onBindViewHolder( ItemHolder holder, int position ) {
        Item item = itemList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + item );

        String key = item.getKey();
        String creatorText = "Creator: " + item.getCreator();

        holder.item.setText( item.getName());
        holder.creator.setText(creatorText);

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditItemDialogFragment editItemFragment =
                        EditItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item.getName(), item.getCreator(), null );
                editItemFragment.show( child, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
