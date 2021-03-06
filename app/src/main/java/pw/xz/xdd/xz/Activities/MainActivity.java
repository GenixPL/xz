package pw.xz.xdd.xz.Activities;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters;
import com.indoorway.android.common.sdk.model.IndoorwayPosition;
import com.indoorway.android.location.sdk.IndoorwayLocationSdk;
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkError;
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkState;
import com.indoorway.android.map.sdk.view.IndoorwayMapView;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pw.xz.xdd.xz.Other.BuildingInformations;
import pw.xz.xdd.xz.Other.CalendarToStringConverter;
import pw.xz.xdd.xz.Database.SQLiteDb;
import pw.xz.xdd.xz.Database.SQLiteDbHelper;
import pw.xz.xdd.xz.Other.InitApp;
import pw.xz.xdd.xz.Other.Lecture;
import pw.xz.xdd.xz.Other.User;
import pw.xz.xdd.xz.R;
import pw.xz.xdd.xz.Other.Room;
import pw.xz.xdd.xz.Other.RoomProximityDetector;
import pw.xz.xdd.xz.Other.RoomTools;

public class MainActivity extends AppCompatActivity {


    public IndoorwayMapView indoorwayMapView;
    public IndoorwayMap currentMap;
    public SQLiteDbHelper database ;
    public MarkersLayer myLayer;
    private RoomProximityDetector detector;
    private IndoorwayPosition currentPosition;
    private double MAX_DETECTION_RANGE = 12;

    private Coordinates actualIndoorwayPosition;
    TextView tx, sub, text;
    CardView cardView;

    //Layout variables DO NOT CHANGE
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
    private ListView navListView;
    private ListView  listView_lectures;
    //regarding tapping
    private String lastRoomId = "";
    private Boolean wasLastRoomInfoActivatedByProximitySensor=false;
    private Boolean isRoomInfoVisible = false;

    //Do anulowania handlerow
    Handler globalInfoBarHandler1;
    Handler globalInfoBarHandler2;
    Runnable callback;


