package keystrokesmod.module;

import keystrokesmod.module.impl.client.CommandLine;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.module.impl.combat.*;
import keystrokesmod.module.impl.movement.*;
import keystrokesmod.module.impl.other.*;
import keystrokesmod.module.impl.player.*;
import keystrokesmod.module.impl.render.*;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.profile.Manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModuleManager {
    static List<Module> modules = new ArrayList<>();
    public static List<Module> organizedModules = new ArrayList<>();
    public static Module nameHider;
    public static Module fastPlace;
    public static AntiFireball antiFireball;
    public static BedAura bedAura;
    public static FastMine fastMine;
    public static Module antiShuffle;
    public static Module commandLine;
    public static Derp Derp;
    public static Module antiBot;
    public static Module noSlow;
    public static Peformance peformance;
    public static Switchaura killAura;
    public static Module autoClicker;
    public static Module hitBox;
    public static Module reach;
    public static BedESP bedESP;
    public static HUD hud;
    public static Module timer;
    public static PlayerCircle playerCircle;
    public static Module fly;
    public static Potions potions;
    public static TargetHUD targetHUD;
    public static Trajectories trajectories;
    public static NoFall noFall;
    public static PlayerESP playerESP;
    public static Module reduce;
    public static SafeWalk safeWalk;
    public static Module keepSprint;
    public static Module antiKnockback;
    public static Tower tower;
    public static NoCameraClip noCameraClip;
    public static Module bedwars;
    public static Speed bHop;
    public static NoHurtCam noHurtCam;
    public static Scaffold scaffold;
    public static Module noMissClick;
    public static Tweaks tweaks;

    public void register() {
        this.addModule(new LongJump());
        this.addModule(new PlayerCircle());
        this.addModule(new Derp());
        this.addModule(new Blink());
        this.addModule(new Peformance());
        this.addModule(new Trajectories());
        this.addModule(tower = new Tower());
        this.addModule(tweaks = new Tweaks());
        this.addModule(new Radar());
        this.addModule(new Settings());
        this.addModule(bHop = new Speed());
        this.addModule(new InvManager());
        this.addModule(scaffold = new Scaffold());
        this.addModule(new AntiAFK());
        this.addModule(new AutoTool());
        this.addModule(noHurtCam = new NoHurtCam());
        this.addModule(fly = new Fly());
        this.addModule(new InvMove());
        this.addModule(potions = new Potions());
        this.addModule(new AutoSwap());
        this.addModule(keepSprint = new KeepSprint());
        this.addModule(bedAura = new BedAura());
        this.addModule(noSlow = new NoSlow());
        this.addModule(new Indicators());
        this.addModule(noCameraClip = new NoCameraClip());
        this.addModule(new Sprint());
        this.addModule(timer = new Timer());
        this.addModule(new AutoPlace());
        this.addModule(fastPlace = new FastPlace());
        this.addModule(noFall = new NoFall());
        this.addModule(safeWalk = new SafeWalk());
        this.addModule(antiKnockback = new AntiKnockback());
        this.addModule(antiBot = new AntiBot());
        this.addModule(antiShuffle = new AntiShuffle());
        this.addModule(new Chams());
        this.addModule(new ChestESP());
        this.addModule(new Nametags());
		this.addModule(new Step());
		this.addModule(new Spider());
        this.addModule(playerESP = new PlayerESP());
        this.addModule(new Tracers());
        this.addModule(hud = new HUD());
        this.addModule(new Anticheat());
		this.addModule(new AutoHeal());
        this.addModule(new MoreKB());
        this.addModule(new Criticals());
        this.addModule(new BreakProgress());
        this.addModule(new Xray());

        this.addModule(targetHUD = new TargetHUD());
        this.addModule(antiFireball = new AntiFireball());
        this.addModule(bedESP = new BedESP());
        this.addModule(new keystrokesmod.script.Manager());
        this.addModule(killAura = new Switchaura());
        this.addModule(new ItemESP());
        this.addModule(new NoRotate());
        this.addModule(nameHider = new NameHider());
        this.addModule(new FakeLag());
        this.addModule(new WaterBucket());
        this.addModule(commandLine = new CommandLine());
        this.addModule(fastMine = new FastMine());
        this.addModule(new FastPlay());
        this.addModule(new Manager());
        this.addModule(new ViewPackets());
        this.addModule(new Gui());
        antiBot.enable();
        Collections.sort(this.modules, Comparator.comparing(Module::getName));
    }

    public void addModule(Module m) {
        modules.add(m);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> inCategory(Module.category categ) {
        ArrayList<Module> categML = new ArrayList<>();

        for (Module mod : this.getModules()) {
            if (mod.moduleCategory().equals(categ)) {
                categML.add(mod);
            }
        }

        return categML;
    }

    public Module getModule(String moduleName) {
        for (Module module : modules) {
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    public static void sort() {
        if (HUD.alphabeticalSort.isToggled()) {
            Collections.sort(organizedModules, Comparator.comparing(Module::getName));
        } else {
            organizedModules.sort((o1, o2) -> Utils.mc.fontRendererObj.getStringWidth(o2.getName() + ((HUD.showInfo.isToggled() && !o2.getInfo().isEmpty()) ? " " + o2.getInfo() : "")) - Utils.mc.fontRendererObj.getStringWidth(o1.getName() + (HUD.showInfo.isToggled() && !o1.getInfo().isEmpty() ? " " + o1.getInfo() : "")));
        }
    }
}
