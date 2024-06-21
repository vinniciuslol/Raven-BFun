package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.other.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.packets.serverbound.C08;
import keystrokesmod.script.packets.serverbound.C0A;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Aura extends Module {
    private SliderSetting aps;
    private SliderSetting fov;
    public SliderSetting autoblockMode;
    private SliderSetting mode;
    private SliderSetting rangeMode;
    private SliderSetting searchMode;
    private SliderSetting switchDelay;
    private SliderSetting targets;
    private SliderSetting rotationMode;

    private SliderSetting filter;
    private SliderSetting autoblockRange;
    private SliderSetting attackRange;
    private SliderSetting swingRange;

    private ButtonSetting targetFriend;
    private ButtonSetting targetInvisible;
    private ButtonSetting targetTeam;
    private ButtonSetting fixSlotReset;
    private ButtonSetting silentSwing;

    private final String[] autoblockModes = new String[]{"Manual", "Vanilla", "Interact", "Swap", "Blink"};
    private final String[] modes = new String[]{"Single", "Switch", "Multi"};
    private final String[] rangeModes = new String[]{"Normal", "Hypixel"};
    private final String[] searchModes = new String[]{"For", "ForEach", "Stream"};
    private final String[] rotationModes = new String[]{"None", "Lockview", "Silent"};
    private final String[] filters = new String[]{"Distance", "Health", "Yaw"};

    private List<EntityLivingBase> entities = new ArrayList<>();
    private List<EntityLivingBase> enemies = new ArrayList<>();
    private EntityLivingBase target;

    private Queue<Packet> packets = new ConcurrentLinkedQueue<>();
    private float[] rotations = null;
    private boolean attack;
    private boolean swing;
    public boolean block;
    private boolean blinked;
    private float lastYaw, lastPitch;

    private Random rand = new Random();
    private long i;
    private long j;
    private long k;
    private long l;
    private double m;
    private boolean n;

    private int switchIndex;
    private double lastSwitch;
    private boolean switchTarget;

    public Aura() {
        super("Aura", category.combat, 0);
        this.registerSetting(aps = new SliderSetting("APS", 15.0, 1.0, 20.0, 0.5));
        this.registerSetting(fov = new SliderSetting("Fov", 360.0, 10.0, 360, 0.5));
        this.registerSetting(autoblockMode = new SliderSetting("Autoblock Mode", autoblockModes, 2));
        this.registerSetting(mode = new SliderSetting("Aura Type", modes, 0));
        this.registerSetting(rangeMode = new SliderSetting("Range Mode", rangeModes, 0));
        this.registerSetting(searchMode = new SliderSetting("Search Mode", searchModes, 0));
        this.registerSetting(switchDelay = new SliderSetting("Switch Delay", 120.0, 50.0, 1000.0, 0.5));
        this.registerSetting(targets = new SliderSetting("Target", 2.0, 1.0, 10.0, 1.0));
        this.registerSetting(rotationMode = new SliderSetting("Rotation Mode", rotationModes, 3));
        this.registerSetting(new DescriptionSetting("Range Settings"));
        this.registerSetting(filter = new SliderSetting("Filter", filters, 0));
        this.registerSetting(autoblockRange = new SliderSetting("Autoblock Range", 3.1, 2.0, 30.0, 0.01));
        this.registerSetting(attackRange = new SliderSetting("Attack Range", 3.2, 3.0, 6.0, 0.01));
        this.registerSetting(swingRange = new SliderSetting("Swing Range", 3.2, 3.0, 8.0, 0.01));
        this.registerSetting(new DescriptionSetting("Options"));
        this.registerSetting(targetFriend = new ButtonSetting("Target Friendd", false));
        this.registerSetting(targetInvisible = new ButtonSetting("Target Invisible", true));
        this.registerSetting(targetTeam = new ButtonSetting("Target Team", false));
        this.registerSetting(fixSlotReset = new ButtonSetting("Fix Slot Reset", false));
        this.registerSetting(silentSwing = new ButtonSetting("Silent Swing", false));

        autoblockRange.showOnly(() -> autoblockMode.getInput() != 0);
        switchDelay.showOnly(() -> mode.getInput() == 1);
        targets.showOnly(() -> mode.getInput() != 0);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (ac() && swing && !attack) {
            if (silentSwing.isToggled()) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());
            } else {
                mc.thePlayer.swingItem();
            }

            swing = false;
        }

        if (block && autoblockMode.getInput() == 4 && Utils.holdingSword()) {
            if (blinked) {
                clearPackets();
                blinked = false;
            } else {
                mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
        } else if (block && autoblockMode.getInput() == 3 && Utils.holdingSword()) {
            if (blinked) {
                clearPackets();
                blinked = false;
            } else {
                mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
        }

        if (attack) {
            if (mode.getInput() == 2 && !entities.isEmpty()) {
                for (EntityLivingBase en : entities) {
                    Utils.attackEntity(en, !silentSwing.isToggled(), silentSwing.isToggled());
                    if (block && autoblockMode.getInput() == 2 && Utils.holdingSword())
                        mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(en, C02PacketUseEntity.Action.INTERACT));
                }
            } else if (mode.getInput() != 2 && target != null) {
                Utils.attackEntity(target, !silentSwing.isToggled(), silentSwing.isToggled());

                if (block && autoblockMode.getInput() == 2 && Utils.holdingSword())
                    mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
            }

            attack = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent e) {
        getTarget();

        if (ac() && (!entities.isEmpty() || target != null)) {
            attack = true;
        }

        if (rotationMode.getInput() == 1) {
            if (mode.getInput() == 2 && !entities.isEmpty()) {
                for (EntityLivingBase en : entities) {
                    rotations = Utils.gr(en);
                    mc.thePlayer.rotationYaw = rotations[0];
                    mc.thePlayer.rotationPitch = rotations[1];
                }
            } else if (mode.getInput() != 2 && target != null) {
                rotations = Utils.gr(target);
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreMotion(PreMotionEvent e) {
        if (mode.getInput() == 2) {
            if (rotationMode.getInput() == 2)
                if (mode.getInput() == 2 && !entities.isEmpty()) {
                    for (EntityLivingBase en : entities) {
                        rotations = Utils.gr(en);
                        e.setYaw(rotations[0]);
                        e.setPitch(rotations[1]);
                    }
                } else if (mode.getInput() != 2 && target != null) {
                    rotations = Utils.gr(target);
                    e.setYaw(rotations[0]);
                    e.setPitch(rotations[1]);
                }
        }

        if (autoblockMode.getInput() == 2 && block && Utils.holdingSword()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreUpdate(PreUpdateEvent e) {
        if (block) {
            setBlocking(true);
            block = false;
        }
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!fixSlotReset.isToggled())
            return;

        if (Utils.holdingSword() && (mc.thePlayer.isBlocking() || block)) {
            if (e.getPacket() instanceof S2FPacketSetSlot) {
                if (mc.thePlayer.inventory.currentItem == ((S2FPacketSetSlot) e.getPacket()).func_149173_d() - 36 && mc.currentScreen == null) {
                    if (((S2FPacketSetSlot) e.getPacket()).func_149174_e() == null || (((S2FPacketSetSlot) e.getPacket()).func_149174_e().getItem() != mc.thePlayer.getHeldItem().getItem()))
                        return;

                    e.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onSendPacket(SendPacketEvent e) {
        if (e.getPacket() instanceof C00PacketKeepAlive)
            return;

        if (autoblockMode.getInput() == 4 && !blinked) {
            blinked = true;
            packets.add(e.getPacket());
            e.setCanceled(true);
        }
    }

    private void clearPackets() {
        synchronized (packets) {
            if (!packets.isEmpty()) {
                packets.forEach(PacketUtils::receivePacketNoEvent);
                packets.clear();
            }
        }
    }

    private void getTarget() {
        enemies.clear();
        entities.clear();

        switch ((int) searchMode.getInput()) {
            case 0:
                for (Entity e : mc.theWorld.loadedEntityList) {
                    if (mode.getInput() != 0 && entities.size() > targets.getInput())
                        continue;

                    if (e == null)
                        continue;

                    if (!(e instanceof EntityPlayer))
                        continue;

                    if (e.isDead)
                        continue;

                    if (e.isInvisible() && !targetInvisible.isToggled())
                        continue;

                    if (AntiBot.isBot(e))
                        continue;

                    if (Utils.isFriended((EntityPlayer) e) && !targetFriend.isToggled())
                        continue;

                    if (Utils.isTeamMate(e) && !targetTeam.isToggled())
                        continue;

                    if (fov.getInput() != 360 && !Utils.inFov((float) fov.getInput(), e))
                        continue;

                    if (searchMode.getInput() == 1) {
                        double distance = mc.thePlayer.getDistanceSqToEntity(e); // distance precise for hypixel;

                        if (autoblockMode.getInput() != 0 && distance <= autoblockRange.getInput() * autoblockRange.getInput())
                            block = true;

                        if (distance <= swingRange.getInput() * swingRange.getInput())
                            swing = true;

                        if (distance > swingRange.getInput() * attackRange.getInput())
                            continue;
                    } else {
                        double distance = mc.thePlayer.getDistanceToEntity(e);

                        if (autoblockMode.getInput() != 0 && distance <= autoblockRange.getInput())
                            block = true;

                        if (distance <= swingRange.getInput())
                            swing = true;

                        if (distance > swingRange.getInput() && distance > attackRange.getInput())
                            continue;
                    }

                    entities.add((EntityLivingBase) e);
                }
                break;
            case 1:
                mc.theWorld.loadedEntityList.forEach(e -> {
                    if (mode.getInput() != 0 && entities.size() > targets.getInput())
                        return;

                    if (e == null)
                        return;

                    if (!(e instanceof EntityPlayer))
                        return;

                    if (e.isDead)
                        return;

                    if (e.isInvisible() && !targetInvisible.isToggled())
                        return;

                    if (AntiBot.isBot(e))
                        return;

                    if (Utils.isFriended((EntityPlayer) e) && !targetFriend.isToggled())
                        return;

                    if (Utils.isTeamMate(e) && !targetTeam.isToggled())
                        return;

                    if (fov.getInput() != 360 && !Utils.inFov((float) fov.getInput(), e))
                        return;

                    if (searchMode.getInput() == 1) {
                        double distance = mc.thePlayer.getDistanceSqToEntity(e); // distance precise for hypixel;

                        if (autoblockMode.getInput() != 0 && distance <= autoblockRange.getInput() * autoblockRange.getInput())
                            block = true;

                        if (distance <= swingRange.getInput() * swingRange.getInput())
                            swing = true;

                        if (distance > swingRange.getInput() * attackRange.getInput())
                            return;
                    } else {
                        double distance = mc.thePlayer.getDistanceToEntity(e);

                        if (autoblockMode.getInput() != 0 && distance <= autoblockRange.getInput())
                            block = true;

                        if (distance <= swingRange.getInput())
                            swing = true;

                        if (distance > swingRange.getInput() && distance > attackRange.getInput())
                            return;
                    }

                    entities.add((EntityLivingBase) e);
                });
                break;
            case 2:
                entities = mc.theWorld.loadedEntityList.stream()
                        .filter(e -> e instanceof EntityPlayer)
                        .map(e -> (EntityLivingBase) e)
                        .filter(e -> {
                            if (mode.getInput() != 0 && entities.size() > targets.getInput())
                                return false;

                            if (e.isDead)
                                return false;

                            if (e.isInvisible() && !targetInvisible.isToggled())
                                return false;

                            if (AntiBot.isBot(e))
                                return false;

                            if (Utils.isFriended((EntityPlayer) e) && !targetFriend.isToggled())
                                return false;

                            if (Utils.isTeamMate(e) && !targetTeam.isToggled())
                                return false;

                            if (fov.getInput() != 360 && !Utils.inFov((float) fov.getInput(), e))
                                return false;

                            if (searchMode.getInput() == 1) {
                                double distance = mc.thePlayer.getDistanceSqToEntity(e); // distance precise for hypixel;

                                if (autoblockMode.getInput() != 0 && distance <= autoblockRange.getInput() * autoblockRange.getInput())
                                    block = true;

                                if (distance <= swingRange.getInput() * swingRange.getInput())
                                    swing = true;

                                if (distance > swingRange.getInput() * attackRange.getInput())
                                    return false;
                            } else {
                                double distance = mc.thePlayer.getDistanceToEntity(e);

                                if (autoblockMode.getInput() != 0 && distance <= autoblockRange.getInput())
                                    block = true;

                                if (distance <= swingRange.getInput())
                                    swing = true;

                                if (distance > swingRange.getInput() && distance > attackRange.getInput())
                                    return false;
                            }

                            return true;
                        })
                        .collect(Collectors.toList());
        }

        if (!entities.isEmpty()) {
            switch ((int) filter.getInput()) {
                case 0:
                    if (rangeMode.getInput() == 1)
                        entities.sort((e1, e2) -> (int) (e1.getDistanceToEntity(mc.thePlayer) - e2.getDistanceToEntity(mc.thePlayer)));
                    else
                        entities.sort((e1, e2) -> (int) (e1.getDistanceToEntity(mc.thePlayer) * e1.getDistanceSqToEntity(mc.thePlayer) - e2.getDistanceSqToEntity(mc.thePlayer) * e2.getDistanceSqToEntity(mc.thePlayer)));
                    break;
                case 1:
                    entities.sort((e1, e2) -> (int) (e1.getHealth() + e1.getAbsorptionAmount() - e2.getHealth() + e2.getAbsorptionAmount()));
                    break;
                case 2:
                    entities.sort((e1, e2) -> (int) (Utils.getYaw(e1) - Utils.getYaw(e2)));
            }
        }

        switch ((int) mode.getInput()) {
            case 0:
                target = entities.subList(0, Math.min(entities.size(), 1)).get(0);
                break;
            case 1:
                if (Math.abs(System.currentTimeMillis() - lastSwitch) > switchDelay.getInput()) {
                    if (switchIndex < entities.size() - 1) {
                        switchIndex++;
                    } else {
                        switchIndex = 0;
                    }
                    lastSwitch = System.currentTimeMillis();
                }

                if (switchIndex > entities.size() - 1)
                    switchIndex = 0;

                target = entities.get(switchIndex);
                break;
        }
    }

    public boolean ac() {
        if (this.j > 0L && this.i > 0L) {
            return System.currentTimeMillis() > this.i;
        } else {
            this.gd();
        }

        return false;
    }

    public void setBlocking(boolean state) {
        if (!Utils.holdingSword())
            return;

        if (state) {
            Reflection.setBlocking(true);
            if (autoblockMode.getInput() == 1)
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
            else if (autoblockMode.getInput() == 3)
                mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        } else {
            Reflection.setBlocking(false);
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }



    public void gd() {
        double c = Utils.randomizeDouble(aps.getInput(), aps.getInput() + 2.5) + 0.4D * this.rand.nextDouble();
        long d = (int) Math.round(1000.0D / c);
        if (System.currentTimeMillis() > this.k) {
            if (!this.n && this.rand.nextInt(100) >= 85) {
                this.n = true;
                this.m = 1.1D + this.rand.nextDouble() * 0.15D;
            } else {
                this.n = false;
            }

            this.k = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        if (this.n) {
            d = (long) ((double) d * this.m);
        }

        if (System.currentTimeMillis() > this.l) {
            if (this.rand.nextInt(100) >= 80) {
                d += 50L + (long) this.rand.nextInt(100);
            }

            this.l = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        this.j = System.currentTimeMillis() + d;
        this.i = System.currentTimeMillis() + d / 2L - (long) this.rand.nextInt(10);
    }
}