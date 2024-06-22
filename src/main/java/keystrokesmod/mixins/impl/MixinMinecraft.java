package keystrokesmod.mixins.impl;

import keystrokesmod.module.ModuleManager;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow public MovingObjectPosition objectMouseOver;

    @Shadow public PlayerControllerMP playerController;

    @Shadow private int leftClickCounter;

    @Shadow @Final private static Logger logger;

    @Shadow public EntityPlayerSP thePlayer;

    @Shadow public WorldClient theWorld;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void clickMouse()
    {
        if (this.leftClickCounter <= 0)
        {
            this.thePlayer.swingItem();

            if (this.objectMouseOver == null)
            {
                logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");

                if (this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else
            {
                if (ModuleManager.noMissClick.isEnabled() && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS)
                    return;

                switch (this.objectMouseOver.typeOfHit)
                {
                    case ENTITY:
                        this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();

                        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air)
                        {
                            this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:
                    default:

                        if (this.playerController.isNotCreative())
                        {
                            this.leftClickCounter = 10;
                        }
                }
            }
        }
    }
}
