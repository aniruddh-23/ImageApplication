package com.example.imageapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.IOException;

public class Decryption extends AppCompatActivity {

    ImageView decImage;
    Button gallaryButton;
    Button decButton;
    TextView passText;

    private static final int PICK_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decryption);

        setValues();
        gallaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SUBS","ENCRYPTION BUTTON");
                Drawable tempDraw = decImage.getDrawable();
                Bitmap imageBitmap = ((BitmapDrawable) tempDraw).getBitmap();
                Intent intent = new Intent(Decryption.this,DecryptedImage.class);

                intent.putExtra("Image",imageBitmap);
                intent.putExtra("Pass",passText.getText());

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            //for image from gallery
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                decImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT);
                toast.setMargin(300,300);
                toast.show();
                e.printStackTrace();
            }

        }
    }
    private void setValues(){
        decImage = (ImageView)findViewById(R.id.decImage);
        gallaryButton = (Button)findViewById(R.id.galleryButton);
        decButton = (Button)findViewById(R.id.decButton);
        passText = (TextView)findViewById(R.id.passText);
    }

    private void openGallery(){
        Log.d("SUBS","GAllery");
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
}
