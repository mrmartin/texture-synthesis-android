package net.martaskolda.meep.tbv.texture;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View.OnClickListener;

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
import net.martaskolda.meep.tbv.TiledBitmapView;

/**
 * A simple demo activity to demonstrate how to interact with the TiledBitmapView, including registering a provider.
 */
public class TBVDemoActivity extends Activity {

    TiledBitmapView tiledBitmapView;
    //Button btnBackToOrigin;
    Button btnAbout;
    ToggleButton btnToggleDebug;
    //ToggleButton btnToggleProvider;
    DemoTileProvider demoProvider;

    //ImageView imVCature_pic;
    Button btnCapture;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("resynth");
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String runResynth();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tiledBitmapView = (TiledBitmapView) findViewById(R.id.simpleTileView);
        //btnBackToOrigin = (Button) findViewById(R.id.btn_backToOrigin);
        btnToggleDebug = (ToggleButton) findViewById(R.id.btn_debug_toggle);
        //btnToggleProvider = (ToggleButton) findViewById(R.id.btn_provider_toggle);
        btnAbout = (Button) findViewById(R.id.btn_about);

        // attach our 'Jump to Origin' button to the TBV method that centers the grid around tile 0,0
        /*btnBackToOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tiledBitmapView.moveToOriginTile();
            }
        });*/

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
        //btnToggleProvider.setChecked(true);

        // and allow toggling between it and the dummy provider that comes with the TBV by default
        /*btnToggleProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnToggleProvider.isChecked()) {
                    tiledBitmapView.registerProvider(demoProvider);
                } else {
                    tiledBitmapView.registerDefaultProvider();
                }
            }
        });*/

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
                boolean success = (new File(Environment.getExternalStorageDirectory() + "/texture_synthesis")).mkdir();
				/* create an instance of intent
				 * pass action android.media.action.IMAGE_CAPTURE
				 * as argument to launch camera
				 */
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				/*create instance of File with name img.jpg*/
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "/texture_synthesis/img.jpg");
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
            File file = new File(Environment.getExternalStorageDirectory()+File.separator + "texture_synthesis"+File.separator +"img.jpg");
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
            //write image to file
            try {
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "texture_synthesis"+File.separator +"cropped.jpg");
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                thePic.compress(Bitmap.CompressFormat.JPEG,100,os);
                os.close();
            }catch (Exception e){
                Toast.makeText(TBVDemoActivity.this, "Error writing cropped image to file", Toast.LENGTH_SHORT).show();
                System.out.println("Error writing cropped image to file");
            }
            //imVCature_pic.setImageBitmap(thePic);
            //inform the user that texture synthesis is going to happen
            Toast.makeText(TBVDemoActivity.this, R.string.synthesis_warning, Toast.LENGTH_SHORT).show();
            System.out.println(runResynth());
            //Toast.makeText(TBVDemoActivity.this, R.string.synthesis_warning, Toast.LENGTH_SHORT).show();
            Bitmap resynth_output = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory()+File.separator + "texture_synthesis"+File.separator +"cropped.synthesized.png").getAbsolutePath());
            Bitmap tileable = Bitmap.createScaledBitmap(resynth_output, 512, 512, true);

            //set captured bitmap as tile
            demoProvider.captured_bmp=tileable;
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
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, 2);
    }
}


