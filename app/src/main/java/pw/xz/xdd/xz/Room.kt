package pw.xz.xdd.xz

import com.indoorway.android.common.sdk.model.Coordinates

/**
 * Created by emile on 13-Jan-18.
 */
data class Room(
        var name:String,
        var id:String,
        var coordinatesCenter: Coordinates,
        var isAuditory:Boolean
)
fun getRoomIDbyName( rooms:List<Room>, name:String):Room{
    return rooms.filter { x-> x.name==name }[0]
}