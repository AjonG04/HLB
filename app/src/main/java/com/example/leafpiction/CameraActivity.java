package com.example.leafpiction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Arrays;

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

        try{
            tflite=new Interpreter(loadmodelfile(this));
        }catch (Exception e) {
            e.printStackTrace();
        }
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
                photo = textureView.getBitmap();
                photo = Bitmap.createScaledBitmap(photo, 32, 32, true);

                int x = photo.getWidth();
                int y = photo.getHeight();

                int componentsPerPixel = 3;
                int totalPixels = x * y;
                int totalBytes = totalPixels * componentsPerPixel;
                float[] rgbValues = new float[totalBytes];
                int[] argbPixels = new int[totalPixels];

                photo.getPixels(argbPixels, 0, x, 0, 0, x, y);

                for (int i = 0; i < totalPixels; i++) {
                    final int argbPixel = argbPixels[i];
                    rgbValues[i * componentsPerPixel + 0] = (float) ((((argbPixel >> 16) & 0xff) / 255.0));
                    rgbValues[i * componentsPerPixel + 1] = (float) ((((argbPixel >> 8) & 0xff) / 255.0));
                    rgbValues[i * componentsPerPixel + 2] = (float) (((argbPixel & 0xff) / 255.0));
                }

//                float[] output = new float[5];
//                TensorFlowInferenceInterface tensorflow = new TensorFlowInferenceInterface(getAssets(),
//                        "rgb_batch30_epoch30_Adam_shallow.pb");
//                tensorflow.feed("conv2d_11_input", rgbValues, 1,32, 32, 3);
//                tensorflow.run(new String[]{"output_node0"});
//                tensorflow.fetch("output_node0", output);
//
//                kloro.setText(""+new DecimalFormat("##.#####").format(output[1]));
//                karo.setText(""+new DecimalFormat("##.####").format(output[3]));
//                anto.setText(""+new DecimalFormat("##.####").format(output[4]));

                bitmap = textureView.getBitmap();

                int imageTensorIndex = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}

//                StringBuffer result = new StringBuffer();
//                for (int i = 0; i < imageShape.length; i++) {
//                    result.append( imageShape[i] );
//                    result.append( ", " );
//                }
//                String mynewstring = result.toString();
//
//                Log.v("Image Input Shape", String.valueOf(mynewstring));

                imageSizeY = imageShape[1];
                imageSizeX = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape =
                        tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}

//                StringBuffer result2 = new StringBuffer();
//                for (int i = 0; i < probabilityShape.length; i++) {
//                    result2.append( probabilityShape[i] );
//                    result2.append( ", " );
//                }
//                String mynewstring2 = result2.toString();
//
//                Log.v("Image Output Shape", String.valueOf(mynewstring2));

                DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                inputImageBuffer = loadImage(bitmap);

                tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());

                float[] output =  outputProbabilityBuffer.getFloatArray();

//                StringBuffer result3 = new StringBuffer();
//                for (int i = 0; i < output.length; i++) {
//                    result3.append( output[i] );
//                    result3.append( ", " );
//                }
//                String mynewstring3 = result3.toString();

//                Context context = getApplicationContext();
//                CharSequence text = "Cloro : " + output[0] + "\nCaro : " + output[1] + "\nAnto : " + output[2];
//                int duration = Toast.LENGTH_LONG;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

//                Log.v("Output Probability", String.valueOf(mynewstring3));

                kloro.setText(""+new DecimalFormat("###.####").format(output[0]));
                karo.setText(""+new DecimalFormat("###.####").format(output[1]));
                anto.setText(""+new DecimalFormat("###.####").format(output[2]));
            }
        }
    };

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
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
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