package com.ddbrother.shootingstars.items

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object CustomItems {

    val CELEBRATION: Item = register("celebration", ::Celebration, Item.Settings().maxCount(1))

    private fun register(path: String, factory: (Item.Settings) -> Item, settings: Item.Settings): Item {
        val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("shootingstars", path))
        val item = factory(settings.registryKey(itemKey))

        Registry.register(Registries.ITEM, itemKey, item)

        return item
    }

    fun initialize() {
    }
}