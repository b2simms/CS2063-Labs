package mobiledev.unb.ca.lab6skeleton;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.ACTION_BATTERY_LOW;
import static android.content.Intent.ACTION_BATTERY_OKAY;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(
                MainActivity.this, 234324243, intent, 0);

        // Hopefully your alarm will have a lower frequency than this!
        alarmMgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        setAlarm();

        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BATTERY_LOW);
        intentFilter.addAction(ACTION_BATTERY_OKAY);

        registerReceiver(batteryInfoReceiver,intentFilter);

    }

    private void setAlarm() {
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 1000,
                AlarmManager.INTERVAL_HALF_HOUR / 30, alarmIntent);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "Error creating image file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        //File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            galleryAddPic();
        }
    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.i("batteryInfoReceiver", "intent.getAction() " + s);
            if (s.equals(ACTION_BATTERY_LOW)) {
                Toast toast = Toast.makeText(MainActivity.this, "Action Battery LOW", Toast.LENGTH_LONG);
                toast.show();
                Log.i("batteryInfoReceiver", "Action Battery LOW");
                // If the alarm has been set, cancel it.
                if (alarmMgr != null) {
                    alarmMgr.cancel(alarmIntent);
                }
            } else {
                Toast toast = Toast.makeText(MainActivity.this, "Action Battery OKAY", Toast.LENGTH_LONG);
                toast.show();
                Log.i("batteryInfoReceiver", "Action Battery OKAY");
                // If the alarm has been set, cancel it.
                if (alarmMgr != null) {
                    setAlarm();
                }
            }
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(batteryInfoReceiver);
    }

}
