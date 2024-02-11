package com.example.leafpiction.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leafpiction.R;
import com.example.leafpiction.Util.HistoryDatabaseCRUD;
import com.mckrpk.animatedprogressbar.AnimatedProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class CalibrationActivity extends AppCompatActivity {

    ImageView iv_photo;
    TextView tv_cloro, tv_caro, tv_anto;
    Button btn_calibrate, btn_delete;
    AnimatedProgressBar animatedProgressBar;

    HistoryDatabaseCRUD dbHandler;

    int id, uploaded;
    byte[] photo;
    byte[] photo_send;
    float kloro, karo, anto;
    private String url="http://192.168.43.108:5000/POST";//****Put your  URL here******
    private String POST="POST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        iv_photo = findViewById(R.id.iv_photo);
        tv_cloro = findViewById(R.id.textkloro);
        tv_anto = findViewById(R.id.textconfidence);
        tv_caro = findViewById(R.id.textstatus);
        btn_delete = findViewById(R.id.btn_delete);
        btn_calibrate = findViewById(R.id.btn_calibrate);
        animatedProgressBar = findViewById(R.id.animatedProgressBar);

        setView();

        dbHandler = new HistoryDatabaseCRUD();

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.deleteRecord(getApplicationContext(), id);
                finish();
            }
        });

        btn_calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = getIntent().getExtras();

//              Send Images to Server
                photo_send = extras.getByteArray("photo");
                Bitmap bmp = BitmapFactory.decodeByteArray(photo_send, 0, photo_send.length);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] bytearray = baos.toByteArray();
                String encodedImage = Base64.encodeToString(bytearray, Base64.DEFAULT);
                sendRequest("json_img",encodedImage);

                animatedProgressBar.setVisibility(View.VISIBLE);

                final Handler nhandler = new Handler();
                nhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CalibrationActivity.this, DetailActivity.class);
                        intent.putExtra("photo_cal",photo);
                        intent.putExtra("id_cal", id);
                        intent.putExtra("uploaded_cal", uploaded);

                        startActivity(intent);

                        animatedProgressBar.setVisibility(View.GONE);

                    }
                },6000);
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object

        iv_photo.setImageBitmap(bmp);

        tv_cloro.setText("" + new DecimalFormat("###.####").format(kloro));
        tv_anto.setText("" + new DecimalFormat("###.####").format(anto));
        tv_caro.setText("" + new DecimalFormat("###.####").format(karo));
    }

    void sendRequest(String paramname,String param){

        /* if url is of our get request, it should not have parameters according to our implementation.
         * But our post request should have 'name' parameter. */
        String fullURL=url;
        Request request;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();

        /* If it is a post request, then we have to pass the parameters inside the request body*/
        RequestBody formBody = new FormBody.Builder()
                .add(paramname, param)
                .build();

        request=new Request.Builder()
                .url(fullURL)
                .post(formBody)
                .build();

        /* this is how the callback get handled */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // Read data on the worker thread
                final String responseData = response.body().string();

                // Run view-related code back on the main thread.
                // Here we display the response message in our text view
            }
        });
    }




}