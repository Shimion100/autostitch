package com.cs482group.autostitch;

//import android.app.ListActivity;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.content.Intent;
import android.database.Cursor;
import java.util.ArrayList;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v4.content.CursorLoader;

import com.cs482group.autostitch.R;

public class ImageListActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "ImageListActivity";
	ArrayList<String> items = new ArrayList<String>();
	ArrayList<String> imageIds = new ArrayList<String>();
	boolean[] checkedItems;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "getting content view");
        setContentView(R.layout.imagelist);
        Log.d(TAG, "getting main list view");
        ListView mainView = (ListView) this.findViewById(R.id.listView1);
        
        ((Button) this.findViewById(R.id.btnDone)).setOnClickListener(this);
        
        
        //this.getIntent();
        Log.d(TAG, "loading image info");
        this.loadImageInfo();
        
        Log.d(TAG, items.size() + " items loaded");
        
        if( items.size() > 0) { 
        	mainView.setItemsCanFocus(false);
        	mainView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        	mainView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, items.toArray(new String[items.size()])));
        } else {
        	Log.e(TAG, "EMPTY array - no images loaded");
        }
    }
    
    public void processReturn() {
    	
    	Log.i(TAG,"processReturn() Started");
    	
    	Intent retData = new Intent();
    	ArrayList<String> chkdIds = new ArrayList<String>();
    	ListView mainView = (ListView) this.findViewById(R.id.listView1);
    	int count = mainView.getAdapter().getCount();

    	for(int i=0; i<count; i++) {
    		if (mainView.isItemChecked(i)){
    			chkdIds.add(imageIds.get(i));
    			Log.i(TAG, items.get(i));
    		}
    	}
    	
    	retData.putExtra("com.cs482group.autostitch.checked", chkdIds.toArray(new String[chkdIds.size()]));
    	this.setResult(RESULT_OK, retData);
    	finish();
    }
    
    public void loadImageInfo() {
    	
    	Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    	
    	String[] projection = new String[]{ 
    			MediaStore.Images.Media._ID, 
    			//MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
    			MediaStore.Images.Media.DISPLAY_NAME
        };

    	CursorLoader cl = new CursorLoader(ImageListActivity.this,imageUri,projection,"",null,"");
    	Cursor cur = cl.loadInBackground();
    	
    	if (cur.moveToFirst()) {
            String name;
            String id;
            int idCol = cur.getColumnIndex(projection[0]);
            int nameCol = cur.getColumnIndex(projection[1]);

            do {
            	id = cur.getString(idCol);
            	name = cur.getString(nameCol);
            	
            	Log.i(TAG, "Image - " + id + " - " + name);
            	
            	items.add(name);
            	imageIds.add(id);
            } while (cur.moveToNext());
        }
    	
    	cur.close();
    	
    }
    
    public Uri getUriFromId(String id) {
    	return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
    }

	public void onClick(View v) {
		processReturn();
	}
	
	@Override
	public void onBackPressed() {
		processReturn();
	    super.onBackPressed();
	}

}
