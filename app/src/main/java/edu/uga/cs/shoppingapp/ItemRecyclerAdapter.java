package edu.uga.cs.shoppingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all items.
 */
public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemHolder> {

    public static final String DEBUG_TAG = "RecyclerAdapter";

    private List<Item> itemList;
    private Context context;
    private FragmentManager child;

    public ItemRecyclerAdapter(List<Item> itemList, Context context, FragmentManager child ) {
        this.itemList = itemList;
        this.context = context;
        this.child = child;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ItemHolder extends RecyclerView.ViewHolder {

        TextView item;
        Button add2Cart;


        public ItemHolder(View itemView ) {
            super(itemView);

            item = itemView.findViewById( R.id.itemName);
            add2Cart = itemView.findViewById(R.id.addToCartBtn);
            add2Cart.setOnClickListener( new AddToCartButtonClickListener());
        }
    }

    private class AddToCartButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Toast.makeText( view.getContext(), "Added to cart",
                    Toast.LENGTH_SHORT).show();
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

        holder.item.setText( item.getName());

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditItemDialogFragment editItemFragment =
                        EditItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item.getName() );
                editItemFragment.show( child, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
