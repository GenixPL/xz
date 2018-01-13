package pw.xz.xdd.xz;


import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

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

import java.util.List;

public class MainActivity extends AppCompatActivity{

    public IndoorwayMapView indoorwayMapView;
    public IndoorwayMap currentMap;
    public MarkersLayer myLayer;
    private RoomProximityDetector detector;
    private IndoorwayPosition currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        indoorwayMapView = findViewById(R.id.mapView);

        initializeMap();
        displayUser();
        initStatusListeners();

    }

    private void initStatusListeners(){
        Action1<IndoorwayLocationSdkError> listener = new Action1<IndoorwayLocationSdkError>() {
            @Override
            public void onAction(IndoorwayLocationSdkError error) {
                if (error instanceof IndoorwayLocationSdkError.BleNotSupported) {
                    // Bluetooth Low Energy is not supported, positioning service will be stopped, it can't work
                    toastMessage("Energy");
                } else if (error instanceof IndoorwayLocationSdkError.MissingPermission) {
                    // Some permissions are missing, ask for it.permission
                    toastMessage("permissions");
                } else if (error instanceof IndoorwayLocationSdkError.BluetoothDisabled) {
                    // Bluetooth is disabled, user have to turn it on
                    toastMessage("disabled blue");
                } else if (error instanceof IndoorwayLocationSdkError.LocationDisabled) {
                    // Location is disabled, user have to turn it on
                    toastMessage("disabled location");
                } else if (error instanceof IndoorwayLocationSdkError.UnableToFetchData) {
                    // Network-related error, service will be restarted on network connection established
                    toastMessage("net");
                } else if (error instanceof IndoorwayLocationSdkError.NoRadioMaps) {
                    // Measurements have to be taken in order to use location
                    toastMessage("measure");
                }
            }
        };

        IndoorwayLocationSdk.instance()
                .state()
                .onError()
                .register(listener);

        Action1<IndoorwayLocationSdkState> listenerCurr = new Action1<IndoorwayLocationSdkState>() {
            @Override
            public void onAction(IndoorwayLocationSdkState indoorwayLocationSdkState) {
                // handle state changes
                toastMessage(indoorwayLocationSdkState.toString());
            }
        };

        IndoorwayLocationSdk.instance()
                .state()
                .onChange()
                .register(listenerCurr);
    }

    private void displayUser(){
        Action1<IndoorwayPosition> listener = new Action1<IndoorwayPosition>() {
            @Override
            public void onAction(IndoorwayPosition position) {
                // store last position as a field
                currentPosition = position;

                // react for position changes...

                // If you are using map view, you can pass position.
                // Second argument indicates if you want to auto reload map on position change
                // for eg. after going to different building level.
                kotlin.Pair<Room, Double> roomData = detector.getNearestRoom(position.getCoordinates());
                indoorwayMapView.getSelection().selectObject(roomData.component1().getId());
                indoorwayMapView.getPosition().setPosition(position, true);
            }
        };

        IndoorwayLocationSdk.instance()
                .position()
                .onChange()
                .register(listener);
    }

    private void initializeMap(){
        indoorwayMapView.load("CScrSxCVhQg", "3-_M01M3r5w");

        indoorwayMapView.getTouch().setOnClickListener(new Action1<Coordinates>() {
            @Override
            public void onAction(Coordinates coordinates) {
                //toastMessage(coordinates.toString());
                List<IndoorwayObjectParameters> result = currentMap.objectsContainingCoordinates(coordinates);
                toastMessage(result.get(0).getName() + "");

            }
        });

        indoorwayMapView.setOnMapLoadCompletedListener(new Action1<IndoorwayMap>() {
            @Override
            public void onAction(IndoorwayMap indoorwayMap) {
                currentMap = indoorwayMap;

                detector = new RoomProximityDetector(indoorwayMap,indoorwayMapView);
                detector.getAllRooms();

                myLayer = indoorwayMapView.getMarker().addLayer(0);
            }
        });

    }

    public void toastMessage(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.cor), message, 7000);
        snackbar.show();

    }

}
