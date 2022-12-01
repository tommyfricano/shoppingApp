package edu.uga.cs.shoppingapp;

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

/**
 * This is an adapter class for the RecyclerView to show all items.
 */
public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ItemHolder> {

    public static final String DEBUG_TAG = "CartRecyclerAdapter";

    private List<Item> itemList;
    private Context context;
    private FragmentManager child;

    public CartRecyclerAdapter(List<Item> itemList, Context context, FragmentManager child ) {
        this.itemList = itemList;
        this.context = context;
        this.child = child;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ItemHolder extends RecyclerView.ViewHolder {

        TextView item;
        TextView creator;
        TextView buyer;
        TextView cost;

        public ItemHolder(View itemView ) {
            super(itemView);

            cost = itemView.findViewById(R.id.itemName4);
//            buyer = itemView.findViewById(R.id.itemName3);
            creator = itemView.findViewById(R.id.itemName2);
            item = itemView.findViewById( R.id.itemName);
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.cart_item, parent, false );
        return new ItemHolder( view );
    }

    // This method fills in the values of the Views to show an item
    @Override
    public void onBindViewHolder( ItemHolder holder, int position ) {
        Item item = itemList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + item );

        String key = item.getKey();

        String creatorText = "Created by: " + item.getCreator();
//        String buyerText = "Purchased by: "+ item.getBuyer();
        String costText = "$ " + item.getCost();

        holder.item.setText( item.getName());
        holder.creator.setText(creatorText);
//        holder.buyer.setText(buyerText);
        holder.cost.setText(costText);

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                add fragment to items in user list?
                EditCartItemDialogFragment editCartItemFragment =
                        EditCartItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item.getName(), item.getCreator(), item.getBuyer(), item.getCost() );
                editCartItemFragment.show( child, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
