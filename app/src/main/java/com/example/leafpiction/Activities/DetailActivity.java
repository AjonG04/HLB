package com.example.leafpiction.Activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leafpiction.Model.DataModel;
import com.example.leafpiction.R;
import com.example.leafpiction.Util.HistoryDatabaseCRUD;
import com.google.android.material.snackbar.Snackbar;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    ImageView iv_photo;
    TextView tv_caro, tv_anto;
    ImageButton btn_delete, btn_save;

    protected Interpreter tflite;

    HistoryDatabaseCRUD dbHandler;

    int id, uploaded;
    byte[] photo;
    String kloro, karo, anto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        iv_photo = findViewById(R.id.iv_photo);
        tv_anto = findViewById(R.id.textconfidence);
        tv_caro = findViewById(R.id.textstatus);
        btn_save = findViewById(R.id.btn_save);
        btn_delete = findViewById(R.id.btn_hapus);

        try{
            tflite = new Interpreter(loadmodelfile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        photo = extras.getByteArray("photo");
        id = extras.getInt("id");
        kloro = extras.getString("cloro");
        karo = extras.getString("caro");
        anto = extras.getString("anto");
        uploaded = extras.getInt("uploaded");

        Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        iv_photo.setImageBitmap(bmp);

        tv_anto.setText("" + anto);
        tv_caro.setText("" + karo);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("SqueezeNet_Adamax.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

}