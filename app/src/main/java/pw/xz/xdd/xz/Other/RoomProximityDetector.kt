package pw.xz.xdd.xz.Other

import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayMap
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import pw.xz.xdd.xz.Other.BuildingInformations
import pw.xz.xdd.xz.Other.Room


/**
 * Created by emile on 13-Jan-18.
 */


class RoomProximityDetector(
        var map:IndoorwayMap,
        var mapViewObject:IndoorwayMapView
){

    fun getNearestRoom(position:Coordinates):Pair<Room?, Double> {
        var minDistance: Double = 999999.0;
        var nearestRoom: Room? = null;
        for (room in BuildingInformations.rooms) {
            if (room.coordinatesCenter.getDistanceTo(position) < minDistance) {
                minDistance = room.coordinatesCenter.getDistanceTo(position)
                nearestRoom = room
            }
        }
        return Pair(nearestRoom,minDistance)
    }


}