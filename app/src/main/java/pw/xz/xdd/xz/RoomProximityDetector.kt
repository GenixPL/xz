package pw.xz.xdd.xz

import com.indoorway.android.common.sdk.model.IndoorwayMap
import com.indoorway.android.map.sdk.view.MapView


/**
 * Created by emile on 13-Jan-18.
 */
class RoomProximityDetector(
        var mapInstance:IndoorwayMap
){
    init {
        mapInstance.objects
    }
    public fun registerTrigger(){

    }
}