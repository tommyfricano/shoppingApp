package edu.uga.cs.shoppingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all job leads.
 */
public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemHolder> {

    public static final String DEBUG_TAG = "JobLeadRecyclerAdapter";

    private List<Item> itemList;
    private Context context;

    public ItemRecyclerAdapter(List<Item> itemList, Context context ) {
        this.itemList = itemList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ItemHolder extends RecyclerView.ViewHolder {

        TextView item;
//        TextView phone;
//        TextView url;
//        TextView comments;

        public ItemHolder(View itemView ) {
            super(itemView);

            item = itemView.findViewById( R.id.itemName);
//            phone = itemView.findViewById( R.id.phone );
//            url = itemView.findViewById( R.id.url );
//            comments = itemView.findViewById( R.id.comments );
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.item, parent, false );
        return new ItemHolder( view );
    }

    // This method fills in the values of the Views to show a JobLead
    @Override
    public void onBindViewHolder( ItemHolder holder, int position ) {
        Item item = itemList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + item );

        String key = item.getKey();
        String company = item.getName();
//        String phone = jobLead.getPhone();
//        String url = jobLead.getUrl();
//        String comments = jobLead.getComments();

        holder.item.setText( item.getName());
//        holder.phone.setText( jobLead.getPhone() );
//        holder.url.setText( jobLead.getUrl() );
//        holder.comments.setText( jobLead.getComments() );

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the job leads shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditJobLeadDialogFragment.
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
//                //Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
//                EditItemDialogFragment editJobFragment =
//                        EditItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item );
//                editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
