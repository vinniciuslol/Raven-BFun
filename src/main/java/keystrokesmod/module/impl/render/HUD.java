package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Theme;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.lwjgl.opengl.GL11;
import sun.font.FontFamily;
import sun.font.FontManager;

import java.awt.*;
import java.io.IOException;

public class HUD extends Module {
    public static SliderSetting theme;
    public static SliderSetting alignw;
    public static ButtonSetting dropShadow;
    public static ButtonSetting alphabeticalSort;
    private static ButtonSetting alignRight;
    private static ButtonSetting lowercase;
    public static ButtonSetting showInfo;
    public static ButtonSetting watermark;
    public static int hudX = 5;
    public static int hudY = 70;
    private boolean isAlphabeticalSort;
    private boolean canShowInfo;

    public HUD() {
        super("HUD", category.render);
        this.registerSetting(new DescriptionSetting("Right click bind to hide modules."));
        this.registerSetting(new ButtonSetting("Edit position", () -> {
            mc.displayGuiScreen(new EditScreen());
        }));
        this.registerSetting(watermark = new ButtonSetting("Watermark", true));
        this.registerSetting(alignw = new SliderSetting("Align", 73, 1.0, 200.0, 0.1));
        this.registerSetting(theme = new SliderSetting("Color Theme", Theme.themes, 1));
        this.registerSetting(alignRight = new ButtonSetting("Align right", false));
        this.registerSetting(alphabeticalSort = new ButtonSetting("Alphabetical sort", false));
        this.registerSetting(dropShadow = new ButtonSetting("Drop shadow", true));
        this.registerSetting(lowercase = new ButtonSetting("Lowercase", false));
        this.registerSetting(showInfo = new ButtonSetting("Show module info", true));
    }

    public void onEnable() {
        ModuleManager.sort();
    }

    public void guiButtonToggled(ButtonSetting b) {
        if (b == alphabeticalSort || b == showInfo) {
            ModuleManager.sort();
        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent ev) {

        if (ev.phase != TickEvent.Phase.END || !Utils.nullCheck()) {
            return;
        }
        if (isAlphabeticalSort != alphabeticalSort.isToggled()) {
            isAlphabeticalSort = alphabeticalSort.isToggled();
            ModuleManager.sort();
        }
        if (canShowInfo != showInfo.isToggled()) {
            canShowInfo = showInfo.isToggled();
            ModuleManager.sort();
        }
        if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
            return;
        }

        int n = hudY;
        double n2 = 0.0;
        try {


            int n33 = hudX;
            int n32 = hudX + 60;
            int ne = hudY - 19;

            int n322 = hudX + ((int)alignw.getInput());
            int ne2 = hudY - 12;
            double n22 = 1.0;
            int ee = Theme.getGradient((int) theme.getInput(), n22);

            if (watermark.isToggled()) {

                String bFunText = "§l[BFun]";
                String ravenText = "§lRaven";

                mc.fontRendererObj.drawString(bFunText, n322, (float) ne2, ee, dropShadow.isToggled());


                float scale = 2.0f;
                GL11.glPushMatrix();
                GL11.glScalef(scale, scale, scale);


                float scaledN33 = n33 / scale;
                float scaledNe = ne / scale;

                mc.fontRendererObj.drawString(ravenText, scaledN33, scaledNe, ee, dropShadow.isToggled());

                // Restaurar a matriz de transformação original
                GL11.glPopMatrix();
            }
            for (Module module : ModuleManager.organizedModules) {
                if (module.isEnabled() && module != this) {
                    if (module.isHidden()) {
                        continue;
                    }
                    if (module == ModuleManager.commandLine) {
                        continue;
                    }
                    String moduleName = module.getName();
                    if (showInfo.isToggled() && !module.getInfo().isEmpty()) {
                        moduleName += " §7" + module.getInfo();
                    }
                    if (lowercase.isToggled()) {
                        moduleName = moduleName.toLowerCase();
                    }
                    int e = Theme.getGradient((int) theme.getInput(), n2);
                    if (theme.getInput() == 0) {
                        n2 -= 120;
                    } else {
                        n2 -= 12;
                    }
                    int n3 = hudX;
                    if (alignRight.isToggled()) {
                        n3 -= mc.fontRendererObj.getStringWidth(moduleName);
                    }
                    mc.fontRendererObj.drawString(moduleName, n3, (float) n, e, dropShadow.isToggled());
                    n += mc.fontRendererObj.FONT_HEIGHT + 2;

                }
            }
        }
        catch (Exception e) {
            Utils.sendMessage("&cAn error occurred rendering HUD. check your logs");
            e.printStackTrace();
        }
    }

    public static int getLongestModule(FontRenderer fr) {
        int length = 0;

        for (Module module : ModuleManager.organizedModules) {
            if (module.isEnabled()) {
                String moduleName = module.getName();
                if (showInfo.isToggled() && !module.getInfo().isEmpty()) {
                    moduleName += " §7" + module.getInfo();
                }
                if (lowercase.isToggled()) {
                    moduleName = moduleName.toLowerCase();
                }
                if (fr.getStringWidth(moduleName) > length) {
                    length = fr.getStringWidth(moduleName);
                }
            }
        }
        return length;
    }

    static class EditScreen extends GuiScreen {
        final String example = "This is an-Example-HUD";
        GuiButtonExt resetPosition;
        boolean d = false;
        int miX = 0;
        int miY = 0;
        int maX = 0;
        int maY = 0;
        int aX = 5;
        int aY = 70;
        int laX = 0;
        int laY = 0;
        int lmX = 0;
        int lmY = 0;
        int clickMinX = 0;

