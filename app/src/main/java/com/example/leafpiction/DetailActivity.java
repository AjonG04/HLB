package com.example.leafpiction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leafpiction.Util.HistoryDatabaseCRUD;

import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {

    ImageView iv_photo;
    TextView tv_cloro, tv_caro, tv_anto;
    Button btn_delete, btn_upload;

    HistoryDatabaseCRUD dbHandler;

    int id;
    byte[] byteArray;
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

    }

    private void setView() {
        Bundle extras = getIntent().getExtras();
        byteArray = extras.getByteArray("photo");
        id = extras.getInt("id");
        kloro = extras.getFloat("cloro");
        karo = extras.getFloat("caro");
        anto = extras.getFloat("anto");

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        iv_photo.setImageBitmap(bmp);

        tv_cloro.setText("" + new DecimalFormat("###.####").format(kloro));
        tv_anto.setText("" + new DecimalFormat("###.####").format(anto));
        tv_caro.setText("" + new DecimalFormat("###.####").format(karo));
    }

}