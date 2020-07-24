package com.example.leafpiction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    ImageView iv_photo;
    TextView tv_cloro, tv_caro, tv_anto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        iv_photo = findViewById(R.id.iv_photo);
        tv_cloro = findViewById(R.id.textkloro);
        tv_anto = findViewById(R.id.textanto);
        tv_caro = findViewById(R.id.textkaro);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("photo");
        int id = extras.getInt("id");
        String kloro = String.valueOf(extras.getFloat("cloro"));
        String karo = String.valueOf(extras.getFloat("caro"));
        String anto = String.valueOf(extras.getFloat("anto"));

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        iv_photo.setImageBitmap(bmp);

        tv_cloro.setText(kloro);
        tv_anto.setText(anto);
        tv_caro.setText(karo);

    }
}