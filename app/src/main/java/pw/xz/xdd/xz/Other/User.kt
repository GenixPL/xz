package pw.xz.xdd.xz.Other

import pw.xz.xdd.xz.Other.Lecture

/**
 * Created by Cezary Borowski on 2018-01-13.
 */

data class User(
        var userId:Int,
        var lectures:List<Lecture>
)