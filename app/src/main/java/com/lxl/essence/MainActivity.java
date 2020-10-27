package com.lxl.essence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

    }
    private NavController controller() {
        return Navigation.findNavController(this, R.id.nav_host_fragment);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavDestination currentDestination = controller().getCurrentDestination();
        return controller().navigateUp();
    }
}