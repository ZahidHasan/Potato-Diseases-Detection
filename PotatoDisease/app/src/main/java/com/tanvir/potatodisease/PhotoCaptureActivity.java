package com.tanvir.potatodisease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tanvir.potatodisease.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class PhotoCaptureActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE =1000 ;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int IMAGE_PICK_CODE=1000;
    Button mCaptureBtn,mPredict,mChooseBtn;
    ImageView mImageView;
    TextView result;
    Bitmap bitmap;

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);

        mImageView=findViewById(R.id.image_view);
        mCaptureBtn=findViewById(R.id.button1);
        mChooseBtn=findViewById(R.id.button2);
        mPredict=findViewById(R.id.button3);
        result=findViewById(R.id.result);

        mChooseBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //if system os is >= marshmallow, request runtime permission
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){

                        String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};

                        //show popup to request permission
                        requestPermissions(permissions,PERMISSION_CODE);
                    }
                    else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else {
                    //System os < marshmallow
                    pickImageFromGallery();
                }
            }
        });




        //Capture image
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if system os is >= marshmallow, request runtime permission
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                    PackageManager.PERMISSION_DENIED){
                        String[] permission={android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        //show popup to request permission
                        requestPermissions(permission,PERMISSION_CODE);
                    }
                    else {
                        //permission already granted
                        openCamera();
                    }
                }
                else {
                    //System os < marshmallow
                    openCamera();
                }
            }
        });


        mPredict.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                try {
                    Model model = Model.newInstance(PhotoCaptureActivity.this);

                    // Creates inputs for reference.
                    bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());


                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    result.setText(getMax(outputFeature0.getFloatArray())+" ");

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });

    }

    int getMax(float[] arr){
        int max=0;
        for(int i=0;i<arr.length;i++){
            if(arr[i]>arr[max])max=i;
        }
        return max;
    }

    private void pickImageFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }



    private void openCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the camera");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //camera intent
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses allow or deny from permission request popup
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission from popup was granted
                    openCamera();
                    pickImageFromGallery();
                } else {
                    //permission from popup was denied

                }
            }
        }
    }

//    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            mImageView.setImageURI(image_uri);
//        }
//        if (resultCode==RESULT_OK && requestCode==IMAGE_PICK_CODE){
//            mImageView.setImageURI(data.getData());
//        }
////        if(data!=null){
////                image_uri=data.getData();
////                try{
////                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
////                    mImageView.setImageBitmap(bitmap);
////                }catch (IOException e){
////                    e.printStackTrace();
////                }
////        }
//        if (bitmap != null) {
//            // Your existing code to resize and process the bitmap
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            mImageView.setImageBitmap(bitmap);
//        } else {
//            // Handle the case where bitmap is null, perhaps with an error message
//            Toast.makeText(this, "Kam hoina", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri;
            if (requestCode == IMAGE_CAPTURE_CODE) {
                // Capturing a new photo
                uri = image_uri;
            } else if (requestCode == IMAGE_PICK_CODE) {
                // Picking an image from the gallery
                uri = data.getData();
            } else {
                return;
            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}


