package com.mrmartin.take_pic_camera;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LaunchCamera extends Activity {
	ImageView imVCature_pic;
	Button btnCapture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch_camera);
		initializeControls();
	}

	private void initializeControls() {
		imVCature_pic=(ImageView)findViewById(R.id.imVCature_pic);
		btnCapture=(Button)findViewById(R.id.btnCapture);
		btnCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/* create an instance of intent
				 * pass action android.media.action.IMAGE_CAPTURE 
				 * as argument to launch camera
				 */
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				/*create instance of File with name img.jpg*/
				File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
				/*put uri as extra in intent object*/
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				/*start activity for result pass intent as argument and request code */
				startActivityForResult(intent, 1);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//if request code is same we pass as argument in startActivityForResult
		if(requestCode==1){
			//create instance of File with same name we created before to get image from storage
			File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
			//Crop the captured image using an other intent
			try {
				/*the user's device may not support cropping*/
				cropCapturedImage(Uri.fromFile(file));
			}
			catch(ActivityNotFoundException aNFE){
				//display an error message if user device doesn't support
				String errorMessage = "Sorry - your device doesn't support the crop action!";
				Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if(requestCode==2){
			//Create an instance of bundle and get the returned data
			Bundle extras = data.getExtras();
			//get the cropped bitmap from extras
			Bitmap thePic = extras.getParcelable("data");
			//print image dimensions
			System.out.println(thePic.getWidth());
			System.out.println(thePic.getHeight());
			//set image bitmap to image view
			imVCature_pic.setImageBitmap(thePic);
			//inform the user that texture synthesis is going to happen
			Toast.makeText(LaunchCamera.this, R.string.synthesis_warning, Toast.LENGTH_SHORT).show();
		}
	}
	//create helping method cropCapturedImage(Uri picUri)
	public void cropCapturedImage(Uri picUri){
		//call the standard crop action intent 
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		//indicate image type and Uri of image
		cropIntent.setDataAndType(picUri, "image/*");
		//set crop properties
		cropIntent.putExtra("crop", "true");
		//indicate aspect of desired crop - no limits
		//cropIntent.putExtra("aspectX", 1);
		//cropIntent.putExtra("aspectY", 1);
		//indicate output X and Y
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		//retrieve data on return
		cropIntent.putExtra("return-data", true);
		//start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, 2);
	}
}
