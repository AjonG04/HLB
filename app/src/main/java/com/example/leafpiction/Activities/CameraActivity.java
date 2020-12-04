package com.example.leafpiction.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leafpiction.Model.DataModel;
import com.example.leafpiction.R;
import com.example.leafpiction.Util.HistoryDatabaseCRUD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static android.content.ContentValues.TAG;

public class CameraActivity extends AppCompatActivity {

    private CameraDevice cameraDevice;
    private TextureView textureView;
    private FloatingActionButton fab;
    private TextView kloro ;
    private TextView karo ;
    private TextView anto ;

    private String cameraId;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size[] allimageDimension;
    private Size imageDimension;
    private ImageReader imageReader;
    private Bitmap photo;

    private View greenBoxView;
    private float RectLeft;
    private float RectTop;
    private float RectRight;
    private float RectBottom;
    private boolean status = false;

//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0,90);
//        ORIENTATIONS.append(Surface.ROTATION_90,0);
//        ORIENTATIONS.append(Surface.ROTATION_180,270);
//        ORIENTATIONS.append(Surface.ROTATION_270,180);
//    }

    //savefile
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private ImageReader mImageReader;

    private int a = 0;

    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 255.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 1.0f;
    private Bitmap bitmap;

    HistoryDatabaseCRUD dbHandler;
    Context context;

    CameraDevice.StateCallback stateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDev, int i) {
            cameraDev.close();
            cameraDevice = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textureView = (TextureView)findViewById(R.id.textureview);
        textureView.setSurfaceTextureListener(textureListener);

        kloro = ((TextView)findViewById(R.id.textkloro));
        karo = ((TextView)findViewById(R.id.textkaro));
        anto = ((TextView)findViewById(R.id.textanto));

        fab = (FloatingActionButton)findViewById(R.id.fab_take_photo);

        dbHandler = new HistoryDatabaseCRUD();
        context = getApplicationContext();

        greenBoxView = new Box(this);
        addContentView(greenBoxView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));

        try{
            tflite = new Interpreter(loadmodelfile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(view);
            }
        });
    }

    public class Box extends View {
        Paint paint = null;
        Canvas temp;
        Paint p = new Paint();
        Paint transparentPaint;
        Paint green=new Paint();
        Bitmap bitmap;

        int pixels;

        public Box(Context context)
        {
            super(context);
            paint = new Paint();
            final float scale = getContext().getResources().getDisplayMetrics().density;
            pixels = (int) (450 * scale + 0.5f);
            bitmap = Bitmap.createBitmap(getWindow().getWindowManager().getDefaultDisplay().getWidth(),pixels, Bitmap.Config.ARGB_8888);
            temp = new Canvas(bitmap);
            paint = new Paint();
            paint.setColor(Color.parseColor("#75757d8a"));
            transparentPaint = new Paint();
            transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
            transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            green.setStyle(Paint.Style.STROKE);
            green.setColor(Color.GREEN);
            green.setStrokeWidth(5);

//            draw guide box
            RectLeft = getWindow().getWindowManager().getDefaultDisplay().getWidth()/2 - getWindow().getWindowManager().getDefaultDisplay().getWidth()/5*3/2;
            RectTop = getWindow().getWindowManager().getDefaultDisplay().getWidth()/2 - RectLeft*3/2;
            RectRight =  getWindow().getWindowManager().getDefaultDisplay().getWidth()/2 + getWindow().getWindowManager().getDefaultDisplay().getWidth()/5*3/2;
            RectBottom = pixels/2 + RectLeft*3/2;

            if (status == false){

                temp.drawRect(0, 0, temp.getWidth(), temp.getHeight(), paint);
                temp.drawRect(RectLeft, RectTop, RectRight, RectBottom, transparentPaint);
                temp.drawCircle(fab.getLeft()+fab.getWidth()/2,fab.getTop()+fab.getHeight()/2,fab.getWidth()/2,transparentPaint);

            }
            canvas.drawRect(RectLeft, RectTop, RectRight, RectBottom, green);
            canvas.drawBitmap(bitmap, 0, 0, p);
            status = true;


        }
    }

    private void takePicture(View view) {
        Bitmap bmp = textureView.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        float[] output =  doInference(bmp);
        float chlorophyll = output[0];
        float carotenoid = output[1];
        float anthocyanin = output[2];

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String datetime = dateFormat.format(date);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        String filename = "Image_" + timeStamp;

        DataModel dataModel = new DataModel(byteArray, chlorophyll, carotenoid, anthocyanin, datetime, filename, 0);

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

    private void openCamera(){
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map !=null;
            allimageDimension = map.getOutputSizes(SurfaceTexture.class);
            imageDimension = findBestSize(allimageDimension,textureView.getHeight(),textureView.getWidth());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallBack,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size findBestSize(Size[] allimageDimension, int height, int width) {

        double preferredRatio = height / (double) width;
        Size currentOptimalSize = allimageDimension[0];
        double currentOptimalRatio = currentOptimalSize.getWidth() / (double) currentOptimalSize.getHeight();
        for (Size currentSize : allimageDimension) {
            double currentRatio = currentSize.getWidth() / (double) currentSize.getHeight();
            if (Math.abs(preferredRatio - currentRatio) <
                    Math.abs(preferredRatio - currentOptimalRatio)) {
                currentOptimalSize = currentSize;
                currentOptimalRatio = currentRatio;
            }
        }
        return currentOptimalSize;
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture!=null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            mImageReader = ImageReader.newInstance(imageDimension.getWidth(), imageDimension.getHeight(), ImageFormat.JPEG, 30);
//            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//                @Override
//                public void onImageAvailable(ImageReader imageReader) {
//                    ((TextView)findViewById(R.id.textkloro)).setText(""+a);
//                    a++;
//                }
//            },mBackgroundHandler);
//            captureRequestBuilder.addTarget(mImageReader.getSurface());
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraSession) {
                    if (cameraDevice == null)
                        return;
                    cameraCaptureSession = cameraSession;
                    updatePreview();

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraActivity.this,"Changed",Toast.LENGTH_SHORT).show();
                }
            },null);

