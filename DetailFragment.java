package com.zybooks.inventoryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {

    public interface ItemListener {
        void handleEdited(Item original, Item edited);
        void handleDeleted(Item deleted);
    }
    private static final List<ItemListener> listeners = new ArrayList<>();
    private Item mItem;
    public static final String ARG_ITEM_ID = "item_id";

    public DetailFragment() {
    }

    public static void register(ItemListener listener) {listeners.add(listener);}

    public static void remove(ItemListener listener) {listeners.remove(listener);}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int itemId = 1;

        // Get the item ID from the fragment arguments
        Bundle args = getArguments();
        if (args != null) {
            itemId = args.getInt(ARG_ITEM_ID);
        }

        // Get the selected item
        mItem = Inventory.getInstance(requireContext()).getItem(itemId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        TextView nameTextView = rootView.findViewById(R.id.item_name);
        nameTextView.setText(mItem.getName());

        TextView descriptionTextView = rootView.findViewById(R.id.item_description);
        descriptionTextView.setText(mItem.getDescription());

        TextView quantityTextView = rootView.findViewById(R.id.item_quantity);
        quantityTextView.setText(" - " + mItem.getQuantity());


        Button editButton = rootView.findViewById(R.id.edit_button);

        editButton.setOnClickListener(l -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Edit Item");

            View editView = inflater.inflate(R.layout.item_input, null);
            builder.setView(editView);

            EditText txtDescription = editView.findViewById(R.id.desc_entry);
            txtDescription.setText(mItem.getDescription());
            EditText txtName = editView.findViewById(R.id.name_entry);
            txtName.setText(mItem.getName());
            EditText txtQty = editView.findViewById(R.id.qty_entry);
            txtQty.setText(mItem.getQuantity());

            builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                String name = txtName.getText().toString();
                String description = txtDescription.getText().toString();
                String quantity = txtQty.getText().toString();
                Item edited = new Item(mItem.getId(), name, description, quantity);

                boolean isEdited = Inventory.getInstance(getContext()).editItem(mItem.getId(), edited);

                if (!isEdited) {
                    Toast.makeText(getContext(), "Error editing item", Toast.LENGTH_SHORT).show();
                } else {
                    listeners.forEach(listener -> listener.handleEdited(mItem, edited));
                    mItem = edited;
                    nameTextView.setText(edited.getName());
                    descriptionTextView.setText(edited.getDescription());
                    quantityTextView.setText(" - " + edited.getQuantity());
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.create();

            builder.show();
        });

        Button deleteButton = rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(l -> {
            boolean isDeleted = Inventory.getInstance(getContext()).deleteItem(mItem.getId());

            if (!isDeleted) {
                Toast.makeText(getContext(), "Error deleting band", Toast.LENGTH_SHORT).show();
            } else {
                listeners.forEach(listener -> listener.handleDeleted(mItem));
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }
}