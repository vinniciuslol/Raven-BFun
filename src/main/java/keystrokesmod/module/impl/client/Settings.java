package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import scala.tools.nsc.settings.ScalaSettings;
import scala.xml.Null;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Settings extends Module {
    public static SliderSetting customCapes;
    public static ButtonSetting weaponAxe;
    public static ButtonSetting weaponRod;
    public static ButtonSetting weaponStick;
    public static ButtonSetting middleClickFriends;
    public static ButtonSetting rotateBody;
    public static ButtonSetting fullBody;
    public static ButtonSetting movementFix;
    public static SliderSetting randomYawFactor;
    public static SliderSetting timeMultiplier;
    public static ButtonSetting sendMessage;
    private String[] capes = new String[]{"None", "MushMC Disabler", "Raven BFun", "Optifine RGB"};
    public static List<ResourceLocation> loadedCapes = new ArrayList<>();

    public Settings() {
        super("Settings", category.client, 0);
        this.registerSetting(new DescriptionSetting("General"));
        this.registerSetting(customCapes = new SliderSetting("Custom cape", capes, 0));
        this.registerSetting(weaponAxe = new ButtonSetting("Set axe as weapon", false));
        this.registerSetting(weaponRod = new ButtonSetting("Set rod as weapon", false));
        this.registerSetting(weaponStick = new ButtonSetting("Set stick as weapon", false));
        this.registerSetting(middleClickFriends = new ButtonSetting("Middle click friends", false));
        this.registerSetting(new DescriptionSetting("Rotations"));
        this.registerSetting(rotateBody = new ButtonSetting("Rotate body", true));
        this.registerSetting(fullBody = new ButtonSetting("Full body", false));
        this.registerSetting(randomYawFactor = new SliderSetting("Random yaw factor", 1.0, 0.0, 10.0, 1.0));
        this.registerSetting(new DescriptionSetting("Profiles"));
        this.registerSetting(sendMessage = new ButtonSetting("Send message on enable", true));
        this.registerSetting(new DescriptionSetting("Theme colors"));
        this.registerSetting(timeMultiplier = new SliderSetting("Time multiplier", 0.5, 0.1, 4.0, 0.1));
        this.canBeEnabled = false;
        loadCapes();
    }

    public void loadCapes() {
        try {
            for (int i = 0; i < capes.length; i++) {
                String name = capes[i];
                if (i > 0) {
                    InputStream stream = Raven.class.getResourceAsStream("/assets/keystrokesmod/textures/capes/" + name + ".png");
                    if (stream == null) {
                        continue;
                    }
                    BufferedImage bufferedImage = ImageIO.read(stream);
                    loadedCapes.add(Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation(name, new DynamicTexture(bufferedImage)));
                }
            }
        }
        catch (Exception ignored) {
        }
    }



}
