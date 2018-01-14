package pw.xz.xdd.xz;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public IndoorwayMapView indoorwayMapView;
    public IndoorwayMap currentMap;
    public SQLiteDbHelper database;
    public MarkersLayer myLayer;
    private RoomProximityDetector detector;
    private MapObjectSelectionManager mapObjectSelectionManager;
    private IndoorwayPosition currentPosition;
    private double MAX_DETECTION_RANGE = 12;
    private String lastRoomId = "";
    private Coordinates actualInoorwayPosition;
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

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);




        setContentView(R.layout.activity_main);
        SQLiteDb sql = new SQLiteDb();
        sql.sqliteDbUpdateOnce(getApplicationContext());
        database = new SQLiteDbHelper(getApplicationContext());
        indoorwayMapView = findViewById(R.id.mapView);



        initializeMap();



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
                actualInoorwayPosition = position.getCoordinates();

                displayInformationAboutNearbyRoom();


            }
        };

        IndoorwayLocationSdk.instance()
                .position()
                .onChange()
                .register(positionListener);
    }

    private void displayInformationAboutNearbyRoom(){
        boolean isChangingFloor= false;
        String roomid="";
        Room room;
        kotlin.Pair<Room, Double> roomData = detector.getNearestRoom(actualInoorwayPosition);
        try {

            room = roomData.component1();
            roomid = room.getId();
        }
        catch(Exception e){
            //ustalone eksperymentlanie, ze podczas zmiany pietra cos sie psuje
            isChangingFloor=true;
        }
        if (!roomid.equals(lastRoomId) && !isChangingFloor && roomData.component2()<MAX_DETECTION_RANGE) {
            lastRoomId = roomid;
            Calendar rightNow = Calendar.getInstance();
            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            int currentMinutes = rightNow.get(Calendar.MINUTE);
            int currentDay = rightNow.get(Calendar.DAY_OF_WEEK);


            CalendarToStringConverter converter = new CalendarToStringConverter();
            String day = converter.getDayFromCalendarEnum(currentDay);

            indoorwayMapView.getSelection().selectObject(roomData.component1().getId());
            indoorwayMapView.getPosition().setPosition(currentPosition, true);

            //List<Lecture> lectures = database.getByRoomAndTime(roomid, currentHour, currentMinutes, day);
            List<Lecture> lectures = database.getByRoomAndTime("3-_M01M3r5w_c1a68", 20,20, "Saturday");

            tx = findViewById(R.id.tx);
            //tx.setText(room.getId() + "\n" + currentHour + ":" + currentMinutes + ", " + day);
            Log.e("myDebug","number of results:" + Integer.toString(lectures.size()));
            if (lectures.size() != 0)
                tx.setText(lectures.get(0).getName());
            //tx.setText("tekst");
            cardView = findViewById(R.id.card_view);
            tx.setGravity(Gravity.CENTER);

            animateCardInAndOut();



        }
    }
    private void animateCardInAndOut(){
        tx.setVisibility(View.VISIBLE) ;
        cardView.setVisibility(View.VISIBLE);

        Animation animateIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_in);
        cardView.startAnimation(animateIn);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animateOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animate_out);
                cardView.startAnimation(animateOut);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tx.setVisibility(View.INVISIBLE);
                        cardView.setVisibility(View.INVISIBLE);
                    }
                }, 750);

            }
        }, 3000);
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
                mapObjectSelectionManager = new MapObjectSelectionManager(currentMap,indoorwayMapView);

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
                if (lastRoomId!=null){ //jezeli cos bylo zaznaczone
                    //to sie zaznacza jeszcze raz xd
                    indoorwayMapView.getSelection().selectObject(lastRoomId);
                }
                 List<IndoorwayObjectParameters> result = currentMap.objectsContainingCoordinates(coordinates);


            }
        });
    }

    private void initNavigationDrawer(){
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        navView = findViewById(R.id.navView);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment newFragment;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                newFragment = new Fragment();
                transaction.replace(R.id.nav_Fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                setTitle("BLA");

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
