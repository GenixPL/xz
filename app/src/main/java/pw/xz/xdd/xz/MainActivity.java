package pw.xz.xdd.xz;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters;
import com.indoorway.android.common.sdk.model.IndoorwayPosition;
import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.indoorway.android.fragments.sdk.map.MapFragment;
import com.indoorway.android.location.sdk.IndoorwayLocationSdk;
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError;
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkState;
import com.indoorway.android.map.sdk.view.IndoorwayMapView;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler defaultUEH;

        private Activity app = null;

        public TopExceptionHandler(Activity app) {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
            this.app = app;
        }

        public void uncaughtException(Thread t, Throwable e) {
            StackTraceElement[] arr = e.getStackTrace();
            String report = e.toString() + "\n\n";
            report += "--------- Stack trace ---------\n\n";
            for (int i = 0; i < arr.length; i++) {
                report += "    " + arr[i].toString() + "\n";
            }
            report += "-------------------------------\n\n";

// If the exception was thrown in a background thread inside
// AsyncTask, then the actual exception can be found with getCause
            report += "--------- Cause ---------\n\n";
            Throwable cause = e.getCause();
            if (cause != null) {
                report += cause.toString() + "\n\n";
                arr = cause.getStackTrace();
                for (int i = 0; i < arr.length; i++) {
                    report += "    " + arr[i].toString() + "\n";
                }
            }
            report += "-------------------------------\n\n";

            try {
                FileOutputStream trace = app.openFileOutput(
                        "stack.trace", Context.MODE_PRIVATE);
                trace.write(report.getBytes());
                trace.close();
            } catch (IOException ioe) {
// ...
            }

            defaultUEH.uncaughtException(t, e);
        }
    }

    public IndoorwayMapView indoorwayMapView;
    public IndoorwayMap currentMap;
    public SQLiteDbHelper database;
    public MarkersLayer myLayer;
    private RoomProximityDetector detector;
    private IndoorwayPosition currentPosition;

    private String lastRoomId="";

    TextView tx;
    CardView cardView;
    String tex;

    //Layout variables DO NOT CHANGE
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        String line;
        String trace = "";
        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(this
                            .openFileInput("stack.trace")));
            while ((line = reader.readLine()) != null) {
                trace += line + "\n";
            }
        } catch (FileNotFoundException fnfe) {
// ...
        } catch (IOException ioe) {
// ...
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Error report";
        String body =
                "Mail this to appdeveloper@gmail.com: " +
                        "\n" +
                        trace +
                        "\n";

        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"emilm7843@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        this.startActivity(
                Intent.createChooser(sendIntent, "Title:"));

        this.deleteFile("stack.trace");

        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        SQLiteDb sql = new SQLiteDb(getApplicationContext());
        sql.sqliteDbUpdateOnce(getApplicationContext());
        database = new SQLiteDbHelper(getApplicationContext());
        indoorwayMapView = findViewById(R.id.mapView);

        initializeMap();
        displayUser();
        initStatusListeners();
        initNavigationDrawer();

    }

    private void initStatusListeners() {
        Action1<IndoorwayLocationSdkError> sdkErrListener = new Action1<IndoorwayLocationSdkError>() {
            @Override
            public void onAction(IndoorwayLocationSdkError error) {
                if (error instanceof IndoorwayLocationSdkError.BleNotSupported) {
                    // Bluetooth Low Energy is not supported, positioning service will be stopped, it can't work
                    toastMessage("Low energy error");
                } else if (error instanceof IndoorwayLocationSdkError.MissingPermission) {
                    // Some permissions are missing, ask for it.permission
                    toastMessage("Permissions error");
                } else if (error instanceof IndoorwayLocationSdkError.BluetoothDisabled) {
                    // Bluetooth is disabled, user have to turn it on
                    toastMessage("Disabled bluetooth error");
                } else if (error instanceof IndoorwayLocationSdkError.LocationDisabled) {
                    // Location is disabled, user have to turn it on
                    toastMessage("Disabled location error");
                } else if (error instanceof IndoorwayLocationSdkError.UnableToFetchData) {
                    // Network-related error, service will be restarted on network connection established
                    toastMessage("Network-related error");
                } else if (error instanceof IndoorwayLocationSdkError.NoRadioMaps) {
                    // Measurements have to be taken in order to use location
                    toastMessage("Measurements error");
                }
            }
        };

        IndoorwayLocationSdk.instance()
                .state()
                .onError()
                .register(sdkErrListener);

        Action1<IndoorwayLocationSdkState> sdkStateListener = new Action1<IndoorwayLocationSdkState>() {
            @Override
            public void onAction(IndoorwayLocationSdkState indoorwayLocationSdkState) {
                // handle state changes
                toastMessage(indoorwayLocationSdkState.toString());
            }
        };

        IndoorwayLocationSdk.instance()
                .state()
                .onChange()
                .register(sdkStateListener);
    }

    private void displayUser(){
        Action1<IndoorwayPosition> positionListener = new Action1<IndoorwayPosition>() {
            @Override

            public void onAction(IndoorwayPosition position) {
                // store last position as a field
                currentPosition = position;

                // react for position changes...

                // If you are using map view, you can pass position.
                // Second argument indicates if you want to auto reload map on position change
                // for eg. after going to different building level.
                boolean isChangingFloor= false;
                String roomid="";
                Room room;
                kotlin.Pair<Room, Double> roomData = detector.getNearestRoom(position.getCoordinates());
                try {

                    room = roomData.component1();
                    roomid = room.getId();
                }
                catch(Exception e){
                    //ustalone eksperymentlanie, ze podczas zmiany pietra cos sie psuje
                    isChangingFloor=true;
                }
                if (!roomid.equals(lastRoomId) && !isChangingFloor) {
                    lastRoomId = roomid;
                    Calendar rightNow = Calendar.getInstance();
                    int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                    int currentMinutes = rightNow.get(Calendar.MINUTE);
                    int currentDay = rightNow.get(Calendar.DAY_OF_WEEK);
                    String day = "";

                    switch (currentDay) {
                        case Calendar.MONDAY:
                            day = "Monday";
                            break;
                        case Calendar.TUESDAY:
                            day = "Tuesday";
                            break;
                        case Calendar.WEDNESDAY:
                            day = "Wendnesday";
                            break;
                        case Calendar.THURSDAY:
                            day = "Thursday";
                            break;
                        case Calendar.FRIDAY:
                            day = "Friday";
                            break;
                        case Calendar.SUNDAY:
                            day = "Sunday";
                            break;
                        case Calendar.SATURDAY:
                            day = "Saturday";
                            break;
                    }

                    indoorwayMapView.getSelection().selectObject(roomData.component1().getId());
                    indoorwayMapView.getPosition().setPosition(currentPosition, true);

                    //List<Lecture> lectures = database.getByRoomAndTime(room.getId(),currentHour,currentMinutes, day);
                    List<Lecture> lectures = database.getByRoomAndTime("3-_M01M3r5w_c1a68",
                            20,20, "Saturday");

                    tx = findViewById(R.id.tx);
                    //tx.setText(room.getId() + "\n" + currentHour + ":" + currentMinutes + ", " + day);
                    //tx.setText(lectures.get(0).getName());
                    tx.setText("tekst");
                    cardView = findViewById(R.id.card_view);
                    tx.setGravity(Gravity.CENTER);


                    tx.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.VISIBLE);


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tx.setVisibility(View.INVISIBLE);
                            cardView.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);


                }

            }
        };

        IndoorwayLocationSdk.instance()
                .position()
                .onChange()
                .register(positionListener);
    }


    private void initializeMap() {
        indoorwayMapView.load("CScrSxCVhQg", "3-_M01M3r5w");

        indoorwayMapView.getTouch().setOnClickListener(new Action1<Coordinates>() {
            @Override
            public void onAction(Coordinates coordinates) {
                //toastMessage(coordinates.toString());
                //indoorwayMapView.getSelection().selectObject();

                //tutaj jest hack na to, zeby sie nie odznaczalo nic na tapniecie na cos
                if (lastRoomId!=null){ //jezeli cos bylo zaznaczone
                    //to sie zaznacza jeszcze raz xd
                    indoorwayMapView.getSelection().selectObject(lastRoomId);
                }


                try {
                    List<IndoorwayObjectParameters> result = currentMap.objectsContainingCoordinates(coordinates);

                    //tx.setText(result.get(0).getName() + "");
                    tex = result.get(0).getId() + "";
                    toastMessage(tex);
                } catch (Exception e) {
                    toastMessage("error byl");
                }
            }
        });

        indoorwayMapView.setOnMapLoadCompletedListener(new Action1<IndoorwayMap>() {
            @Override
            public void onAction(IndoorwayMap indoorwayMap) {
                currentMap = indoorwayMap;

                detector = new RoomProximityDetector(indoorwayMap, indoorwayMapView);
                detector.getAllRooms();

                myLayer = indoorwayMapView.getMarker().addLayer(0);
            }
        });

    }

    private void initNavigationDrawer(){
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        navView = findViewById(R.id.nav);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                navRoomFragment navRoomFragment = new navRoomFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_Fragment, navRoomFragment).commit();

                return true;
            }
        });

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public boolean onOptionsItemSelected(MenuItem item){

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toastMessage(String message){
        //Snackbar snackbar = Snackbar.make(findViewById(R.id.cor), message, 7000);
        //snackbar.show();

        Toast toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
        toast.show();

    }

}
