package edu.uga.cs.shoppingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private CartRecyclerAdapter recyclerAdapter;
    private List<String> userList;
    private FirebaseDatabase database;

    public CostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CostFragment newInstance(String param1, String param2) {
        CostFragment fragment = new CostFragment();
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
        return inflater.inflate(R.layout.fragment_cost, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);

//        recyclerView = getView().findViewById( R.id.recyclerView1);
//
//        // initialize the items list
//        userList = new ArrayList<String>();
//
//        // use a linear layout manager for the recycler view
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//
//        // the recycler adapter with items is empty at first; it will be updated later
//        recyclerAdapter = new CartRecyclerAdapter( userList, getActivity(), getChildFragmentManager());
//        recyclerView.setAdapter( recyclerAdapter );
//
//        // get a Firebase DB instance reference
       database = FirebaseDatabase.getInstance();
       DatabaseReference myRef = database.getReference("cart");
       Button button = getView().findViewById(R.id.button2);
       button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
       });
       Button costBtn = getView().findViewById(R.id.button5);
       costBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
       });
    }
}