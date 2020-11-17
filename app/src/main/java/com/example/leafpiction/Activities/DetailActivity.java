package com.example.leafpiction.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.transition.TransitionSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leafpiction.Model.PhotoRequestModel;
import com.example.leafpiction.R;
import com.example.leafpiction.UploadTask;
import com.example.leafpiction.Util.HistoryDatabaseCRUD;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {

    ImageView iv_photo;
    TextView tv_cloro, tv_caro, tv_anto;
    Button btn_delete, btn_upload;

    HistoryDatabaseCRUD dbHandler;

    int id, uploaded;
    byte[] photo;
    float kloro, karo, anto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        iv_photo = findViewById(R.id.iv_photo);
        tv_cloro = findViewById(R.id.textkloro);
        tv_anto = findViewById(R.id.textanto);
        tv_caro = findViewById(R.id.textkaro);
        btn_delete = findViewById(R.id.btn_delete);
        btn_upload = findViewById(R.id.btn_upload);

        setView();

        dbHandler = new HistoryDatabaseCRUD();

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.deleteRecord(getApplicationContext(), id);
                finish();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar snackbar = Snackbar.make(view, getResources().getString(R.string.inDevelopment), Snackbar.LENGTH_SHORT);
//                ColorStateList colorPrimary = ContextCompat.getColorStateList(view.getContext(), R.color.colorPrimaryDark);
//                snackbar.setBackgroundTintList(colorPrimary);
//                snackbar.show();

                Log.d("Uploaded Status", String.valueOf(uploaded));

                if(uploaded == 0) {
                    String encodedImage = Base64.encodeToString(photo, Base64.DEFAULT);

                    PhotoRequestModel request = new PhotoRequestModel(encodedImage, id, kloro, karo, anto);
                    UploadTask uploadTask = new UploadTask(DetailActivity.this, request);
                    uploadTask.execute();

                    uploaded = 1;
                }
                else{
                    Toast.makeText(DetailActivity.this, getResources().getString(R.string.uploaded),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setView() {
        Bundle extras = getIntent().getExtras();
        photo = extras.getByteArray("photo");
        id = extras.getInt("id");
        kloro = extras.getFloat("cloro");
        karo = extras.getFloat("caro");
        anto = extras.getFloat("anto");
        uploaded = extras.getInt("uploaded");

        Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        iv_photo.setImageBitmap(bmp);

        tv_cloro.setText("" + new DecimalFormat("###.####").format(kloro));
        tv_anto.setText("" + new DecimalFormat("###.####").format(anto));
        tv_caro.setText("" + new DecimalFormat("###.####").format(karo));
    }

}