package com.example.nicecook;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment implements ShoppingListAdapter.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ShoppingListAdapter shoppingListAdapter;
    private ArrayList<ShoppingItem> list;
    private double totalPrice;
    private TextView txtTotalPrice;
    private LinearLayout btnCheckout;
    private ArrayList<ShoppingItem> checkedItem;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingListFragment newInstance(String param1, String param2) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_shopping_list, container, false);
        totalPrice = 0;
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        txtTotalPrice.setText("₱".concat(Double.toString(totalPrice)));
        btnCheckout = view.findViewById(R.id.btnCheckout);
        recyclerView = view.findViewById(R.id.shoppingList);
        databaseReference = FirebaseDatabase.getInstance().getReference("ShoppingList");
        list = new ArrayList<>();
        shoppingListAdapter = new ShoppingListAdapter(getContext(), list, this);
        recyclerView.setAdapter(shoppingListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String userID = FirebaseAuth.getInstance().getUid();
        checkedItem = new ArrayList<>();
        showItems(userID);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedItem.isEmpty()) {
                    Toast.makeText(getContext(), "Nothing to checkout", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(ShoppingItem item : checkedItem) {
                    Query itemQuery = databaseReference.orderByChild("itemID").equalTo(item.getItemID());
                    itemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                itemSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Toast.makeText(getContext(), "Checked out ₱".concat(Double.toString(totalPrice)), Toast.LENGTH_SHORT).show();
                    totalPrice = 0;
                }
                showItems(userID);
            }
        });

        return view;
    }

    private void showItems(String userID) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ShoppingItem shoppingItem = dataSnapshot.getValue(ShoppingItem.class);
                    if(shoppingItem != null && userID != null && userID.equals(shoppingItem.getUserID())) {
                        list.add(shoppingItem);
                    }
                }
                shoppingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemCheckedChanged(boolean isIncrement, double amount, ShoppingItem shoppingItem) {
        if(isIncrement) {
            totalPrice += amount;
            checkedItem.add(shoppingItem);
        } else {
            totalPrice -= amount;
            checkedItem.remove(shoppingItem);
        }
        txtTotalPrice.setText("₱".concat(Double.toString(totalPrice)));
    }
}