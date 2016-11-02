package com.example.spork.lab3assignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int TAKE_AVATAR_CAMERA_REQUEST = 1;
    static final String SETTINGS_PREFS_AVATAR = "AvatarSettings";
    private SharedPreferences settings;
    Button up,down,right,left;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        up = (Button) findViewById(R.id.btnUp);
        down = (Button) findViewById(R.id.btnDown);
        left = (Button) findViewById(R.id.btnLeft);
        right = (Button) findViewById(R.id.btnRight);

        takePhoto();
    }
    public void movePicture(View v) {
        ImageButton image = (ImageButton) findViewById(R.id.imageButton);
        switch(v.getId()){
            case R.id.btnDown:
                    image.setY(image.getY() + 25);
                Log.d("button","Down");
                break;
            case R.id.btnUp:
                if (image.getY() >0 ) {
                    image.setY(image.getY() - 25);
                    Log.d("button", "up");
                }
                break;
            case R.id.btnRight:
                image.setX(image.getX()+25);
                Log.d("button","right");
                break;
            case R.id.btnLeft:
                if ( image.getX() > 0) {
                    image.setX(image.getX() - 25);
                    Log.d("button", "left");
                }
                break;
        }
    }
    private void takePhoto() {
        ImageButton avatarButton = (ImageButton) findViewById(R.id.imageButton);
        avatarButton.setOnClickListener(new ChooseCameraListener());
    }
    private class ChooseCameraListener implements View.OnClickListener{
        @Override
        public void onClick(View arg0){
            Intent pictureIntent = new Intent (
                    MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(Intent.createChooser(pictureIntent,"Take your photo"), TAKE_AVATAR_CAMERA_REQUEST);

        }
    }

    private void dispatchTakeIntent(int actionCode){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent,actionCode);
    }
    public static boolean isIntentAvailable(Context context, String action){
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list= packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void saveAvatar(Bitmap avatar){
        String avatarFilename = "avatar.jpg";
        //try to compres
        try
        {
            avatar.compress(Bitmap.CompressFormat.JPEG,100,
                    openFileOutput(avatarFilename,MODE_PRIVATE));

        }
        catch (Exception e ){

        }
        Uri avatarUri = Uri.fromFile(new File(
                //SettingsActivity.
                this.getFilesDir(),avatarFilename));

        ImageButton avatarButton = (ImageButton) findViewById(R.id.imageButton);
        avatarButton.setImageURI(null);//refresh image
        avatarButton.setImageURI(avatarUri);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SETTINGS_PREFS_AVATAR, avatarUri.getPath());
        editor.commit();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode)
        {
            case TAKE_AVATAR_CAMERA_REQUEST:
            {
                if (resultCode == Activity.RESULT_CANCELED)
                {
                    //shit all
                }
                else if (resultCode == Activity.RESULT_OK)
                {
                    //go on
                    Bitmap cameraPic = (Bitmap) data.getExtras().get("data");
                    if (cameraPic != null)
                    {
                        try
                        {
                            saveAvatar(cameraPic);
                        }
                        catch ( Exception e ){
                            //errrorororor
                            Log.wtf("camerapic","wtf");
                        }
                    }
                }
            }

        }
    }
}
