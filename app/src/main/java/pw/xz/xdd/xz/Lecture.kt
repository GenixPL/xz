package pw.xz.xdd.xz

import java.sql.Time

/**
 * Created by emile on 13-Jan-18.
 */
data class Lecture(
        var name:String,
        var startTime:Time,
        var endTime:Time,
        var day:String,
        var roomId:String,
        var description:String
)