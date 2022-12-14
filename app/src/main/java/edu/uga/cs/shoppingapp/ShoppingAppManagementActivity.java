package edu.uga.cs.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.uga.cs.shoppingapp.NavBarFragments.PurchasesFragment;
import edu.uga.cs.shoppingapp.User.CostFragment;
import edu.uga.cs.shoppingapp.User.ListFragment;
import edu.uga.cs.shoppingapp.User.Purchase;

public class ShoppingAppManagementActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private static final String DEBUG_TAG = "ManagementActivity";

    private TextView signedInTextView;
    BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_app_management);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.cart);
        Log.d( DEBUG_TAG, "ShoppingAppManagementActivity.onCreate()" );


        // Setup a listener for a change in the sign in status (authentication status change)
        // when it is invoked, check if a user is signed in and update the UI text view string,
        // as needed.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if( currentUser != null ) {
                    // User is signed in
                    Log.d(DEBUG_TAG, "onAuthStateChanged:signed_in:" + currentUser.getUid());
//                    String userEmail = currentUser.getEmail();
//                    signedInTextView.setText( "Signed in as: " + userEmail );
                } else {
                    // User is signed out
                    Log.d( DEBUG_TAG, "onAuthStateChanged:signed_out" );
                }
            }
        });
    }

    ListFragment listFragment = new ListFragment();
    PurchasesFragment purchasesFragment = new PurchasesFragment();
    CostFragment costFragment = new CostFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cart:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, listFragment).commit();
                return true;

            case R.id.recents:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, purchasesFragment).commit();
                return true;

            case R.id.pay:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, costFragment).commit();
                return true;
        }
        return false;
    }

    // These activity callback methods are not needed and are for edational purposes only
    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "ShoppingApp: ManagementActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "ShoppingApp: ManagementActivity.onResume()" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "ShoppingApp: ManagementActivity.onPause()" );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "ShoppingApp: ManagementActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "ShoppingApp: ManagementActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "ShoppingApp: ManagementActivity.onRestart()" );
        super.onRestart();
    }
}

