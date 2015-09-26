package com.example.test1;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	private EditText mTitle;
	private EditText mPrice;
	private Spinner  mLocation;
	private Spinner  mCategories;
	private ImageView mImageView;
	private Button mButtonSave;
	private Button mButtonCancel;
	private String errMessages="";
	private ArrayList<DataSave> userData = new ArrayList<DataSave>();
	private String [] mLocationArray;
	private String [] mCategoryArr;
	private TextView mErrorMsg;
	private DataSave obj = new DataSave();
	
	static final int REQUEST_IMAGE_PICK = 1;
	static final int REQUEST_IMAGE_CAPTURE = 2;
	AlertDialog actions;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTitle =(EditText) findViewById(R.id.title);
		mPrice =(EditText) findViewById(R.id.price);
		mLocation = (Spinner) findViewById(R.id.spinnerLocation);
		mCategories = (Spinner) findViewById(R.id.spinnerCategories);
		mButtonSave = (Button) findViewById(R.id.save);
		mButtonCancel = (Button) findViewById(R.id.cancel);
		mImageView = (ImageView)findViewById(R.id.imageView);
		mLocationArray = getResources().getStringArray(R.array.location);
		mCategoryArr = getResources().getStringArray(R.array.category);
		mErrorMsg = (TextView) findViewById(R.id.errorMsg);
		//mTitle.add
		mButtonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!validateData()){
					obj.clear();
					obj.title = mTitle.getText().toString();
					
					obj.price = Float.parseFloat(mPrice.getText().toString());
					obj.location = mLocationArray[mLocation.getSelectedItemPosition()];
					obj.Category = mCategoryArr[mCategories.getSelectedItemPosition()];
					if(obj.location.equals(DataSave.locationFromGPS)){
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setTitle("Location");
						builder.setMessage("Do you want to save with current Location "+DataSave.locationFromGPS);
						builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(userData.contains(obj)){
									Toast.makeText(MainActivity.this, "Duplicate Data", Toast.LENGTH_LONG).show();
								}else{
									userData.add(obj);
									Toast.makeText(MainActivity.this, "User Data", Toast.LENGTH_LONG).show();
								}
								
							}
						});
						
						builder.setCancelable(true);
						actions = builder.create();
						actions.show();
					 
					}
					else{
						if(userData.contains(obj)){
							Toast.makeText(MainActivity.this, "Duplicate Data", Toast.LENGTH_LONG).show();
						}else{
							userData.add(obj);
							Toast.makeText(MainActivity.this, "User Data", Toast.LENGTH_LONG).show();
						}
					}
					
					
					
					
					
			    }else {
			    	mErrorMsg.setVisibility(View.VISIBLE);
			    	mErrorMsg.setText(errMessages);
			    }
			}
		});
		
		mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Existing Images");
			    String[] options = { "Pick Image", "New Pictute"};
			    builder.setItems(options, actionListener);
			    builder.setPositiveButton("New Pictute", null);
			    builder.setCancelable(true);
			    actions = builder.create();
			    actions.show();
				
				
				
			}
		});
		
		
	}
	DialogInterface.OnClickListener actionListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	      switch (which) {
	      case 0: // Delete
	    	  dispatchPickPictureIntent();
	        break;
	      case 1: // Copy
	    	  dispatchTakePictureIntent();
	        break;
	      
	      }
	    }
	  };
	  private void dispatchPickPictureIntent() {
		    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			
		    
		    if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
		    	startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK);
		    }
		}
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
	        Bundle extras = data.getExtras();
	        //
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        
	        mImageView.setImageBitmap(imageBitmap);
	    }else if(requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK){
	    	final Uri imageUri = data.getData();
			InputStream imageStream;
			try {
				imageStream = getContentResolver().openInputStream(imageUri);
				final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
				mImageView.setImageBitmap(selectedImage);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    }
	}
	private boolean validateData(){
		boolean error = false;
		if(TextUtils.isEmpty(mTitle.getText())){
			errMessages = "Input title\n";
	 	    error = true;
		}
	    int selectedIndex = mCategories.getSelectedItemPosition();
	    if(selectedIndex == 0){
	    	errMessages += "Select Category\n";
	    	error = true;
	    }	
		if(TextUtils.isEmpty(mPrice.getText())){
			errMessages += "Input Price";
	 	    error = true;
		}
		errMessages.trim();
		return error;
	}
}
