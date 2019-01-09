package com.example.gbiscini.test;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class CameraActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private TextView mTextView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        this.imageView = (ImageView) this.findViewById(R.id.imageView1);
        this.mTextView = findViewById(R.id.text_view);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }


    }

    protected void onActivityResult (int requestCode,int resultCode, Intent data){
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            final TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();


            Bitmap photo = (Bitmap) data.getExtras().get("data");
            photo=rotateImage(photo,90f);
            Frame image= new Frame.Builder().setBitmap(photo).build();
            final SparseArray<TextBlock> items=textRecognizer.detect(image);
            Toast.makeText(this, "size " + items.size()+" "+textRecognizer.isOperational (), Toast.LENGTH_LONG).show();

            if (items.size() != 0 ){

                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int i=0;i<items.size();i++){
                            TextBlock item = items.valueAt(i);
                            stringBuilder.append(item.getValue());
                            stringBuilder.append(" ");
                        }
                        String foundText=stringBuilder.toString();
                        dialogCheck(foundText);
                    }
                });
            }else{
                mTextView.setText("No text detected");
            }
            imageView.setImageBitmap(photo);
        }
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void dialogCheck(final String text_found){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("What is written");

        // Setting Dialog Message
        alertDialog.setMessage("Could it be "+text_found+" ?");
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.key);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        // Write your code here to execute after dialog
                        if (input.getText().toString().trim().equals(text_found.trim())){
                            Toast toast = Toast.makeText(getApplicationContext(), "I knew it", Toast.LENGTH_SHORT);
                            toast.show();
                            mTextView.setText("I knew it");
                            finish();
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            intent.putExtra("wordFound",true);
                            startActivity(intent);
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), "You win this time", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            intent.putExtra("wordFound",true);
                            startActivity(intent);
                            mTextView.setText("You win this time");
                        }
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });

        // closed

        // Showing Alert Message
        alertDialog.show();
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }



}