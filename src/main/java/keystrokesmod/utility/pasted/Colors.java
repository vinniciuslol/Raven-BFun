package keystrokesmod.utility.pasted;

import com.ibm.icu.text.NumberFormat;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class Colors {
    public static final int WHITE;
    public static final int RED;
    public static final int PINK;
    public static final int PURPLE;
    public static final int DEEP_PURPLE;
    public static final int INDIGO;
    public static final int BLUE;
    public static final int LIGHT_BLUE;
    public static final int CYAN;
    public static final int TEAL;
    public static final int GREEN;

    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
        color |= MathHelper.clamp_int(red, 0, 255) << 16;
        color |= MathHelper.clamp_int(green, 0, 255) << 8;
        color |= MathHelper.clamp_int(blue, 0, 255);
        return color;
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        } else if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        } else if (fractions.length == colors.length) {
            int[] indicies = getFractionIndicies(fractions, progress);
            float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
            Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            Color color = blend(colorRange[0], colorRange[1], (double)(1.0F - weight));
            return color;
        } else {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
    }

    public static double getIncremental(double val, double inc) {
        double one = 1.0D / inc;
        return (double)Math.round(val * one) / one;
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int[] range = new int[2];

        int startPoint;
        for(startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }

        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }

        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0F - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0F) {
            red = 0.0F;
        } else if (red > 255.0F) {
            red = 255.0F;
        }

        if (green < 0.0F) {
            green = 0.0F;
        } else if (green > 255.0F) {
            green = 255.0F;
        }

        if (blue < 0.0F) {
            blue = 0.0F;
        } else if (blue > 255.0F) {
            blue = 255.0F;
        }

        Color color3 = null;

        try {
            color3 = new Color(red, green, blue);
        } catch (IllegalArgumentException var14) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format((double)red) + "; " + nf.format((double)green) + "; " + nf.format((double)blue));
            var14.printStackTrace();
        }

        return color3;
    }

    static {
        WHITE = Color.WHITE.getRGB();
        RED = (new Color(16007990)).getRGB();
        PINK = (new Color(16744619)).getRGB();
        PURPLE = (new Color(12216520)).getRGB();
        DEEP_PURPLE = (new Color(8281781)).getRGB();
        INDIGO = (new Color(7964363)).getRGB();
        BLUE = (new Color(1668818)).getRGB();
        LIGHT_BLUE = (new Color(7652351)).getRGB();
        CYAN = (new Color(44225)).getRGB();
        TEAL = (new Color(11010027)).getRGB();
        GREEN = (new Color(65350)).getRGB();
    }
}
