package com.example.leafpiction.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leafpiction.Model.DataModel;
import com.example.leafpiction.R;
import com.example.leafpiction.Util.HistoryDatabaseCRUD;
import com.example.leafpiction.ml.SqueezenetAdamaxv2;
import com.google.android.material.snackbar.Snackbar;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



import static android.content.ContentValues.TAG;

public class GalleryActivity extends AppCompatActivity {

    ImageView iv_photo;
    TextView tv_cloro, tv_caro, tv_anto;
    ImageButton btn_save;

    HistoryDatabaseCRUD dbHandler;
    Context context;

    protected Interpreter tflite;
//    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
//    private TensorBuffer outputProbabilityBuffer;
//    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 255.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 1.0f;

    Pair test;
    String[] output;
    byte[] photo;
    Bitmap bmp;
    int imageSize1 = 656;
    int imageSize2 = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        iv_photo = findViewById(R.id.iv_photo);
//        tv_cloro = findViewById(R.id.textkloro);
        tv_anto = findViewById(R.id.textconfidence);
        tv_caro = findViewById(R.id.textstatus);
        btn_save = findViewById(R.id.btn_save);

        dbHandler = new HistoryDatabaseCRUD();
        context = getApplicationContext();

        try{
            tflite = new Interpreter(loadmodelfile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUI();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(view);
            }
        });
    }

    private void setUI(){
        photo = getIntent().getByteArrayExtra("photo");
        bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        iv_photo.setImageBitmap(bmp);

        test = classifyImage(bmp);

//        output =  doInference(bmp);
        String status = test.first.toString();
        String confidence = test.second.toString();
//        tv_cloro.setText("" + status);
        tv_caro.setText("" + confidence + "%");
        tv_anto.setText("" + status);
    }

    private void saveData(View view) {

        String status = test.first.toString();
        String confidence = test.second.toString();


        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String datetime = dateFormat.format(date);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        String filename = "Image_" + timeStamp;

        DataModel dataModel = new DataModel(photo, status, confidence + "%", datetime, filename, 0);

        String text;

        try {
            dbHandler.addRecord(context, dataModel);
            text = getResources().getString(R.string.addPhoto);
        } catch (Exception e) {
            text = getResources().getString(R.string.errorAddPhoto);
            Log.d(TAG, "Error while trying to add photo to database");
        }

        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    public Pair classifyImage(Bitmap image){
        String result = "";
        float maxConfidence = 0;
        Bitmap input = Bitmap.createScaledBitmap(image, 456, 656, true);

        try {
            SqueezenetAdamaxv2 model = SqueezenetAdamaxv2.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 656, 456, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize1 * imageSize2 * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize1 * imageSize2];
            input.getPixels(intValues, 0, input.getWidth(), 0, 0, input.getWidth(), input.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize1; i ++){
                for(int j = 0; j < imageSize2; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            SqueezenetAdamaxv2.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Bukan HLB", "HLB", "Sehat"};
            result = classes[maxPos];
            maxConfidence = maxConfidence * 100;

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
        return new Pair(result, new DecimalFormat("##.##").format(maxConfidence));
    }

    private String[] doInference(Bitmap bitmap){
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}

        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

//        int probabilityTensorIndex = 0;
//        int[] probabilityShape =
//                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}

//        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        bitmap = Bitmap.createScaledBitmap(bitmap, 54, 54, true);
//        iv_photo.setImageBitmap(bitmap);

        // New Code
//        ByteBuffer input = ByteBuffer.allocateDirect(54 * 54 * 3 * 4).order(ByteOrder.nativeOrder());
//        for (int y = 0; y < 54; y++) {
//            for (int x = 0; x < 54; x++) {
//                int px = bitmap.getPixel(x, y);
//
//                // Get channel values from the pixel value.
//                int r = Color.red(px);
//                int g = Color.green(px);
//                int b = Color.blue(px);
//
//                // Normalize channel values to [0.0, 1.0].
//                float rf = (r) / 255.0f;
//                float gf = (g) / 255.0f;
//                float bf = (b) / 255.0f;
//
//                input.putFloat(rf);
//                input.putFloat(gf);
//                input.putFloat(bf);
//            }
//        }

//        float[][] outputs = new float[1][3];;
//        tflite.run(input, outputs);
//        float[] output = outputs[0];
//
//        String temp = " " +  output[0] + " " + output[1] + " " + output[2];
//        Log.d("Prediksi Pigmen", temp);
//
//        output[0] = output[0] * 892.24595f + 0.0032483f;
//        output[1] = output[1] * 211.29755f + 0.0f;
//        output[2] = output[2] * 345.45058f + 0.0f;

        int x = 54;
        int y = 54;
        int componentsPerPixel = 3;
        int totalPixels = x * y;

        int[] argbPixels = new int[totalPixels];
        float[][][][] rgbValuesFinal = new float[1][x][y][componentsPerPixel];

        bitmap.getPixels(argbPixels, 0, x, 0, 0, x, y);

//        double[][] alphas = new double[54][54];
        double[][] reds = new double[54][54];
        double[][] greens = new double[54][54];
        double[][] blues = new double[54][54];

        int pos;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
//                pos = i*j+j;

//                int argbPixel = argbPixels[pos];
//
//                int alpha = ((argbPixel >> 24) & 0xff);
//                int blue = ((argbPixel >> 16) & 0xff);
//                int green = ((argbPixel >> 8) & 0xff);
//                int red = (argbPixel & 0xff);

//                int diff = (blue - red) / 2;
//                blue += diff;
//                red += diff;

//                rgbValuesFinal[0][i][j][0] = (float) (red / 255.0);
//                rgbValuesFinal[0][i][j][1] = (float) (green / 255.0);
//                rgbValuesFinal[0][i][j][2] = (float) (blue / 255.0);

//                rgbValuesFinal[0][i][j][0] = (float) (redColor[i][j] / 255.0);
//                rgbValuesFinal[0][i][j][1] = (float) (greenColor[i][j] / 255.0);
//                rgbValuesFinal[0][i][j][2] = (float) (blueColor[i][j] / 255.0);

//                alphas[i][j] = alpha;
//                reds[i][j] = red;
//                greens[i][j] = green;
//                blues[i][j] = blue;
//                reds[i][j] = redColor[i][j];
//                greens[i][j] = greenColor[i][j];
//                blues[i][j] = blueColor[i][j];

//                rgbValuesFinal[0][i][j][0] = (float) ((((argbPixel >> 16) & 0xff) / 255.0));
//                rgbValuesFinal[0][i][j][1] = (float) ((((argbPixel >> 8) & 0xff) / 255.0));
//                rgbValuesFinal[0][i][j][2] = (float) (((argbPixel & 0xff) / 255.0));

                int argbPixel = bitmap.getPixel(i, j);
                int blue = Color.red(argbPixel);
                int green = Color.green(argbPixel);
                int red = Color.blue(argbPixel);
                rgbValuesFinal[0][i][j][0] = (float) (red/255.0);
                rgbValuesFinal[0][i][j][1] = (float) (green/255.0);
                rgbValuesFinal[0][i][j][2] = (float) (blue/255.0);
                reds[i][j] = red;
                greens[i][j] = green;
                blues[i][j] = blue;
            }
        }

        int pixpos = 30;
        String temp3 = " " + reds[pixpos][pixpos] + " "
                + greens[pixpos][pixpos] + " "
                + blues[pixpos][pixpos];
        Log.d("Trial - RGB", temp3);

        String temp2 = " " + rgbValuesFinal[0][pixpos][pixpos][0] + " "
                + rgbValuesFinal[0][pixpos][pixpos][1] + " "
                + rgbValuesFinal[0][pixpos][pixpos][2];
        Log.d("Trial - RGB2", temp2);

        String[][] outputs = new String[1][3];

        tflite.run(rgbValuesFinal, outputs);

        String[] output = outputs[0];

        String temp = " " +  output[0] + " " + output[1] + " " + output[2];
        Log.d("Trial - Output", temp);

//        output[0] = output[0] * 892.24595f + 0.0032483f;
//        output[1] = output[1] * 211.29755f + 0.0f;
//        output[2] = output[2] * 345.45058f + 0.0f;

        // End of New Code

//        inputImageBuffer = new TensorImage(imageDataType);
////        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
////        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();
//
//        inputImageBuffer = loadImage(bitmap);
//
////        tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
////        float[] output =  outputProbabilityBuffer.getFloatArray();
//
//        float[][] outputs = new float[1][3];;
//        tflite.run(inputImageBuffer.getBuffer(), outputs);
//        float[] output = outputs[0];
//
//        output[0] = output[0] * 892.24595f + 0.0032483f;
//        output[1] = output[1] * 211.29755f + 0.0f;
//        output[2] = output[2] * 345.45058f + 0.0f;

        return output;
    }

//    private TensorImage loadImage(final Bitmap bitmap) {
//        // Loads bitmap into a TensorImage.
//        inputImageBuffer.load(bitmap);
//
//        // Creates processor for the TensorImage.
////        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
//
//        // TODO(b/143564309): Fuse ops inside ImageProcessor.
////        ImageProcessor imageProcessor =
////                new ImageProcessor.Builder()
////                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
////                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
////                        .add(getPreprocessNormalizeOp())
////                        .build();
//
//        ImageProcessor imageProcessor =
//                new ImageProcessor.Builder()
//                        .add(getPreprocessNormalizeOp())
//                        .build();
//
//        return imageProcessor.process(inputImageBuffer);
//    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("SqueezeNet_Adamax.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    //next button
//    public void LoadDetailActivity(View view) {
//        Intent intent = new Intent(GalleryActivity.this, DetailActivity.class);
//        startActivity(intent);
//    }

    //back button
//    public void loadCameraActivity(View view) {
//        Intent intent = new Intent(GalleryActivity.this,CameraActivity.class);
//        startActivity(intent);
//    }

//    private TensorOperator getPreprocessNormalizeOp() {
////        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
////    }

//    private TensorOperator getPostprocessNormalizeOp(){
//        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
//    }
}