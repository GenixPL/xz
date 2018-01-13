package pw.xz.xdd.xz;


import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters;
import com.indoorway.android.common.sdk.model.IndoorwayPosition;
import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.indoorway.android.fragments.sdk.map.MapFragment;
import com.indoorway.android.location.sdk.IndoorwayLocationSdk;
import com.indoorway.android.location.sdk.model.IndoorwayLocationSdkState;
import com.indoorway.android.map.sdk.view.IndoorwayMapView;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    public IndoorwayMapView indoorwayMapView;
    public IndoorwayMap currentMap;
    public MarkersLayer myLayer;
    private IndoorwayPosition currentPosition;
    private IndoorwayLocationSdkState currentState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        indoorwayMapView = findViewById(R.id.mapView);

        initializeMap();

        indoorwayMapView.setOnMapLoadCompletedListener(new Action1<IndoorwayMap>() {
            @Override
            public void onAction(IndoorwayMap indoorwayMap) {
                currentMap = indoorwayMap;

                RoomProximityDetector detector = new RoomProximityDetector(indoorwayMap,indoorwayMapView);

                myLayer = indoorwayMapView.getMarker().addLayer(0);
            }
        });

        locationStuff();

    }

    private void locationStuff(){

        Action1<IndoorwayLocationSdkState> listener = new Action1<IndoorwayLocationSdkState>() {
            @Override
            public void onAction(IndoorwayLocationSdkState indoorwayLocationSdkState) {
                currentState = IndoorwayLocationSdk.instance()
                        .state()
                        .current();
            }
        };

        IndoorwayLocationSdk.instance()
                .state()
                .onChange()
                .register(listener);

        // REMEMBER TO UNREGISTER LISTENER
        IndoorwayLocationSdk.instance()
                .state()
                .onChange()
                .unregister(listener);


        Action1<IndoorwayPosition> listener2 = new Action1<IndoorwayPosition>() {
            @Override
            public void onAction(IndoorwayPosition position) {
                // store last position as a field
                currentPosition = position;

                // react for position changes...

                // If you are using map view, you can pass position.
                // Second argument indicates if you want to auto reload map on position change
                // for eg. after going to different building level.
                indoorwayMapView.getPosition().setPosition(currentPosition, true);
            }
        };

        IndoorwayLocationSdk.instance()
                .position()
                .onChange()
                .register(listener2);

        // remember to unregister listener!
        IndoorwayLocationSdk.instance()
                .position()
                .onChange()
                .unregister(listener2);
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
    }

    public void toastMessage(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.cor), message, 7000);
        snackbar.show();

    }

}
