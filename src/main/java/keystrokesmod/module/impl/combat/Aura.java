package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;
import java.util.List;

public class Aura extends Module {
    private SliderSetting aps;
    private SliderSetting autoblockMode;
    private SliderSetting mode;
    private SliderSetting rangeMode;
    private SliderSetting searchMode;
    private SliderSetting switchDelay;
    private SliderSetting targets;
    private SliderSetting rotationMode;

    private SliderSetting autoblockRange;
    private SliderSetting attackRange;
    private SliderSetting swingRange;

    private final String[] autoblockModes = new String[]{"Manual", "Vanilla", "Interact", "Swap", "Blink"};
    private final String[] modes = new String[]{"Single", "Switch", "Multi"};
    private final String[] rangeModes = new String[]{"Normal", "Hypixel"};
    private final String[] searchModes = new String[]{"For", "ForEach", "Stream"};
    private final String[] rotationModes = new String[]{"None", "Lockview", "Silent"};

    private List<EntityLivingBase> entities = new ArrayList<>();

    private boolean swing;

    public Aura() {
        super("Aura", category.combat, 0);
        this.registerSetting(aps = new SliderSetting("APS", 15.0, 1.0, 20.0, 0.5));
        this.registerSetting(autoblockMode = new SliderSetting("Autoblock Mode", autoblockModes, 2));
        this.registerSetting(mode = new SliderSetting("Aura Type", modes, 0));
        this.registerSetting(rangeMode = new SliderSetting("Range Mode", rangeModes, 0));
        this.registerSetting(searchMode = new SliderSetting("Search Mode", searchModes, 0));
        this.registerSetting(switchDelay = new SliderSetting("Switch Delay", 120.0, 50.0, 1000.0, 0.5));
        this.registerSetting(targets = new SliderSetting("Target", 2.0, 1.0, 10.0, 1.0));
        this.registerSetting(rotationMode = new SliderSetting("Rotation Mode", rotationModes, 3));
        this.registerSetting(new DescriptionSetting("Range Settings"));
        this.registerSetting(autoblockRange = new SliderSetting("Autoblock Range", 3.1, 2.0, 30.0, 0.01));
        this.registerSetting(attackRange = new SliderSetting("Attack Range", 3.2, 3.0, 6.0, 0.01));
        this.registerSetting(swingRange = new SliderSetting("Swing Range", 3.2, 3.0, 8.0, 0.01));

        autoblockRange.showOnly(() -> autoblockMode.getInput() != 0);
        switchDelay.showOnly(() -> mode.getInput() == 1);
        targets.showOnly(() -> mode.getInput() != 0);
    }
}