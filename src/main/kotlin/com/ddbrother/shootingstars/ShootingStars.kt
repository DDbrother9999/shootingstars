package com.ddbrother.shootingstars

import com.ddbrother.shootingstars.items.Items
import com.ddbrother.shootingstars.manager.CelebrationManager
import net.fabricmc.api.ModInitializer

class ShootingStars : ModInitializer {

    override fun onInitialize() {
        Items.initialize()
        CelebrationManager.register()
    }

}

