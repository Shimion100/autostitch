package com.cs468group.autostitch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.*;
import android.net.Uri;
import android.content.Intent;
import android.provider.MediaStore;
import android.os.Environment;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.util.Log;
import android.os.Parcelable;
import java.util.ArrayList;

public class AutoStitchActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	private static final String TAG = "AutoStitchActivity";
	private static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final int PICKER_IMAGE_REQUEST_CODE = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	Button btnCapture;
	Button btnSelect;
	Button btnSettings;
	Button btnStitch;
	Button btnOpen;
	Uri imgUri;
	ArrayList<Uri> imgList;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        imgList = new ArrayList<Uri>();
        
        btnCapture = (Button) this.findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);
        btnSelect = (Button) this.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(this);
        btnSettings = (Button) this.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);
        btnStitch = (Button) this.findViewById(R.id.btnStitch);
        btnStitch.setOnClickListener(this);
        btnOpen = (Button) this.findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(this);

    }
    
    public void onClick(View v) {
    	int id = v.getId();
    	switch (id) {
    		case R.id.btnCapture: startCameraIntent();
    		case R.id.btnSelect: startPickerIntent();
    		case R.id.btnSettings: ;
    		case R.id.btnStitch: ;
    		case R.id.btnOpen: ;
    		default: ; // error
    	}
    }
    
    public void startCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imgName = "IMG_" + (new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())) + ".jpg";
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AutoStitch");
        File img = new File(dir.getPath() + File.separator + imgName);
        
        imgUri = Uri.fromFile(img);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);

        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
    }
    
    // NOT WORKING - img picker code doesn't work
    public void startPickerIntent(){
    	Intent picker = new Intent(Intent.ACTION_GET_CONTENT);

    	picker.setType("image/*");
    	startActivityForResult(Intent.createChooser(picker, "Select Imgs"), PICKER_IMAGE_REQUEST_CODE);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            	Log.v(TAG,"image captured and saved");
            } else if (resultCode == RESULT_CANCELED) {
            	Log.e(TAG,"User cancelled the image capture");
            } else {
            	Log.e(TAG,"the image capture failed");
            }
        }
        
        // NOT WORKING - img picker code doesn't work
        if (requestCode == PICKER_IMAGE_REQUEST_CODE) {
        	if (Intent.ACTION_SEND_MULTIPLE.equals(data.getAction()) && data.hasExtra(Intent.EXTRA_STREAM)) {
                ArrayList<Parcelable> uriList = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if( !uriList.isEmpty() ){
                	imgList.clear();
	                for (Parcelable item : uriList) {
	                   imgList.add((Uri) item);
	                }
                }
                Log.v(TAG,"Selected " + uriList.size() + " images");
            } else {
            	Log.e(TAG,"Picker intent not ASM and not Extra");
            }
        }
        
    }
    
    
    
    
    
    
    
    
    
    
    
}