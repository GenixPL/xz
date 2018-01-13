package pw.xz.xdd.xz

import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayMap
import com.indoorway.android.common.sdk.model.proximity.IndoorwayProximityEvent
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import com.indoorway.android.map.sdk.view.MapView


/**
 * Created by emile on 13-Jan-18.
 */
class RoomProximityDetector(
        var map:IndoorwayMap,
        var mapViewObject:IndoorwayMapView
){

    init {
/*

        IndoorwayLocationSdk.instance().dashboardProximityEvents().register((a:Action1<IndoorwayProximityEvent>)->onEnterZone(a))
        IndoorwayLocationSdk.instance()
                .dashboardProximityEvents()
                .unregister(listener);


// REMBER TO UNREGISTER LISTENER
        IndoorwayLocationSdk.instance()
                .dashboardProximityEvents()
                .unregister(listener);*/
    }
    fun makeTriggersFromRooms(){
        //Nie znam inputu danych z sqla

    }
    public fun registerTrigger(){

    }
    public fun onEnterZone(event: IndoorwayProximityEvent){

    }
}