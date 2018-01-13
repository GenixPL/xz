package pw.xz.xdd.xz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.map.sdk.view.IndoorwayMapView;

public class MainActivity extends AppCompatActivity {

    public IndoorwayMapView indoorwayMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // init application context on each Application start
        IndoorwaySdk.initContext(this);

        // it's up to you when to initialize IndoorwaySdk, once initialized it will work forever!
        IndoorwaySdk.configure("6513a6eb-133a-43f6-a206-6afe3e07fe96");

        indoorwayMapView = findViewById(R.id.mapView);
        initializeMap();

        RoomProximityDetector detector = new RoomProximityDetector(indoorwayMapView);


    }

    private void initializeMap(){
        indoorwayMapView.load("CScrSxCVhQg", "3-_M01M3r5w");

        indoorwayMapView.getTouch().setOnClickListener(new Action1<Coordinates>() {
            @Override
            public void onAction(Coordinates coordinates) {
                toastMessage(coordinates.toString());
            }
        });
    }


    public void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
