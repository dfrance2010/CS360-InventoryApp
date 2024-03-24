package com.zybooks.inventoryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListFragment extends Fragment implements DetailFragment.ItemListener {

    private ItemAdapter adapter;

    // Handle edited item from DetailFragment
    @Override
    public void handleEdited(Item original, Item edited) {
        adapter.editItem(original, edited);
    }

    // Handle deleted item from DetailFragment
    @Override
    public void handleDeleted(Item deleted) {
        adapter.deleteItem(deleted);
    }

    public ListFragment() {DetailFragment.register(this);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Click listener for the RecyclerView
        View.OnClickListener onClickListener = itemView -> {

            // Create fragment arguments containing the selected item ID
            int selectedItemId = (int) itemView.getTag();
            Bundle args = new Bundle();
            args.putInt(DetailFragment.ARG_ITEM_ID, selectedItemId);

            // Replace list with details
            Navigation.findNavController(itemView).navigate(R.id.show_item_detail, args);
        };

        // Send bands to RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Item> items = Inventory.getInstance(requireContext())
                .getItems(AuthenticatedUserManager.getInstance().getUser());
        adapter = new ItemAdapter(items, onClickListener);
        recyclerView.setAdapter(adapter);


        FloatingActionButton fab = view.findViewById(R.id.floating_action_button);

        // Bring up dialog to add item, if item name is already in use, bring up alert message,
        // otherwise add item to inventory database.
        fab.setOnClickListener(l -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Add Item");

            View editView = inflater.inflate(R.layout.item_input, null);
            builder.setView(editView);

            EditText txtName = editView.findViewById(R.id.name_entry);
            EditText txtDescription = editView.findViewById(R.id.desc_entry);
            EditText txtQty = editView.findViewById(R.id.qty_entry);

            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                String name = txtName.getText().toString();
                String description = txtDescription.getText().toString();
                String quantity = txtQty.getText().toString();
                Item item = new Item(name, description, quantity);
                AuthenticatedUser authUser = AuthenticatedUserManager.getInstance().getUser();
                int result = Inventory.getInstance(getContext()).addItem(item, authUser);

                if (result == -1) {
                    Toast.makeText(getContext(), "Item already exists", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.addItem(new Item(result, item));
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.create();

            builder.show();
        });

        return view;
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

        private final List<Item> mItems;
        private final View.OnClickListener mOnClickListener;

        public ItemAdapter(List<Item> items, View.OnClickListener onClickListener) {
            mItems = items;
            mOnClickListener = onClickListener;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Item item = mItems.get(position);
            holder.bind(item);
            holder.itemView.setTag(item.getId());
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        // Add item to recycler view
        public void addItem(Item item) {
            mItems.add(item);
            notifyItemInserted(mItems.size() - 1);
        }

        // Edit item in recycler view
        public void editItem(Item original, Item edited) {
            int index = mItems.indexOf(original);

            if (index == -1) {
                return;
            }
            mItems.add(index, edited);
            mItems.remove(original);
            notifyItemChanged(index);
        }

        // Delete item from recycler view
        public void deleteItem(Item item) {
            int index = mItems.indexOf(item);
            mItems.remove(item);
            notifyItemRemoved(index);
        }

    }

    private static class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView mNameTextView;
        private final TextView mQtyTextView;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            mNameTextView = itemView.findViewById(R.id.item_name);
            mQtyTextView = itemView.findViewById(R.id.item_quantity);
        }

        public void bind(Item item) {
            mNameTextView.setText(item.getName());
            mQtyTextView.setText(item.getQuantity());
        }
    }

}