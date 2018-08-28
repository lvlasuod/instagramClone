/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

  //Declare UI Elements
  LinearLayout signInLinearLayout;
  LinearLayout signUpLinearLayout;
  RelativeLayout bgLayout;

  EditText usernameEditText;
  EditText passwordEditText;

  Button signinButton;
  Button signupButton;

  TextView signUpLayoutLink;
  TextView signInLayoutLink;


    @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
        setTitle("Sign in or Sign up");

    //Fetching UI Elements
      signInLinearLayout=(LinearLayout) findViewById(R.id.sign_in_layout);
      signUpLinearLayout=(LinearLayout) findViewById(R.id.sign_up_layout);
      bgLayout=(RelativeLayout)findViewById(R.id.bg_layout);

      usernameEditText=(EditText)findViewById(R.id.nameEditText);
      passwordEditText=(EditText)findViewById(R.id.passwordEditText);

      signinButton=(Button)findViewById(R.id.sign_in_button);
      signupButton=(Button)findViewById(R.id.sign_up_button);

      signUpLayoutLink=(TextView)findViewById(R.id.sign_up_textView);
      signInLayoutLink=(TextView)findViewById(R.id.signinTextView);

      // setup keyboard modification

      passwordEditText.setOnKeyListener(this);

      //setting up UI for Login
      signUpLinearLayout.setVisibility(View.GONE);

      if(ParseUser.getCurrentUser()!=null){
          //  show sign in page and show profile page
          startActivity(new Intent(getApplicationContext(),HomeActivity.class));
          finish();
          // Toast.makeText(MainActivity.this, ParseUser.getCurrentUser().getUsername().toString(), Toast.LENGTH_SHORT).show();
      }

      //closing keyboard
      bgLayout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
              inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
          }
      });

      signUpLayoutLink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          signUpLinearLayout.setVisibility(View.VISIBLE);
          signInLinearLayout.setVisibility(View.GONE);
        }
      });

    signInLayoutLink.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        signUpLinearLayout.setVisibility(View.GONE);
        signInLinearLayout.setVisibility(View.VISIBLE);
      }
    });


    signinButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String username=usernameEditText.getText().toString();
        String password=passwordEditText.getText().toString();

        if(username.isEmpty()){
          usernameEditText.setError("require entering username");
        }
        if(password.isEmpty()){
          passwordEditText.setError("enter password");
        }


          //do login
          ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
              if(user !=null){
                // show profile page
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
                Log.e("User",user.getUsername().toString()+" Loged in");
              }else{
                  Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
              }
            }
          });
        }

    });

    signupButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String username=usernameEditText.getText().toString();
        String password=passwordEditText.getText().toString();

        if(username.isEmpty()){
          usernameEditText.setError("require entering username");
        }
        if(password.isEmpty()){
          passwordEditText.setError("enter password");
        }
         ParseUser user=new ParseUser();
         user.setUsername(username);
         user.setPassword(password);
         user.signUpInBackground(new SignUpCallback() {
        @Override
         public void done(ParseException e) {
          if(e == null){
                // show profile page
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
            Log.e("Signup","Done");
          } else {
              Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
          }
      }
    });

      }
    });

    /*
      // Creating " Score " class
    ParseObject score=new ParseObject("Score");

    // Insert Data To " Score " class
    score.put("username","John");
    score.put("score",50);
    score.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if(e==null){
              //ok
              Log.e("Success","We saved the score");
        }else{
          e.printStackTrace();
        }
      }
    });
    // Fetching Data From" Score " class
    ParseQuery<ParseObject> query=ParseQuery.getQuery("Score");

    query.getInBackground("4S942NCRHj", new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {
        if(e ==null && object !=null){

          // Update Data " Score " class
          object.put("score",85);
          object.saveInBackground();

          Log.e("username",object.getString("username")+" "+object.getInt("score"));

        }else{
          e.printStackTrace();
        }
      }
    });
*/
    /*ParseObject tweets=new ParseObject("Tweets");
    tweets.put("username","Mesut");
    tweets.put("tweet","hi man");
    tweets.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if(e==null){
          //ok
          Log.e("Success","We saved the Tweets");
        }else{
          e.printStackTrace();
        }
      }
    });

    // query OF Specific item in specific class
    ParseQuery<ParseObject> query=ParseQuery.getQuery("Tweets");
    query.getInBackground("MimSqdbyEr", new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {
        if(e == null && object !=null){
              //ok
                object.put("tweet","how r u today ?");
                object.saveInBackground();
                Log.e("update",object.getString("username"));
        }else{
          e.printStackTrace();
        }
      }
    });
*/

   /* // query OF all item in specific class
    ParseQuery<ParseObject> query=ParseQuery.getQuery("Score");
    query.whereGreaterThan("score",42);
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> objects, ParseException e) {
        if(e == null && objects.size() >0){
                for(ParseObject object :objects){
                  object.put("score",(object.getInt("score")+20));
                  object.saveInBackground();
                  Log.e("Scores",object.getString("username")+
                  " "+Integer.toString(object.getInt("score")));

                }
        }else {
          e.printStackTrace();
        }
      }
    });*/

   /* ParseUser user=new ParseUser();
    user.setUsername("lvlasuod");
    user.setPassword("123456");
    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {
          if(e == null){
                  Log.e("Signup","Done");
          } else {
            e.printStackTrace();
          }
      }
    });

      ParseUser.logInInBackground("lvlasuod", "123456", new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
                if(user !=null){
                    Log.e("User",user.getUsername().toString()+" Loged in");
                }else{
                    e.printStackTrace();
                }
          }
      });
*/
  /* if(ParseUser.getCurrentUser()!=null){
        Log.e("Signed In Alreay",ParseUser.getCurrentUser().getUsername().toString());
   }else{
       //do login

       }*/
      //  ParseUser.logOut();
      ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  // for keyboard modifications
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if((i == KeyEvent.KEYCODE_ENTER) && (keyEvent.getAction() == KeyEvent.ACTION_DOWN)){
            login(view);
        }
        return false;
    }

    public void login(View view){
        String username=usernameEditText.getText().toString();
        String password=passwordEditText.getText().toString();

        if(username.isEmpty()){
            usernameEditText.setError("require entering username");
        }
        if(password.isEmpty()){
            passwordEditText.setError("enter password");
        }


        //do login
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user !=null){
                    // show profile page
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();
                    Log.e("User",user.getUsername().toString()+" Loged in");
                }else{
                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}