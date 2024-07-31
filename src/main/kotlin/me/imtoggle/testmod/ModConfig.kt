package me.imtoggle.testmod

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.data.*

object ModConfig : Config(Mod(TestMod.NAME, ModType.UTIL_QOL), "${TestMod.MODID}.json") {

    init {
        initialize()
    }
}