//            cameraDevice.createCaptureSession(Arrays.asList(mImageReader.getSurface()), new StateCallback() {
//                @Override
//                public void onConfigured(@NonNull CameraCaptureSession cameraSession) {
//                    if (cameraDevice == null)
//                        return;
//                    cameraCaptureSession = cameraSession;
//                    updatePreview();
//                }
//
//                @Override
//                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
//
//                }
//            },null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    private void updatePreview(){
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(CameraActivity.this,"Can't use camera without permission",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            if (textureView.getBitmap() != null){

                bitmap = textureView.getBitmap();

                float[] output =  doInference(bitmap);

                kloro.setText(""+new DecimalFormat("###.####").format(output[0]));
                karo.setText(""+new DecimalFormat("###.####").format(output[1]));
                anto.setText(""+new DecimalFormat("###.####").format(output[2]));
            }
        }
    };

    private float[] doInference(Bitmap bitmap){
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}

        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}

        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        inputImageBuffer = new TensorImage(imageDataType);
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        Matrix mat = new Matrix();
        //PROSES CROPPING
        photo = Bitmap.createBitmap(bitmap, Math.round(RectLeft), Math.round(RectTop),
                Math.round(RectRight)-Math.round(RectLeft),
                Math.round(RectBottom)-Math.round(RectTop), mat, true);

//        photo = Bitmap.createScaledBitmap(photo, 34, 34, true);
        inputImageBuffer = loadImage(photo);
//        inputImageBuffer = loadImage(bitmap);

//        tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
//        float[] output =  outputProbabilityBuffer.getFloatArray();

        float[][] outputs = new float[1][3];;
        tflite.run(inputImageBuffer.getBuffer(), outputs);
        float[] output = outputs[0];

        String temp = output[0] + " " + output[1] + " " + output[2];
        Log.d("Output", temp);

        output[0] = output[0] * 892.24595f + 0.0032483f;
        output[1] = output[1] * 211.29755f + 0.0f;
        output[2] = output[2] * 345.45058f + 0.0f;

        return output;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable()){
            openCamera();
        }else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBackgroundThread();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        // TODO(b/143564309): Fuse ops inside ImageProcessor.
//        ImageProcessor imageProcessor =
//                new ImageProcessor.Builder()
//                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
//                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
//                        .add(getPreprocessNormalizeOp())
//                        .build();

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("P3Net.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

}