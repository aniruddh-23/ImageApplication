package com.example.imageapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Encryption extends AppCompatActivity {

    private ImageView encryptImage;
    private ImageView camButton;
    private Button galleryButton;
    private Button encButton;
    private TextView passText;
    private Bitmap imageBitmap;
    private Uri imageUri;

    static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int PICK_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);

        setValues();

        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        encButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SUBS","ENCRYPTION BUTTON");
                Drawable tempDraw = encryptImage.getDrawable();
                imageBitmap = ((BitmapDrawable) tempDraw).getBitmap();
                Intent intent = new Intent(Encryption.this,EncryptedImage.class);

                intent.putExtra("Image",imageBitmap);
                intent.putExtra("Pass",passText.getText());


                startActivity(intent);
            }
        });

    }

    private void setValues(){
        encryptImage = (ImageView)findViewById(R.id.imageView);
        camButton = (ImageView)findViewById(R.id.camButton);
        galleryButton = (Button)findViewById(R.id.galleryButton);
        encButton = (Button)findViewById(R.id.encButton);
        passText = (TextView)findViewById(R.id.editText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //for camera
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encryptImage.setImageBitmap(imageBitmap);
        }
        else if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            //for image from gallery
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                bitmap = getResizedBitmap(bitmap,300);
                encryptImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT);
                toast.setMargin(300,300);
                toast.show();
                e.printStackTrace();
            }
            // Log.d("Uri Tag",imageUri.getPath());


        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        Log.d("SUBS","mget resiZed");
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void launchCamera(){
        Log.d("SUBS","Camera");
        //taking pics
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //take a picture and pass results along to onActicyty Results
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }

    private void openGallery(){
        Log.d("SUBS","GAllery");
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
}
