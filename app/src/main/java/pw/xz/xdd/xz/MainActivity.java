package pw.xz.xdd.xz;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters;
import com.indoorway.android.map.sdk.view.IndoorwayMapView;
import com.indoorway.android.map.sdk.view.MapView;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public IndoorwayMapView indoorwayMapView;
    public IndoorwayMap currentMap;
    public MarkersLayer myLayer;


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
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.cor), message, 7000);
        snackbar.show();



    }
}
