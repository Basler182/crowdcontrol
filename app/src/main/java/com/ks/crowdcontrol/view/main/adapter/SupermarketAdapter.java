package com.ks.crowdcontrol.view.main.adapter;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ks.crowdcontrol.R;
import com.ks.crowdcontrol.database.SupermarketDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Adapter for the Recycler View to display the Orders.
 */
public class SupermarketAdapter extends RecyclerView.Adapter<SupermarketAdapter.SupermarketHolder> {
    private Context context;
    private ArrayList<SupermarketDTO> supermarketDTOS;
    private Location location;
    private SuperMarketListener listener;

    public SupermarketAdapter(Context context, ArrayList<SupermarketDTO> supermarketDTOS, Location location, SuperMarketListener listener) {
        this.context = context;
        this.supermarketDTOS = supermarketDTOS;
        this.location = location;
        this.listener = listener;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @NonNull
    @Override
    public SupermarketHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.market_view_item, parent, false);
        return new SupermarketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupermarketHolder holder, int position) {
        SupermarketDTO order = supermarketDTOS.get(position);
        double distance = 0;
       /* if (location != null) {
            distance = OrderHandler.getDistance(location.getLatitude(),
                    location.getLongitude(), order.getLatitude(), order.getLongitude()) / 1000;
        }*/
        holder.setDetails(order, distance);
    }

    @Override
    public int getItemCount() {
        return supermarketDTOS.size();
    }


    /**
     * Sets the Order specific Text to each item and adds an On Click Listener
     */
    class SupermarketHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View view;
        private TextView supermarketID;
        private TextView supermarketClientName;
        private TextView supermarketClientAddress;
        private ImageView supermarketIcon;
        private TextView supermarketDistance;

        SupermarketHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(this);

            supermarketID = itemView.findViewById(R.id.supermarket_id);
            supermarketClientName = itemView.findViewById(R.id.supermarket_client_name);
            supermarketClientAddress = itemView.findViewById(R.id.supermarket_client_address);
            supermarketIcon = itemView.findViewById(R.id.supermarket_icon);
            supermarketDistance = itemView.findViewById(R.id.supermarket_distance);
        }

        void setDetails(SupermarketDTO supermarketDTO, double distance) {
            Context context = view.getContext();

            // Build extras
            //String typeText = context.getString(order.getType().getTitle());

            //final int urgencyColor = order.getUrgency().getColor(context);

            view.setTag(supermarketDTO.getId());
            //orderId.setTextColor(urgencyColor);
            supermarketID.setText(String.valueOf(supermarketDTO.getListId()));
            supermarketClientAddress.setText(supermarketDTO.getShortAddress());
            supermarketClientName.setText(supermarketDTO.getName());
            supermarketIcon.setImageResource(supermarketDTO.getType().getIcon());
            //supermarketIcon.setImageTintList(ColorStateList.valueOf(urgencyColor));
            //supermarketDistance.setText(context.getString(R.string.home_order_distance_km, distance));
        }

        @Override
        public void onClick(View v) {
            final String orderId = (String) v.getTag();
            if (listener != null) {
                listener.onSupermarketClicked(orderId);
                Log.wtf("WTF", orderId);
            }
        }
    }

    /**
     * Joins the strings from the list using the given delimiter.
     *
     * @param delimiter The delimiter is placed in between the elements.
     * @param strings   The elements to join to one string.
     * @return The joined elements in a single string.
     */
    private String joinStrings(String delimiter, List<String> strings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return String.join(delimiter, strings);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < strings.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(strings.get(i));
            }
            return sb.toString();
        }
    }

    public interface SuperMarketListener {
        /**
         * Called when the specified order has been clicked.
         *
         * @param orderId The id of the order that has been clicked.
         */
        void onSupermarketClicked(String orderId);
    }
}