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


class RoomProximityDetector(
        var map:IndoorwayMap,
        var mapViewObject:IndoorwayMapView
){
    public var roomsList:MutableList<Room> = mutableListOf()

    init {
        val listener = object : Action1<IndoorwayProximityEvent> {
            override fun onAction(indoorwayProximityEvent: IndoorwayProximityEvent) {
                onEnterZone(indoorwayProximityEvent);
            }
        }

        IndoorwayLocationSdk.instance().dashboardProximityEvents().register(listener)
       // IndoorwayLocationSdk.instance().dashboardProximityEvents().unregister(listener);

    }
    fun getAllRooms(){
        roomsList.clear()
        for (mapObject in map.objects ){
            var isAuditory = false
            var isRoom = true
            if (mapObject.name!!.toLowerCase().contains("room")){

            }
            else if (mapObject.name!!.toLowerCase().isNumber()){
                isAuditory=true
            }
            var thisRoom = Room(mapObject.name!!.toLowerCase().replace("room ",""), mapObject.id, mapObject.centerPoint, isAuditory);
            roomsList.add(thisRoom)
        }
    }
    fun getNearestRoom(){

    }
    fun makeTriggersFromRooms(){
        //Nie znam inputu danych z sqla


        //map


    }
    public fun registerTrigger(){

    }
    public fun onEnterZone(event: IndoorwayProximityEvent){

    }
}