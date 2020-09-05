package com.loki.fakelocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener , View.OnClickListener {

    TextView textView,textView2,textView3;
    Button button;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    static double latitude, longitude;

    Button btn,btn2;
    Button  btn3,btn4,btn5,btn6;
    EditText editText1,editText3;


    SharedPreferences sharedPreferences;
    public static final String mypref="mypref";
    public static final String Number="number";

    DatabaseReference rootRef, demoRef ;
    DatabaseReference demoMsg;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        textView = findViewById(R.id.tvLocation);
        button = findViewById(R.id.btnLocation);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    return;

                }
                mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLocation == null) {
                    startLocationUpdates();
                }
                if (mLocation != null) {
                    latitude = mLocation.getLatitude();
                    longitude = mLocation.getLongitude();
                    textView.setText(String.valueOf(latitude + " , " + longitude));
                }
            }
        });


        editText1 =  findViewById(R.id.getNo);
        editText3 = findViewById(R.id.getMsg);

        textView2 = findViewById(R.id.sendMsg);
        textView3=  findViewById(R.id.getData);

        btn2=  findViewById(R.id.btn2);
        btn3=  findViewById(R.id.btn3);
        btn4=  findViewById(R.id.btn4);
        btn5=  findViewById(R.id.btn5);
        btn6=  findViewById(R.id.btn6);


        btn=  findViewById(R.id.btn);
        btn.setOnClickListener(this);


        //reff = FirebaseDatabase.getInstance().getReference();


        rootRef = FirebaseDatabase.getInstance().getReference();
        // Database reference pointing to demo node
        demoRef = rootRef.child("demo");
        demoMsg = rootRef.child("Messages");




        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                demoMsg.setValue(editText3.getText().toString());
                editText3.setText("");

            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoMsg.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        textView2.setText(value);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                CharSequence label = "";
                ClipData clip = ClipData.newPlainText(label, textView.getText().toString());
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });

        textView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                CharSequence label = "";
                ClipData clip = ClipData.newPlainText(label, textView2.getText().toString());
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });

        textView3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                CharSequence label = "";
                ClipData clip = ClipData.newPlainText(label, textView3.getText().toString());
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });

        editText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText1.setFocusable(true);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                demoRef.setValue(textView.getText().toString());
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        textView3.setText(value);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        sharedPreferences=getSharedPreferences(mypref, Context.MODE_PRIVATE);

        if(sharedPreferences.contains(Number)){
            editText1.setText(sharedPreferences.getString(Number,""));
        }


    }






    @Override
    public void onClick(View v){
        String bt1_Number = editText1.getText().toString();
        String bt2_Sms = textView.getText().toString();

        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Number,bt1_Number);
        editor.commit();

        try {
            SmsManager sms = null;
            //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                sms = SmsManager.getSmsManagerForSubscriptionId(0);
            }
            sms.sendTextMessage(bt1_Number, "", bt2_Sms, null, null);
            //}


        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }



       /* Intent mailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:?subject=" + "cordinates"+ "&body=" + "27.232,45455" + "&to=" + "mdvishalagrawal@gmail.com");
        mailIntent.setData(data);
        startActivity(Intent.createChooser(mailIntent, "Send mail..."));


        */


        /*

        try {
            // Construct data
            String apiKey = "apikey=" + "HvkCaDH6nkg-OXRI6N281ZcMHLNBoe4ug9aPR1o90w";
            String message = "&message=" +  bt2_Sms;
            String sender = "&sender=" + "Vishal";
            String numbers = "&numbers=" + bt1_Number;

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data =  apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                //stringBuffer.append(line);
                Toast.makeText(this, "the message is "+line, Toast.LENGTH_SHORT).show();
            }
            rd.close();

            //return stringBuffer.toString();
        } catch (Exception e) {
            //System.out.println("Error SMS "+e);
            Toast.makeText(this, "Error "+e, Toast.LENGTH_SHORT).show();
            //return "Error "+e;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


         */


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;

        }
    }

    protected void startLocationUpdates() {
        locationRequest = LocationRequest.create();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


}