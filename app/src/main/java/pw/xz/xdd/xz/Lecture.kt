package pw.xz.xdd.xz

/**
 * Created by emile on 13-Jan-18.
 */
data class Lecture(
        var name:String,
        var startTimeHH:Int,
        var startTimeMM:Int,
        var endTimeHH:Int,
        var endTimeMM:Int,
        var day:String,
        var roomId:String,
        var description:String
)