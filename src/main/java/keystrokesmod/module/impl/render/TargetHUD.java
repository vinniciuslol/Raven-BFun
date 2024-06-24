package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.combat.Switchaura;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Theme;
import keystrokesmod.utility.Timer;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.pasted.Colors;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class TargetHUD extends Module {
    private DescriptionSetting description;
    private SliderSetting mode;
    private SliderSetting theme;
    private ButtonSetting renderEsp;
    private ButtonSetting showStatus;
    private ButtonSetting healthColor;
    private String[] modes = new String[] {"Raven BS", "Exhibition"};
    private Timer fadeTimer;
    private Timer healthBarTimer = null;
    private EntityLivingBase target;
    private long lastAliveMS;
    private double lastHealth;
    private float lastHealthBar;

    public TargetHUD() {
        super("TargetHUD", category.render);
        this.registerSetting(description = new DescriptionSetting("Only works with KillAura."));
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(theme = new SliderSetting("Theme", Theme.themes, 0));
        this.registerSetting(renderEsp = new ButtonSetting("Render ESP", true));
        this.registerSetting(showStatus = new ButtonSetting("Show win or loss", true));
        this.registerSetting(healthColor = new ButtonSetting("Traditional health color", false));
    }

    public void onDisable() {
        reset();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent ev) {
        if (!Utils.nullCheck()) {
            reset();
            return;
        }
        if (ev.phase == TickEvent.Phase.END) {
            if (mc.currentScreen != null) {
                reset();
                return;
            }

            if (Switchaura.target != null) {
                target = Switchaura.target;
                lastAliveMS = System.currentTimeMillis();
                fadeTimer = null;
            } else if (target != null) {
                if (System.currentTimeMillis() - lastAliveMS >= 200 && fadeTimer == null) {
                    (fadeTimer = new Timer(400)).start();
                }
            } else {
                return;
            }
            String playerInfo = target.getDisplayName().getFormattedText();

            playerInfo += " " + (getHealthh(target, true, true) + target.getAbsorptionAmount());
            drawTargetHUD(fadeTimer, playerInfo);
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent renderWorldLastEvent) {
        if (!renderEsp.isToggled() || !Utils.nullCheck()) {
            return;
        }
        if (Switchaura.target != null) {
            RenderUtils.renderEntity(Switchaura.target, 2, 0.0, 0.0, Theme.getGradient((int) theme.getInput(), 0), false);
        }
    }

    private void drawTargetHUD(Timer cd, String string) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int n2 = 8;
        final int n3 = mc.fontRendererObj.getStringWidth(string) + n2;
        final int n4 = scaledResolution.getScaledWidth() / 2 - n3 / 2 + 70;
        final int n5 = scaledResolution.getScaledHeight() / 2 + 15 + 30;
        final int n6 = n4 - n2;
        final int n7 = n5 - n2;
        final int n8 = n4 + n3;
        final int n9 = n5 + (mc.fontRendererObj.FONT_HEIGHT + 5) - 6 + n2;
        final int n10 = (cd == null) ? 255 : (255 - cd.getValueInt(0, 255, 1));

        switch ((int) mode.getInput()) {
            case 0:
                float h = getHealthh(target, true, true) / target.getMaxHealth();
                if (h != lastHealth) {
                    (healthBarTimer = new Timer(400)).start();
                }

                lastHealth = h;

                if (showStatus.isToggled()) {
                    string = string + " " + ((h <= Utils.getCompleteHealth(mc.thePlayer) / mc.thePlayer.getMaxHealth()) ? "§aW" : "§cL");
                }

                if (n10 > 0) {
                    final int n11 = Math.min(n10, 110);
                    final int n12 = Math.min(n10, 210);
                    final int[] array = Theme.getGradients((int) theme.getInput());

                    RenderUtils.drawRoundedGradientOutlinedRectangle((float) n6, (float) n7, (float) n8, (float) (n9 + 13), 10.0f, Utils.merge(Color.black.getRGB(), n11), Utils.merge(array[0], n10), Utils.merge(array[1], n10)); // outline

                    final int n13 = n6 + 6;
                    final int n14 = n8 - 6;
                    final int n15 = n9;

                    RenderUtils.drawRoundedRectangle((float) n13, (float) n15, (float) n14, (float) (n15 + 5), 4.0f, Utils.merge(Color.black.getRGB(), n11)); // background

                    int k = Utils.merge(array[0], n12);
                    int n16 = Utils.merge(array[1], n12);
                    float healthBar = (float) (int) (n14 + (n13 - n14) * (1.0 - ((h < 0.05) ? 0.05 : h)));

                    if (healthBar - n13 < 3) { // if goes below, the rounded health bar glitches out
                        healthBar = n13 + 3;
                    }

                    if (healthBar != lastHealthBar && lastHealthBar - n13 >= 3 && healthBarTimer != null) {
                        float diff = lastHealthBar - healthBar;

                        if (diff > 0) {
                            lastHealthBar = lastHealthBar - healthBarTimer.getValueFloat(0, diff, 1);
                        } else {
                            lastHealthBar = healthBarTimer.getValueFloat(lastHealthBar, healthBar, 1);
                        }
                    } else {
                        lastHealthBar = healthBar;
                    }

                    if (healthColor.isToggled()) {
                        k = n16 = Utils.merge(Utils.getColorForHealth(h), n12);
                    }

                    RenderUtils.drawRoundedGradientRect((float) n13, (float) n15, lastHealthBar, (float) (n15 + 5), 4.0f, k, k, k, n16); // health bar
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    mc.fontRendererObj.drawString(string, (float) n4, (float) n5, (new Color(220, 220, 220, 255).getRGB() & 0xFFFFFF) | Utils.clamp(n10 + 15) << 24, true);
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                } else {
                    target = null;
                    healthBarTimer = null;
                }
                break;
            case 1:
                if (n10 > 0) {
                    float health = getHealthh(target, true, true);
                    float width = (float)scaledResolution.getScaledWidth();
                    float height = (float)scaledResolution.getScaledHeight();

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(width / 2.0F + 10.0F - 2.0F, height - 90.0F - 66.0F, 0.0F);
                    RenderUtils.targetHudRect(0.0D, -2.0D, mc.fontRendererObj.getStringWidth(target.getName()) > 70.0F ? (double)(124.0F + mc.fontRendererObj.getStringWidth(target.getName()) - 70.0F) : 124.0D, 38.0D, 1.0D);
                    RenderUtils.targetHudRect1(0.0D, -2.0D, 124.0D, 38.0D, 1.0D);
                    mc.fontRendererObj.drawString(target.getName(), 42.0F, 0.5F, -1, true);

                    float totalHealth = health + target.getAbsorptionAmount();
                    float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
                    Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                    float progress = (float) (health / target.getMaxHealth());
                    Color customColor = health >= 0.0F ? Colors.blendColors(fractions, colors, progress).brighter() : Color.RED;
                    double width1 = 0.0D;
                    width1 = Colors.getIncremental(width1, 5.0D);

                    if (width1 < 50.0D) {
                        width1 = 50.0D;
                    }

                    double healthLocation = width1 * (double)progress;

                    RenderUtils.rectangle(42.5D, 10.3D, 53.0D + healthLocation + 0.5D, 13.5D, customColor.getRGB());

                    if (target.getAbsorptionAmount() > 0.0F) {
                        RenderUtils.rectangle(97.5D - (double)((EntityLivingBase)target).getAbsorptionAmount(), 10.3D, 103.5D, 13.5D, (new Color(137, 112, 9)).getRGB());
                    }

                    RenderUtils.drawRectBordered(42.0D, 9.800000190734863D, 54.0D + width1, 14.0D, 0.5D, Colors.getColor(0, 0), Colors.getColor(0));

                    int var18;

                    for(var18 = 1; var18 < 10; ++var18) {
                        double dThing = width1 / 8.5D * (double)var18;
                        RenderUtils.rectangle(43.5D + dThing, 9.8D, 43.5D + dThing + 0.5D, 14.0D, Colors.getColor(0));
                    }

                    GlStateManager.scale(0.5D, 0.5D, 0.5D);

                    var18 = (int)mc.thePlayer.getDistanceToEntity(target);
                    String str = "HP: " + totalHealth + " | Dist: " + var18;

                    mc.fontRendererObj.drawString(str, 85.6F, 32.0F, -1, true);
                    GlStateManager.scale(2.0D, 2.0D, 2.0D);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

                    if (target instanceof EntityPlayer) {
                        this.drawArmor(28, 19);
                    }

                    GlStateManager.scale(0.31D, 0.31D, 0.31D);
                    GlStateManager.translate(73.0F, 102.0F, 40.0F);
                    this.model(target.rotationYaw, target.rotationPitch, target);
                    GlStateManager.popMatrix();
                } else {
                    target = null;
                    healthBarTimer = null;
                }
                break;
        }
    }

    private void model(float yaw, float pitch, EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 50.0F);
        GlStateManager.scale(-50.0F, 50.0F, 50.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float renderYawOffset = entityLivingBase.renderYawOffset;
        float rotationYaw = entityLivingBase.rotationYaw;
        float rotationPitch = entityLivingBase.rotationPitch;
        float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        float rotationYawHead = entityLivingBase.rotationYawHead;

        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(-Math.atan((double)(pitch / 40.0F)) * 20.0D), 1.0F, 0.0F, 0.0F);

        entityLivingBase.renderYawOffset = yaw - yaw / yaw * 0.4F;
        entityLivingBase.rotationYaw = yaw - yaw / yaw * 0.2F;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;

        GlStateManager.translate(0.0F, 0.0F, 0.0F);

        RenderManager renderManager = mc.getRenderManager();

        renderManager.setPlayerViewY(180.0F);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        renderManager.setRenderShadow(true);

        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    private void drawArmor(int x, int y) {
        GL11.glPushMatrix();
        ArrayList<ItemStack> stuff = new ArrayList<>();
        int split = -3;

        ItemStack everything;
        for(int index = 3; index >= 0; --index) {
            everything = mc.thePlayer.inventory.armorInventory[index];
            if (everything != null) {
                stuff.add(everything);
            }
        }

        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            stuff.add(mc.thePlayer.getCurrentEquippedItem());
        }

        Iterator var7 = stuff.iterator();

        while(var7.hasNext()) {
            everything = (ItemStack)var7.next();
            if (mc.theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                split += 16;
            }

            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            mc.getRenderItem().zLevel = -150.0F;
            mc.getRenderItem().renderItemIntoGUI(everything, split + x, y);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, everything, split + x, y);
            RenderUtils.renderEnchantText(everything, split + x, y);
            mc.getRenderItem().zLevel = 0.0F;
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            everything.getEnchantmentTagList();
        }

        GL11.glPopMatrix();
    }

    private float getHealthh(EntityLivingBase e, boolean fs, boolean a) {
        if (e == null)
            return 0.0f;

        if (fs && e instanceof EntityPlayer) {
            Scoreboard scoreboard = ((EntityPlayer) e).getWorldScoreboard();
            Score objective = scoreboard.getValueFromObjective(e.getName(), scoreboard.getObjectiveInDisplaySlot(2));

            int scoreboardHealth = objective.getScorePoints();

            if (scoreboardHealth > 0)
                return scoreboardHealth;
        }

        float health = e.getHealth();

        if (a)
            health += e.getAbsorptionAmount();

        return health > 0 ? health : 20f;
    }

    private void reset() {
        fadeTimer = null;
        target = null;
        healthBarTimer = null;
    }
}