        public void initGui() {
            super.initGui();
            this.buttonList.add(this.resetPosition = new GuiButtonExt(1, this.width - 90, 5, 85, 20, "Reset position"));
            this.aX = HUD.hudX;
            this.aY = HUD.hudY;
        }

        public void drawScreen(int mX, int mY, float pt) {
            drawRect(0, 0, this.width, this.height, -1308622848);
            int miX = this.aX;
            int miY = this.aY;
            int maX = miX + 50;
            int maY = miY + 32;
            int[] clickPos = this.d(this.mc.fontRendererObj, this.example);
            this.miX = miX;
            this.miY = miY;
            if (clickPos == null) {
                this.maX = maX;
                this.maY = maY;
                this.clickMinX = miX;
            }
            else {
                this.maX = clickPos[0];
                this.maY = clickPos[1];
                this.clickMinX = clickPos[2];
            }
            HUD.hudX = miX;
            HUD.hudY = miY;
            ScaledResolution res = new ScaledResolution(this.mc);
            int x = res.getScaledWidth() / 2 - 84;
            int y = res.getScaledHeight() / 2 - 20;
            RenderUtils.dct("Edit the HUD position by dragging.", '-', x, y, 2L, 0L, true, this.mc.fontRendererObj);

            try {
                this.handleInput();
            } catch (IOException var12) {
            }

            super.drawScreen(mX, mY, pt);
        }

        private int[] d(FontRenderer fr, String t) {

            int n33 = hudX;
            int n32 = hudX + 60;
            int ne = hudY - 19;

            int n322 = hudX + ((int)alignw.getInput());
            int ne2 = hudY - 12;
            double n22 = 1.0;
            int ee = Theme.getGradient((int) theme.getInput(), n22);

            if (watermark.isToggled()) {

                String bFunText = "§l[BFun]";
                String ravenText = "§lRaven";

                mc.fontRendererObj.drawString(bFunText, n322, (float) ne2, ee, dropShadow.isToggled());


                float scale = 2.0f;
                GL11.glPushMatrix();
                GL11.glScalef(scale, scale, scale);


                float scaledN33 = n33 / scale;
                float scaledNe = ne / scale;

                mc.fontRendererObj.drawString(ravenText, scaledN33, scaledNe, ee, dropShadow.isToggled());

                // Restaurar a matriz de transformação original
                GL11.glPopMatrix();
            }

            if (empty()) {
                int x = this.miX;
                int y = this.miY;
                String[] var5 = t.split("-");

                for (String s : var5) {
                    if (HUD.alignRight.isToggled()) {
                        x += mc.fontRendererObj.getStringWidth(var5[0]) - mc.fontRendererObj.getStringWidth(s);
                    }
                    fr.drawString(s, (float) x, (float) y, Color.white.getRGB(), HUD.dropShadow.isToggled());
                    y += fr.FONT_HEIGHT + 2;
                }
            }
            else {
                int longestModule = getLongestModule(mc.fontRendererObj);
                int n = this.miY;
                double n2 = 0.0;
                for (Module module : ModuleManager.organizedModules) {
                    if (module.isEnabled() && !module.getName().equals("HUD")) {
                        if (module.isHidden()) {
                            continue;
                        }
                        if (module == ModuleManager.commandLine) {
                            continue;
                        }
                        String moduleName = module.getName();
                        if (showInfo.isToggled() && !module.getInfo().isEmpty()) {
                            moduleName += " §7" + module.getInfo();
                        }
                        if (lowercase.isToggled()) {
                            moduleName = moduleName.toLowerCase();
                        }
                        int e = Theme.getGradient((int) theme.getInput(), n2);
                        if (theme.getInput() == 0) {
                            n2 -= 120;
                        }
                        else {
                            n2 -= 12;
                        }
                        int n3 = this.miX;
                        if (alignRight.isToggled()) {
                            n3 -= mc.fontRendererObj.getStringWidth(moduleName);
                        }
                        mc.fontRendererObj.drawString(moduleName, n3, (float) n, e, dropShadow.isToggled());
                        n += mc.fontRendererObj.FONT_HEIGHT + 2;
                    }
                }
                return new int[]{this.miX + longestModule, n, this.miX - longestModule};
            }
            return null;
        }

        protected void mouseClickMove(int mX, int mY, int b, long t) {
            super.mouseClickMove(mX, mY, b, t);
            if (b == 0) {
                if (this.d) {
                    this.aX = this.laX + (mX - this.lmX);
                    this.aY = this.laY + (mY - this.lmY);
                } else if (mX > this.clickMinX && mX < this.maX && mY > this.miY && mY < this.maY) {
                    this.d = true;
                    this.lmX = mX;
                    this.lmY = mY;
                    this.laX = this.aX;
                    this.laY = this.aY;
                }

            }
        }

        protected void mouseReleased(int mX, int mY, int s) {
            super.mouseReleased(mX, mY, s);
            if (s == 0) {
                this.d = false;
            }

        }

        public void actionPerformed(GuiButton b) {
            if (b == this.resetPosition) {
                this.aX = HUD.hudX = 5;
                this.aY = HUD.hudY = 70;
            }

        }

        public boolean doesGuiPauseGame() {
            return false;
        }

        private boolean empty() {
            for (Module module : ModuleManager.organizedModules) {
                if (module.isEnabled() && !module.getName().equals("HUD")) {
                    if (module.isHidden()) {
                        continue;
                    }
                    if (module == ModuleManager.commandLine) {
                        continue;
                    }
                    return false;
                }
            }
            return true;
        }
    }
}
