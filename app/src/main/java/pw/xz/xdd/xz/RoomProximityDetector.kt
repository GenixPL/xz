package pw.xz.xdd.xz

import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayMap
import com.indoorway.android.common.sdk.model.proximity.IndoorwayNotificationInfo
import com.indoorway.android.common.sdk.model.proximity.IndoorwayProximityEvent
import com.indoorway.android.common.sdk.model.proximity.IndoorwayProximityEventShape
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import java.sql.Time
import java.util.*


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

            //tutaj sa dirty hacki bo pokoje nie maja tagow??
            if (mapObject.name!!.toLowerCase().contains("room")){

            }
            else if (mapObject.name!!.toLowerCase().isNumber()){
                isAuditory=true
            }
            var thisRoom = Room(mapObject.name!!.toLowerCase().replace("room ",""), mapObject.id, mapObject.centerPoint, isAuditory);
            roomsList.add(thisRoom)
        }
    }
    fun getNearestRoom(position:Coordinates):Pair<Room?, Double> {
        var minDistance: Double = 999999.0;
        var nearestRoom: Room? = null;
        for (room in roomsList) {
            if (room.coordinatesCenter.getDistanceTo(position) < minDistance) {
                minDistance = room.coordinatesCenter.getDistanceTo(position)
                nearestRoom = room
            }
        }
        return Pair(nearestRoom,minDistance)
    }

    fun getLectureCurrentlyTakingPlace(room:Room){
        val today = java.util.Date()
        val currentTime = java.sql.Time(today.time)


    }
    public fun onEnterZone(event: IndoorwayProximityEvent){

    }
}