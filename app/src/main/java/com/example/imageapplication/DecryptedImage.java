package com.example.imageapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class DecryptedImage extends AppCompatActivity {

    private ImageView decryptedImage;
    private ImageView saveButton;
    private Bitmap bitmap;
    private Bitmap finalBitMap;
    private String passKey;
    private int[] rArray;
    private int[] passArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypted_image);
        Log.d("SUBS","In final");
        Bundle recIntent = getIntent().getExtras();
        bitmap = (Bitmap) recIntent.getParcelable("Image");
        passKey = recIntent.getString("Pass");
        Log.d("SUBS","SET");

        setValues();
        decryption();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveImage(finalBitMap);
            }
        });
    }

    private void SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".png";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

        Toast toast = Toast.makeText(getApplicationContext(),"Image Saved",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,100);
        toast.show();

    }

    private void decryption(){
        Log.d("SUBS", "1");
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        int[] intArray = new int[x * y];
        bitmap.getPixels(intArray, 0, x, 0, 0, x, y);
        finalBitMap = Bitmap.createBitmap(x, y, bitmap.getConfig());
        setArray(x,y);

        Log.d("SUBS", "2");
        int i,j,c;
        for(i=x-1;i>=0;i--)
        {
            c=swap(i,rArray[i],x);
            for(j=y-1;j>=0;j--)
            {
                int temp = intArray[i*y + j];
                intArray[i*y + j] = intArray[c*y + j];
                intArray[c*y + j] = temp;
            }
        }

        for(i=y-1;i>=0;i--)
        {
            c=swap(i,rArray[i],y);
            for(j=x-1;j>=0;j--)
            {

                int temp = intArray[i*x + j];
                intArray[i*x + j] = intArray[c*x + j];
                intArray[c*x + j] = temp;
            }
        }


        for ( i = 0; i < y; i++) {
            for ( j = 0; j < x; j++) {
                int pixel = intArray[i*x + j];
                int noise =  rArray[i*x + j];
                int pixelAlpha = Color.alpha(pixel);
                int pixelRed = (Color.red(pixel) + 256 - (noise)%256 )%256;
                int pixelGreen = (Color.green(pixel) + 256 - (noise)%256 )%256;
                int pixelBlue = (Color.blue(pixel) + 256 - (noise)%256 )%256;

                int newPixel = Color.argb(pixelAlpha, pixelRed, pixelGreen, pixelBlue);
                intArray[i*x + j] = newPixel;
                Log.d("PixelVals",Integer.toString(pixel)+" : "+Integer.toString(newPixel)+" : red "+Integer.toString(Color.red(pixel))+" "+Integer.toString(Color.red(newPixel))+" : blue "+Integer.toString(Color.blue(pixel))+" "+Integer.toString(Color.blue(newPixel))+" : green "+Integer.toString(Color.green(pixel))+" "+Integer.toString(Color.green(newPixel))+": noise  "+Integer.toString(noise%256));
            }
            Log.d("PixelVals",Integer.toString(i)+" : abc");
        }



        finalBitMap.setPixels(intArray, 0, x, 0, 0, x, y);
        decryptedImage.setImageBitmap(finalBitMap);
    }
    private static int swap(int i,int j,int h)
    {
        return ((i+j)%h);
    }

    private void setArray(int x, int y){
        double[] tempArray = new double[x*y];
        rArray = new int[x*y];
        tempArray[0] = 0.5;
        rArray[0] = (int)(tempArray[0]*1000);
        double r = 3.67;
        for(int i = 1; i<x*y; i++){
            tempArray[i] = r*tempArray[i-1]*(1 - tempArray[i-1]);
            rArray[i] = (int)(tempArray[i]*1000);
        }
    }

    private void setValues(){
        decryptedImage = (ImageView)findViewById(R.id.decryptedImage);
        saveButton = (ImageView)findViewById(R.id.saveButton2);

//        passArray = new int[passKey.length()];
//        for(int i =0;i<passArray.length;i++){
//            //int temp = passKey.charAt(i);
//            passArray[i] = 0;
//        }
    }

}
