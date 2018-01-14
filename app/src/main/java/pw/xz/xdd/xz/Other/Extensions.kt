package pw.xz.xdd.xz.Other

/**
 * Created by emile on 13-Jan-18.
 */

fun String.isNumber():Boolean{
    return this.matches(Regex("\\d+(?:\\.\\d+)?"))
}

