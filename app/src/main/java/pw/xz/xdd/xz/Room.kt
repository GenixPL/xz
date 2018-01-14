package pw.xz.xdd.xz

import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayMap

/**
 * Created by emile on 13-Jan-18.
 */
data class Room(
        var name:String,
        var id:String,
        var coordinatesCenter: Coordinates,
        var isAuditory:Boolean
)
class RoomTools {
    companion object {
        fun getRoomByName(rooms: List<Room>, name: String): Room {
            return rooms.filter { x -> x.name == name }[0];
        }
        fun getAllRooms(map:IndoorwayMap){
            val out:MutableList<Room> = mutableListOf()

            for (mapObject in map.objects ){
                var isAuditory = false
                var isRoom = true
                var shouldAdd=false
                //tutaj sa dirty hacki bo pokoje nie maja tagow??
                if (mapObject.name!!.toLowerCase().contains("room")){
                    shouldAdd = true
                }
                else if (mapObject.name!!.toLowerCase().isNumber()){
                    isAuditory=true
                    shouldAdd=true
                }

                if (shouldAdd) {
                    var thisRoom = Room(mapObject.name!!.toLowerCase().replace("room ", ""), mapObject.id, mapObject.centerPoint, isAuditory);
                    out.add(thisRoom)
                }
            }
            BuildingInformations.rooms=out;
        }
        fun getIdByName(name:String):String{
            return BuildingInformations.rooms.filter { x-> x.name == name }[0].id
        }
    }


}