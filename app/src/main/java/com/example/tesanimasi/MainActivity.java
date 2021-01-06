package com.example.tesanimasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Runnable, SensorEventListener {

    Thread t;
    Button btn,btn2;
    ImageView marker,warna;
    private ViewGroup mainLayout;
    private Line customCanvas;

    int color;

    private float xCoOrdinate, yCoOrdinate;
    private boolean walking = false;
    int[] imageCordinates = new int[2];

    private int AbsoluteX = 0;
    private int AbsoluteY = 0;


    private Timer timer = new Timer();
    private Handler handler = new Handler();

    private float boxX, boxY;

//    private static final int WRITE_PERMISSION_RQST = 100;
    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    int orientationValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.tombol);
        btn2 = (Button) findViewById(R.id.tombol2);
        marker = (ImageView) findViewById(R.id.marker);
        mainLayout = (ConstraintLayout)findViewById(R.id.background);
        customCanvas = (Line) findViewById(R.id.signature_canvas);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        warna  = (ImageView) findViewById(R.id.warna);
        t=new Thread(this);


        marker.setOnTouchListener(onTouchListener());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.start();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t != null) t.interrupt();
            }
        });

    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        xCoOrdinate = v.getX() - event.getRawX();
                        yCoOrdinate = v.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        marker.getLocationOnScreen(imageCordinates);
                        int x = imageCordinates[0];
                        int y = imageCordinates[1];

                        Toast.makeText(MainActivity.this, "position: "+x, Toast.LENGTH_SHORT).show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        v.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                        break;

                    default:
                        return false;

                }
                return true;
            }
        };
    }

    public void putImage(int x, int y, int warna) {
//                    int x = 0,y = 0;
        System.out.println("gg : "+AbsoluteX+AbsoluteY);
        if(AbsoluteX == 0 && AbsoluteY == 0) {
            customCanvas.startTouch(x, y, warna);
            customCanvas.invalidate();
        }
        else {
            customCanvas.moveTouch(x, y, warna);
            customCanvas.invalidate();
        }
        AbsoluteX = x;
        AbsoluteY = y;
    }

    static int i=0;
    @Override
    public void run() {
        try {
            while(true)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marker.getLocationOnScreen(imageCordinates);
                        int x = imageCordinates[0];
                        int y = imageCordinates[1];


//                        marker.setX(x-25);
//                        putImage(x+40,y);

                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        // Level of current connection
                        int rssi = wifiManager.getConnectionInfo().getRssi();
                        String ssid = wifiManager.getConnectionInfo().getSSID();
                        if(rssi >= -57){ //atur warna rssi
                            warna.setBackgroundColor(0xFF00FF00);
                            color = 0xFF00FF00;

                        }else if (rssi < -57 && rssi >= -75){
                            warna.setBackgroundColor(Color.rgb(50, 168, 153));
                            color = Color.rgb(50, 168, 153);
                        }else{
                            warna.setBackgroundColor(0x000000);
                            color = 0x000000;
                        }

                        if (getOrientationLabel(orientationValue) == "S"){
                        Toast.makeText(MainActivity.this,"UP",Toast.LENGTH_SHORT).show();
                            //atas
                            marker.setY(y-100);
                            putImage(x+60,y-5,color);
                        }else if (getOrientationLabel(orientationValue) == "W"){
                            Toast.makeText(MainActivity.this,"RIGHT",Toast.LENGTH_SHORT).show();
                            //kanan
                            marker.setX(x+25);
                            putImage(x+80,y,color);
                        }else if (getOrientationLabel(orientationValue) == "E"){
                            Toast.makeText(MainActivity.this,"LEFT",Toast.LENGTH_SHORT).show();
                            //kiri
                            marker.setX(x-25);
                            putImage(x+40,y,color);
                        }else if (getOrientationLabel(orientationValue) == "N"){
                            Toast.makeText(MainActivity.this,"TURN",Toast.LENGTH_SHORT).show();
                            //bawah
                            marker.setY(y-60);
                            putImage(x+60,y+5,color);
                        }


    //                  marker.setY(y);
                        Log.i("COORD X","X: "+marker.getX());
    //                  Log.i("COORD Y","Y: "+locations[1]);
                        i++;
                    }
                });
                Thread.sleep(1000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);

            updateOrientationAngles();

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);

            updateOrientationAngles();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(MainActivity.this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(MainActivity.this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
//        orientationTextView.setText("No orientation");
    }

    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "mRotationMatrix" now has up-to-date information.
        SensorManager.getOrientation(rotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.
        orientationValue = gerOrientationValue();
        System.out.println("Label : "+getOrientationLabel(orientationValue));
//        outcomeTextView.setText(getOrientationLabel(orientationValue));
//        orientationTextView.setText("id: " + orientationValue);

//        if(snackbar.isShown()){
//            String line =   accelerometerReading[0] + "," + accelerometerReading[1] + "," + accelerometerReading[2] + "," +
//                    magnetometerReading[0] + "," + magnetometerReading[1] + "," + magnetometerReading[2] + "," +
//                    mOrientationAngles[0] + "," + mOrientationAngles[1] + "," + mOrientationAngles[2] + "," +
//                    rotationMatrix[0] + "," + rotationMatrix[1] + "," + rotationMatrix[2] + "," +
//                    rotationMatrix[3] + "," + rotationMatrix[4] + "," + rotationMatrix[5] + "," +
//                    rotationMatrix[6] + "," + rotationMatrix[7] + "," + rotationMatrix[8] ;
//
//            String sample = line + "," + orientationValue + "\n";

//            csvString += sample;
//        }
    }

    private int gerOrientationValue() {
                /*
        Azimuth, angle of rotation about the -z axis.
        This value represents the angle between the device's y axis and the magnetic north pole.
        When facing north, this angle is 0, when facing south, this angle is π.
        Likewise, when facing east, this angle is π/2, and when facing west, this angle is -π/2.
        The range of values is -π to π.
        Device axis
        https://developer.android.com/images/axis_device.png
        Globe axis
        https://developer.android.com/images/axis_globe.png
        * */

        float azimuth = (float) mOrientationAngles[0]; // orientation

        int orientation = 1000;
        float tolerance = (float) Math.PI / 8;

        float NORTH = 0f;
        float MIN_NORTH = (NORTH - tolerance);
        float MAX_NORTH = (NORTH + tolerance);

        float SOUTH = (float) Math.PI;
        float MIN_SOUTH = (SOUTH - tolerance);

        float EAST = (float) Math.PI / 2;
        float MIN_EAST = (EAST - tolerance);
        float MAX_EAST = (EAST + tolerance);

        float WEST = -EAST;
        float MIN_WEST = (WEST - tolerance);
        float MAX_WEST = (WEST + tolerance);

        if (azimuth >= MIN_NORTH && azimuth <= MAX_NORTH) {//NORTH
            orientation = 0;

        } else {

            if (azimuth > MAX_NORTH && azimuth < MIN_EAST) {//NORTHEAST
                orientation = 1;

            } else {

                if (azimuth >= MIN_EAST && azimuth <= MAX_EAST) {//EAST
                    orientation = 2;

                } else {

                    if (azimuth > MAX_EAST && azimuth < MIN_SOUTH) {//SOUTHEAST
                        orientation = 3;

                    } else {

                        if (azimuth >= MIN_WEST && azimuth <= MAX_WEST) {//WEST
                            orientation = 4;

                        } else {

                            if (azimuth < MIN_WEST && azimuth > -MIN_SOUTH) {//SOUTHWEST
                                orientation = 5;

                            } else {

                                if (azimuth > MAX_WEST && azimuth < MIN_NORTH) {//NORTHWEST
                                    orientation = 6;

                                } else {
                                    if (azimuth > MIN_SOUTH || azimuth < -MIN_SOUTH) {//SOUTH
                                        orientation = 7;

                                    }

                                }


                            }

                        }
                    }

                }

            }


        }

        return orientation;
    }

    private String getOrientationLabel(int orientationValue) {

        switch (orientationValue){
            case 0:
                return "N";//NORTH
            case 1:
                return "NE";//NORTHEAST
            case 2:
                return "E";//EAST
            case 3:
                return "SE";//SOUTHEAST
            case 4:
                return "W";//WEST
            case 5:
                return "SW";//SOUTHWEST
            case 6:
                return "NW";//NORTHWEST
            case 7:
                return "S";//SOUTH
        }

        return null;
    }


//    public void changePos() {
//
//    }


//    public void moveAnimation(){
//        Animation img = new TranslateAnimation(0,-150,0,0);
//        img.setDuration(300);
//        img.setFillAfter(true);
//
//
//        marker.startAnimation(img);
//    }
}