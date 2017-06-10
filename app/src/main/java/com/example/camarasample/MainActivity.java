package com.example.camarasample;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;


public class MainActivity extends ActionBarActivity {
	
	    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
	    public static final int MEDIA_TYPE_IMAGE = 1;
	    public static final int MEDIA_TYPE_VIDEO = 2;
	    
	    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
	    
	    private Uri fileUri; // file url to store image/video
	 
	    private ImageView imgPreview;
	    private VideoView videoPreview;
	    private Button btnCapturePicture, btnRecordVideo;
	 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        videoPreview = (VideoView) findViewById(R.id.videoPreview);
        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
        
        btnCapturePicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				captureImage();
				
			}
		});
        
        btnRecordVideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				recordVideo();
				
			}
		});
        
    }
    
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    
    private void captureImage(){
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	 
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
    	
    	
 
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
 
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    // TODO Auto-generated method stub
    super.onSaveInstanceState(outState);
    
    outState.putParcelable("file_uri", fileUri);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onRestoreInstanceState(savedInstanceState);
    
    fileUri = savedInstanceState.getParcelable("file_uri");
    }
    
    private void recordVideo(){
    	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    	 
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
 
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
 
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
                                                            // name
 
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    super.onActivityResult(requestCode, resultCode, data);
    
    if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            // successfully captured the image
            // display it in image view
            previewCapturedImage();
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(getApplicationContext(),
                    "User cancelled image capture", Toast.LENGTH_SHORT)
                    .show();
        } else {
            // failed to capture image
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                    .show();
        }
    } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            // video successfully recorded
            // preview the recorded video
            previewVideo();
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled recording
            Toast.makeText(getApplicationContext(),
                    "User cancelled video recording", Toast.LENGTH_SHORT)
                    .show();
        } else {
            // failed to record video
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    }
    
    private void previewCapturedImage(){
    	try {
            // hide video preview
            videoPreview.setVisibility(View.GONE);
 
         //   imgPreview.setVisibility(View.VISIBLE);
 
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();
 
            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
 
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),options);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
    		byte[] b = baos.toByteArray();
    		
    		//convert image byte array to base64 string format.
    		String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
    		System.out.println("This is BitMap"+encodedImage);
    		
    		byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
    		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
    		
    		System.out.println("Decoded String"+decodedByte);
 
            imgPreview.setImageBitmap(decodedByte);
            imgPreview.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    	
    }
    
    private void previewVideo(){
    	
    	 try {
             // hide image preview
             imgPreview.setVisibility(View.GONE);
  
             videoPreview.setVisibility(View.VISIBLE);
             videoPreview.setVideoPath(fileUri.getPath());
             // start playing
             videoPreview.start();
         } catch (Exception e) {
             e.printStackTrace();
         }
    	
    }
    
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    
    private static File getOutputMediaFile(int type) {
    	 
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
 
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
 
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyy_MMdd_HHmmss",Locale.getDefault()).format(new Date(0));
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
 
        return mediaFile;
    }
}
