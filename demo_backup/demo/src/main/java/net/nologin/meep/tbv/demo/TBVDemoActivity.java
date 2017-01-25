/*
 *    TiledBitmapViewDemo - An Android application demonstrating the abilities of the TiledBitmapView library
 *    Copyright 2013 Barry O'Neill (http://meep.nologin.net/)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.nologin.meep.tbv.demo;

import java.io.File;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import net.nologin.meep.tbv.TiledBitmapView;

/**
 * A simple demo activity to demonstrate how to interact with the TiledBitmapView, including registering a provider.
 */
public class TBVDemoActivity extends Activity {

    TiledBitmapView tiledBitmapView;
    Button btnBackToOrigin, btnAbout;
    ToggleButton btnToggleDebug, btnToggleProvider;
    DemoTileProvider demoProvider;

    //ImageView imVCature_pic;
    Button btnCapture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tiledBitmapView = (TiledBitmapView) findViewById(R.id.simpleTileView);
        btnBackToOrigin = (Button) findViewById(R.id.btn_backToOrigin);
        btnToggleDebug = (ToggleButton) findViewById(R.id.btn_debug_toggle);
        btnToggleProvider = (ToggleButton) findViewById(R.id.btn_provider_toggle);
        btnAbout = (Button) findViewById(R.id.btn_about);

        // attach our 'Jump to Origin' button to the TBV method that centers the grid around tile 0,0
        btnBackToOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tiledBitmapView.moveToOriginTile();
            }
        });

        // Setup the toggle button that enables and disables the render of debug information on the TBV
        btnToggleDebug.setChecked(tiledBitmapView.isDebugEnabled());
        btnToggleDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tiledBitmapView.toggleDebugEnabled();
                if (tiledBitmapView.isDebugEnabled()) {
                    // debug incurs a performance hit, perhaps better not to expose it to end users in your app
                    Toast.makeText(TBVDemoActivity.this, R.string.debug_warning, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // register our 'demo' tile provider
        demoProvider = new DemoTileProvider(this);
        tiledBitmapView.registerProvider(demoProvider);
        btnToggleProvider.setChecked(true);

        // and allow toggling between it and the dummy provider that comes with the TBV by default
        btnToggleProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnToggleProvider.isChecked()) {
                    tiledBitmapView.registerProvider(demoProvider);
                } else {
                    tiledBitmapView.registerDefaultProvider();
                }
            }
        });

        // attach about dialog
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = TBVDemoActivity.this;

                final TextView message = new TextView(ctx);

                final SpannableString s =
                        new SpannableString(ctx.getText(R.string.dialog_about_text));
                Linkify.addLinks(s, Linkify.WEB_URLS);
                message.setText(s);
                message.setMovementMethod(LinkMovementMethod.getInstance());

                new AlertDialog.Builder(ctx)
                        .setTitle(R.string.dialog_about_title)
                        .setCancelable(true)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(android.R.string.ok, null)
                        .setView(message)
                        .create().show();
            }
        });

        //imVCature_pic=(ImageView)findViewById(R.id.imVCature_pic);
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
            //imVCature_pic.setImageBitmap(thePic);
            //inform the user that texture synthesis is going to happen
            Toast.makeText(TBVDemoActivity.this, R.string.synthesis_warning, Toast.LENGTH_SHORT).show();
            //set captured bitmap as tile
            demoProvider.captured_bmp=thePic;
            demoProvider.use_captured_bmp=true;
            demoProvider.Refresh();
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


