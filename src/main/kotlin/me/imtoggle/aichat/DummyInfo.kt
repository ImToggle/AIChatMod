package me.imtoggle.aichat

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldSettings.GameType

class DummyInfo(profile: GameProfile): NetworkPlayerInfo(profile) {

    override fun getLocationSkin(): ResourceLocation {
        return skinLocation
    }

    override fun getDisplayName(): IChatComponent {
        return ChatComponentText(ModConfig.displayName)
    }

    override fun getGameType(): GameType {
        return GameType.getByID(ModConfig.gameType)
    }
}