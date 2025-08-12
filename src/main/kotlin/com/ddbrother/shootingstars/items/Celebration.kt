package com.ddbrother.shootingstars.items

import com.ddbrother.shootingstars.manager.CelebrationManager
import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.FireworkExplosionComponent
import net.minecraft.component.type.FireworksComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireworkRocketEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.random.Random

class Celebration(settings: Settings) : Item(settings) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        if (!world.isClient) {
            world.playSound(
                null,
                user.x,
                user.y,
                user.z,
                SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH,
                SoundCategory.NEUTRAL,
                1.0f,
                1.0f
            )

            for (i in 0..4) {
                spawnRocket(world, user)
            }
        }

        return ActionResult.SUCCESS
    }

    private fun spawnRocket(world: World, player: PlayerEntity) {
        val forward = Vec3d(player.rotationVector.x, 0.0, player.rotationVector.z).normalize()
        val spawnPos = player.pos.add(forward.multiply(1.0)).add(0.0, 1.0, 0.0)

        val fireworkStack = createRandomFirework()

        val rocket = HomingFireworkEntity(
            world,
            spawnPos.x,
            spawnPos.y,
            spawnPos.z,
            fireworkStack,
            player
        )

        rocket.setVelocity(player.rotationVector.x, player.rotationVector.y, player.rotationVector.z, 0.25f, 0.3f)

        world.spawnEntity(rocket)
    }




    private fun createRandomFirework(): ItemStack {
        val fireworkStack = ItemStack(Items.FIREWORK_ROCKET)

        val explosionComponent = FireworkExplosionComponent(
            FireworkExplosionComponent.Type.entries.random(),
            IntArrayList(List(Random.nextInt(1, 4)) { Random.nextInt(0xFFFFFF + 1) }),
            IntArrayList(listOf(Random.nextInt(0xFFFFFF + 1))),
            Random.nextBoolean(),
            Random.nextBoolean()
        )

        val fireworksComponent = FireworksComponent(
            1,
            listOf(explosionComponent)
        )

        fireworkStack.set(DataComponentTypes.FIREWORKS, fireworksComponent)

        return fireworkStack
    }
}

class HomingFireworkEntity(
    world: World,
    x: Double,
    y: Double,
    z: Double,
    stack: ItemStack,
    private val owner: LivingEntity
) : FireworkRocketEntity(world, x, y, z, stack) {

    private var ticksAlive = 0

    init {
        try {
            val field = FireworkRocketEntity::class.java.getDeclaredField("lifeTime")
            field.isAccessible = true
            field.setInt(this, (Random.nextInt(from = 3, until = 4) * 7))
        } catch (_: Exception) {}
    }

    override fun tick() {
        super.tick()
        ticksAlive++

        try {
            if (!world.isClient && 5 < ticksAlive && ticksAlive < 20) {
                applyCelebrationMark()
            }
        } catch (_: Exception) {}

        if (ticksAlive < 5) {
            val vel = velocity
            val offset = Vec3d(
                (Random.nextDouble() - 0.5) * 0.3,
                (Random.nextDouble() - 0.5) * 0.3,
                (Random.nextDouble() - 0.5) * 0.3
            )
            this.velocity = vel.add(offset)
        } else if (ticksAlive < 20) {
            val target = world.getEntitiesByClass(
                LivingEntity::class.java,
                boundingBox.expand(10.0)
            ) { it.isAlive && it != owner && !it.isSpectator }.minByOrNull { it.squaredDistanceTo(this) }

            if (target != null) {
                val vel = velocity
                val toTarget = target.pos.add(0.0, target.height / 2.0, 0.0)
                    .subtract(pos)
                    .normalize()
                    .multiply(vel.length())

                val newVel = vel.add(toTarget.subtract(vel).multiply(0.25))
                this.velocity = newVel
            }
        }
    }

    private fun applyCelebrationMark() {
        val nearbyEntities = world.getEntitiesByClass(
            LivingEntity::class.java,
            this.boundingBox.expand(7.0)
        ) { it.isAlive }

        for (entity in nearbyEntities) {
            CelebrationManager.markEntity(entity.uuid, 100)
        }
    }

}






