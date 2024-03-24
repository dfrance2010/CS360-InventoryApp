package com.zybooks.inventoryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
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
        mItem = Inventory.getInstance(getContext()).getItem(itemId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView nameTextView = view.findViewById(R.id.item_name);
        nameTextView.setText(mItem.getName());

        TextView descriptionTextView = view.findViewById(R.id.item_description);
        descriptionTextView.setText(mItem.getDescription());

        TextView quantityTextView = view.findViewById(R.id.item_quantity);
        quantityTextView.setText(mItem.getQuantity());


        Button editButton = view.findViewById(R.id.edit_button);

        // Dialog for editing item. If quantity updated to 0, bring up alert message for
        // low inventory.
        editButton.setOnClickListener(l -> {

            // When edit button pressed, inflate view to take in new information to enter.
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
                String quantity = txtQty.getText().toString();// If user enters nothing, set quantity to 0
                if (quantity.equals("")) {
                    quantity = "0";
                }
                Item edited = new Item(mItem.getId(), name, description, quantity);

                boolean isEdited = Inventory.getInstance(getContext()).editItem(mItem.getId(), edited);

                if (!isEdited) {
                    Toast.makeText(getContext(), "Error editing item", Toast.LENGTH_SHORT).show();
                } else {
                    listeners.forEach(listener -> listener.handleEdited(mItem, edited));
                    nameTextView.setText(edited.getName());
                    descriptionTextView.setText(edited.getDescription());
                    quantityTextView.setText(edited.getQuantity());


                    // If new quantity is zero and notifications are on, display error message.
                    if (edited.getQuantity().equals("0") &&
                            NotificationsManager.getInstance().getNotificationPreference()) {

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("Low Inventory Alert");

                        View alertView = inflater.inflate(R.layout.low_inventory, null);
                        builder2.setView(alertView);

                        String itemAlert = "You are out of " + edited.getName();
                        TextView alertText = alertView.findViewById(R.id.low_inventory);
                        alertText.setText(itemAlert);

                        builder2.setPositiveButton("OK", null);

                        builder2.create();

                        builder2.show();
                    }
                }

            });

            builder.setNegativeButton("Cancel", null);
            builder.create();

            builder.show();
        });

        // Delete item from inventory
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(l -> {
            boolean isDeleted = Inventory.getInstance(getContext()).deleteItem(mItem.getId());

            if (!isDeleted) {
                Toast.makeText(getContext(), "Error deleting band", Toast.LENGTH_SHORT).show();
            } else {
                listeners.forEach(listener -> listener.handleDeleted(mItem));
                getActivity().onBackPressed();
            }
        });

        // Edit quantity of item by clicking on quantity text
        quantityTextView.setOnClickListener(l -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Update Quantity");

            View qtyView = inflater.inflate(R.layout.edit_quantity, null);
            builder.setView(qtyView);

            EditText txtQty = qtyView.findViewById(R.id.qty_entry);
            txtQty.setText(mItem.getQuantity());

            builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                        String name = mItem.getName();
                        String description = mItem.getDescription();
                        String quantity = txtQty.getText().toString();
                        // If user enters nothing, set quantity to 0
                        if (quantity.equals("")) {
                            quantity = "0";
                        }
                        Item edited = new Item(mItem.getId(), name, description, quantity);

                        boolean isEdited = Inventory.getInstance(getContext()).editItem(mItem.getId(), edited);

                        if (!isEdited) {
                            Toast.makeText(getContext(), "Error editing item", Toast.LENGTH_SHORT).show();
                        } else {
                            listeners.forEach(listener -> listener.handleEdited(mItem, edited));
                            quantityTextView.setText(edited.getQuantity());


                            // If new quantity is zero and notifications are on, display error message.
                            if (edited.getQuantity().equals("0") &&
                                    NotificationsManager.getInstance().getNotificationPreference()) {

                                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                                builder2.setTitle("Low Inventory Alert");

                                View alertView = inflater.inflate(R.layout.low_inventory, null);
                                builder2.setView(alertView);

                                String itemAlert = "You are out of " + edited.getName();
                                TextView alertText = alertView.findViewById(R.id.low_inventory);
                                alertText.setText(itemAlert);

                                builder2.setPositiveButton("OK", null);

                                builder2.create();

                                builder2.show();
                            }
                        }
                    });

            builder.setNegativeButton("Cancel", null);

            builder.create();

            builder.show();
        });

        return view;
    }
}