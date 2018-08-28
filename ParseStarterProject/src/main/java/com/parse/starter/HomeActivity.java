package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    LinearLayout mFeedLayout;

    private static final int CHOOSE_IMAGE_CODE =101 ;
    private Uri uriProfileImage;
    private ImageView mImageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(ParseUser.getCurrentUser().getUsername().toString());
        mProgressBar=findViewById(R.id.progressBar);
        mFeedLayout=findViewById(R.id.feed_layout);
        mProgressBar.setVisibility(View.GONE);

        loadUserImages(ParseUser.getCurrentUser().getUsername().toString());

    }

    public void loadUserImages(String username){
        mFeedLayout.removeAllViews();
        // query OF all item in specific class
        ParseQuery<ParseObject> query=new ParseQuery<ParseObject>("Images");

        query.whereEqualTo("username",username);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() >0){
                    for(ParseObject object :objects){

                        ParseFile file=(ParseFile)object.get("image");

                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e== null && data !=null){
                                    Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
                                    ImageView imageView=new ImageView(getApplicationContext());

                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));

                                    imageView.setImageBitmap(bitmap);

                                    mFeedLayout.addView(imageView);
                                }
                            }
                        });

                    }
                }else {
                    Toast.makeText(HomeActivity.this,"THERE IS NO PHOTO YET", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showImageChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"), CHOOSE_IMAGE_CODE);
        mProgressBar.setVisibility(View.VISIBLE);


        // Directly opens Gallery app
       /*Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       startActivityForResult(intent,CHOOSE_IMAGE_CODE);*/
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // get image from image chooser
        if(requestCode == CHOOSE_IMAGE_CODE && resultCode == RESULT_OK  && data != null && data.getData() != null){
            uriProfileImage=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);

                ByteArrayOutputStream stream= new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                byte[] bytes=stream.toByteArray();

                ParseFile file=new ParseFile("image.png",bytes);

                ParseObject object=new ParseObject("Images");

                object.put("image",file);
                object.put("username",ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mProgressBar.setVisibility(View.GONE);
                        if(e == null){
                            Toast.makeText(HomeActivity.this, "Image has been shared", Toast.LENGTH_SHORT).show();
                            loadUserImages(ParseUser.getCurrentUser().getUsername());
                        }else{
                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //mImageProfile.setImageBitmap(bitmap);
                //uploadImageToFirebaseStorage()
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                showImageChooser();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.share_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.share){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},CHOOSE_IMAGE_CODE);
            }else {
                showImageChooser();
            }
        }else if (item.getItemId()==R.id.logout){
            ParseUser.logOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
