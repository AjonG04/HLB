package com.example.leafpiction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private static final int CAMERA_REQUEST = 1888;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                loadFragment(item.getItemId());
                return true;
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        int id = bottomNavigationView.getSelectedItemId();

        loadFragment(id);
    }

    private void loadFragment(int id){
        Fragment selectedFragment = null;

        switch (id){
            case R.id.home:
                selectedFragment = new HomeFragment();
                break;
            case R.id.history:
                selectedFragment = new HistoryFragment();
                break;
            case R.id.about:
                selectedFragment = new AboutFragment();
                break;
            case R.id.setting:
                selectedFragment = new SettingFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }

    public void loadCameraActivity(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void getImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageuri;
        Bitmap bitmap = null;

        if(requestCode == 12 && resultCode == RESULT_OK && data != null) {
            imageuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(bitmap != null){
            loadGalleryActivity(bitmap);
        }
    }

    private void loadGalleryActivity(Bitmap bitmap){
        Intent intent = new Intent(this, GalleryActivity.class);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        intent.putExtra("photo", byteArray);
        startActivity(intent);
    }

    public void reverseListOrder(View view){
        switch (view.getId()) {
            case R.id.btn_latest:
                editor.putBoolean("defaultOrder", true);
                editor.commit();
                break;
            case R.id.btn_oldest:
                editor.putBoolean("defaultOrder", false);
                editor.commit();
                break;
        }

        Fragment selectedFragment = new HistoryFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }

    public void changeLanguage(View view){

//        String mystring = getResources().getString(R.string.mystring);

        String[] language = {
                "English",
                "Indonesia"
        };

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(getResources().getString(R.string.choose));
        mBuilder.setSingleChoiceItems(language, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    setLocale("en");
                    recreate();
                }
                else if (i == 1){
                    setLocale("id");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        editor.putString("Language", lang);
        editor.commit();
    }

    @Override
    public void recreate() {
        super.recreate();

        bottomNavigationView.setSelectedItemId(R.id.home);
    }
}