    User exampleUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                InitApp.MY_PERMISSIONS_ACCESS_FINE_LOCATION);

        setContentView(R.layout.activity_main);
        SQLiteDb sql = new SQLiteDb();
        sql.sqliteDbUpdateOnce(getApplicationContext());
        database = new SQLiteDbHelper(getApplicationContext());
        indoorwayMapView = findViewById(R.id.mapView);
        navListView = findViewById(R.id.navigation_listView);
        listView_lectures = findViewById(R.id.listView_lectures);
        //exampleuser
        exampleUser = new User(6, database.getByRoomAndTime("3-_M01M3r5w_fe9c8",
                12, "Wednesday") );

        initializeMap();
        initListView();
        cardView = findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              animateOutInfoBar();
            }
        });


    }
    private void animateOutInfoBar(){
        Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_out);
        cardView.startAnimation(a);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardView.setVisibility(View.INVISIBLE);
            }
        }, 450) ;
        isRoomInfoVisible=false;
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

    private void setupClientPositionListener(){
        Action1<IndoorwayPosition> positionListener = new Action1<IndoorwayPosition>() {
            @Override

            public void onAction(IndoorwayPosition position) {

                currentPosition = position;
                actualIndoorwayPosition = position.getCoordinates();
                boolean isChangingFloor = false;
                String roomid = "";

                kotlin.Pair<Room, Double> roomData = detector.getNearestRoom(actualIndoorwayPosition);
                Room room = roomData.component1();
                try {
                    roomid = room.getId();
                } catch (Exception e) {
                    //ustalone eksperymentlanie, ze podczas zmiany pietra cos sie psuje
                    isChangingFloor = true;
                }
                if (!roomid.equals(lastRoomId) && !isChangingFloor && roomData.component2() < MAX_DETECTION_RANGE && (!!wasLastRoomInfoActivatedByProximitySensor||!isRoomInfoVisible)) {
                        wasLastRoomInfoActivatedByProximitySensor=true;
                        displayInformationAboutRoom(room);


                }
            }
        };

        IndoorwayLocationSdk.instance()
                .position()
                .onChange()
                .register(positionListener);
    }
    private List<Lecture> getLecturesForCurrentDatetime(Room room){
        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = rightNow.get(Calendar.MINUTE);
        int currentDay = rightNow.get(Calendar.DAY_OF_WEEK);

        CalendarToStringConverter converter = new CalendarToStringConverter();
        String day = converter.getDays().get(currentDay);

        indoorwayMapView.getSelection().selectObject(room.getId());
        indoorwayMapView.getPosition().setPosition(currentPosition, true);
        Log.d("myDebug","romId:" + room.getId());
        Log.d("myDebug","currentHour:" + currentHour);
        Log.d("myDebug","currentMinutes:" + currentMinutes);
        Log.d("myDebug","day:" + day);

        List<Lecture> lectures = database.getByRoomAndTime(room.getId(), currentHour, day);
        return lectures;
    }
    private void displayInformationAboutRoom(Room room){
        lastRoomId = room.getId();
        List<Lecture> lectures = getLecturesForCurrentDatetime(room);
        //List<Lecture> lectures = database.getByRoomAndTime("3-_M01M3r5w_c1a68", 20,20, "Saturday");

        tx = findViewById(R.id.tx);
        sub = findViewById(R.id.sub);
        text = findViewById(R.id.text);
        //tx.setText(room.getId() + "\n" + currentHour + ":" + currentMinutes + ", " + day);
        Log.d("myDebug","number of results:" + lectures.size());

        if (!lectures.isEmpty())
        {
            tx.setText(lectures.get(0).getName());
            String text_tmp = lectures.get(0).getStartTimeHH() + ":"
                    + lectures.get(0).getStartTimeMM() + ", " + lectures.get(0).getDay();
            sub.setText(text_tmp);
            text.setText(lectures.get(0).getDescription());

            animateCardInAndOut();
            }
            else if (isRoomInfoVisible){
                animateOutInfoBar();
            }
        }



    private void animateCardInAndOut(){

        isRoomInfoVisible=true;

        cardView.setVisibility(View.VISIBLE);

        Animation animateIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_in);
        cardView.startAnimation(animateIn);




    }
    private void initializeMap() {
        indoorwayMapView.load(BuildingInformations.Companion.getBuildingID(), BuildingInformations.Companion.getStartFloorID());



        indoorwayMapView.setOnMapLoadCompletedListener(new Action1<IndoorwayMap>() {
            @Override
            public void onAction(IndoorwayMap indoorwayMap) {
                currentMap = indoorwayMap;

                setupClientPositionListener();
                initStatusListeners();
                initNavigationDrawer();

                RoomTools.Companion.getAllRooms(currentMap); // read room data
                detector = new RoomProximityDetector(currentMap, indoorwayMapView);


                configureOnClick();

                myLayer = indoorwayMapView.getMarker().addLayer(0);
            }
        });

    }
    private void configureOnClick(){
        indoorwayMapView.getTouch().setOnClickListener(new Action1<Coordinates>() {
            @Override
            public void onAction(Coordinates coordinates) {


                //tutaj jest hack na to, zeby sie nie odznaczalo nic na tapniecie na cos

                    try {
                        List<IndoorwayObjectParameters> result = currentMap.objectsContainingCoordinates(coordinates);
                        if ( lastRoomId!=null && lastRoomId.equals(result.get(0).getId())){
                            if (isRoomInfoVisible) {
                                animateOutInfoBar();
                                fillLecturesView(getLecturesForCurrentDatetime(RoomTools.Companion.getRoomByID(result.get(0).getId())));
                            }
                            //toastMessage("czy to tutaj");

                        }
                        else {

                            displayInformationAboutRoom(RoomTools.Companion.getRoomByID(result.get(0).getId()));
                        }
                        wasLastRoomInfoActivatedByProximitySensor = false;


                    } catch (IndexOutOfBoundsException e) {
                    if (isRoomInfoVisible)
                        animateOutInfoBar();
                    listView_lectures.setVisibility(View.INVISIBLE);
                    navListView.setVisibility(View.INVISIBLE);
                    }

                }



        });
    }

    private void initListView(){


        String[] buttons = {"Navigate to 216", "Navigate to 213", "Navigate to 212", "Navigate to 211", "Navigate to 214", "Navigate to 103", "Navigate to 107"};
        ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, buttons);
        navListView.setAdapter(listAdapter);


        navListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        navListView.setVisibility(View.INVISIBLE);

                        if(i == 0){
                            indoorwayMapView.getNavigation().start(currentPosition, "3-_M01M3r5w_ca808");

                        } else if (i == 1) {
                            indoorwayMapView.getNavigation().start(currentPosition, "3-_M01M3r5w_fe9c8");

                        } else if (i == 2) {
                            indoorwayMapView.getNavigation().start(currentPosition, "3-_M01M3r5w_76b29");

                        } else if (i == 3) {
                            indoorwayMapView.getNavigation().start(currentPosition, "3-_M01M3r5w_36a38");

                        } else if (i == 4) {
                            indoorwayMapView.getNavigation().start(currentPosition, "3-_M01M3r5w_c1a68");

                        } else if (i == 5) {
                            indoorwayMapView.getNavigation().start(currentPosition, "3-_M01M3r5w_c0b28");

                        } else if (i == 6) {
                            indoorwayMapView.getNavigation().start(currentPosition, "3-_M01M3r5w_a18d9");

                        }

                    }
                }
        );
    }

    private void fillLecturesView(final List<Lecture> lectures ){


        listView_lectures.setVisibility(View.VISIBLE);
        Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_in);
        listView_lectures.startAnimation(a);
        List<String> lectureNames = new ArrayList<String>();
        for (Lecture l:lectures){
            lectureNames.add(l.getName());

        }
        ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, lectureNames);
        listView_lectures.setAdapter(listAdapter);


        listView_lectures.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        listView_lectures .setVisibility(View.INVISIBLE);
                        Lecture lecture = lectures.get(i);
                        Room room = RoomTools.Companion.getRoomByID(lecture.getRoomId());
                        indoorwayMapView.getNavigation().start(currentPosition, room.getId());

                    }
                }
        );
    }

    private void initNavigationDrawer(){
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        navView = findViewById(R.id.navView);
        final Intent fbIntent = new Intent(this, Facebook.class);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();

                if(item.getItemId() == R.id.navToRoom){
                    navListView.setVisibility(View.VISIBLE);

                } else if (item.getItemId() == R.id.navToFb){
                    startActivity(fbIntent);
                    finish();

                } else if (item.getItemId() == R.id.navToAccount){
                        fillLecturesView(exampleUser.getLectures());
                    //USER
                }

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
        Toast toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
        toast.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}