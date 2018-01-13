package pw.xz.xdd.xz

import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayMap

import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import com.indoorway.android.map.sdk.view.MapView
import com.indoorway.android.common.sdk.model.proximity.IndoorwayProximityEvent





/**
 * Created by emile on 13-Jan-18.
 */
data class Room(
        var name:String,
        var id:Int
)
class RoomProximityDetector(
        var map:IndoorwayMap,
        var mapViewObject:IndoorwayMapView
){

    init {
        val listener = object : Action1<IndoorwayProximityEvent> {
            override fun onAction(indoorwayProximityEvent: IndoorwayProximityEvent) {
                onEnterZone(indoorwayProximityEvent);
            }
        }

        IndoorwayLocationSdk.instance().dashboardProximityEvents().register(listener)
       // IndoorwayLocationSdk.instance().dashboardProximityEvents().unregister(listener);

    }
    fun makeTriggersFromRooms(){
        //Nie znam inputu danych z sqla
        var roomsPlaceholder:MutableList<Room> = mutableListOf(Room("aaa",1));
        for (room in roomsPlaceholder){

        }
    }
    public fun registerTrigger(){

    }
    public fun onEnterZone(event: IndoorwayProximityEvent){

    }
}