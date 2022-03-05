package com.anjaniy.banglorehomepricepredictor;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;

import com.anjaniy.banglorehomepricepredictor.fragments.Predictor;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar main_toolbar;
    private Toolbar nav_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        widgetSetup();

        //WILL GET MESSAGE FRAGMENT AS DEFAULT FRAGMENT.
        if(savedInstanceState == null){
            main_toolbar.setTitle("Predictor");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Predictor()).commit();
            navigationView.setCheckedItem(R.id.predictor);
        }
    }

    private void widgetSetup() {
        //CUSTOM - TOOLBAR: -
        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);
        nav_toolbar = (Toolbar) findViewById(R.id.nav_toolbar);
        setSupportActionBar(nav_toolbar);

        navigationView = (NavigationView)findViewById(R.id.nav_menu);
        drawerLayout = (DrawerLayout)findViewById(R.id.Drawer_Layout);
        drawerLayout.setFitsSystemWindows(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,main_toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    //ON_BACK_PRESSED() - METHOD: -
    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        else {
            exitApp();
        }
    }

    private void exitApp() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Do You Want To Close This App?")

                .setCancelable(false)

                //CODE FOR POSITIVE(YES) BUTTON: -
                .setPositiveButton("Yes", (dialog, which) -> {
                    //ACTION FOR "YES" BUTTON: -
                    finishAffinity();
                })

                //CODE FOR NEGATIVE(NO) BUTTON: -
                .setNegativeButton("No", (dialog, which) -> {
                    //ACTION FOR "NO" BUTTON: -
                    dialog.cancel();

                });

        //CREATING A DIALOG-BOX: -
        AlertDialog alertDialog = builder.create();
        //SET TITLE MAUALLY: -
        alertDialog.setTitle("Exit");
        alertDialog.show();
    }
}