package com.ddbrother.shootingstars

import com.ddbrother.shootingstars.items.CustomItems
import com.ddbrother.shootingstars.manager.CelebrationManager
import net.fabricmc.api.ModInitializer

class ShootingStars : ModInitializer {

    override fun onInitialize() {
        CustomItems.initialize()
        CelebrationManager.register()
    }
}

