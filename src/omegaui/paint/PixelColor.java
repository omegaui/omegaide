/**
 * Enhanced Color Class
 * Copyright (C) 2022 Omega UI

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package omegaui.paint;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;

public class PixelColor extends Color {

    /**
     * Creates an opaque sRGB color with the specified red, green,
     * and blue values in the range (0 - 255).
     * The actual color used in rendering depends
     * on finding the best match given the color space
     * available for a given output device.
     * Alpha is defaulted to 255.
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @throws IllegalArgumentException if {@code r}, {@code g}
     *                                  or {@code b} are outside of the range
     *                                  0 to 255, inclusive
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getRGB
     */
    public PixelColor(int r, int g, int b) {
        super(r, g, b);
    }

    /**
     * Creates an sRGB color with the specified red, green, blue, and alpha
     * values in the range (0 - 255).
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     * @throws IllegalArgumentException if {@code r}, {@code g},
     *                                  {@code b} or {@code a} are outside of the range
     *                                  0 to 255, inclusive
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getAlpha
     * @see #getRGB
     */
    public PixelColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    /**
     * Creates an opaque sRGB color with the specified combined RGB value
     * consisting of the red component in bits 16-23, the green component
     * in bits 8-15, and the blue component in bits 0-7.  The actual color
     * used in rendering depends on finding the best match given the
     * color space available for a particular output device.  Alpha is
     * defaulted to 255.
     *
     * @param rgb the combined RGB components
     * @see ColorModel#getRGBdefault
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getRGB
     */
    public PixelColor(int rgb) {
        super(rgb);
    }

    /**
     * Creates an sRGB color with the specified combined RGBA value consisting
     * of the alpha component in bits 24-31, the red component in bits 16-23,
     * the green component in bits 8-15, and the blue component in bits 0-7.
     * If the {@code hasalpha} argument is {@code false}, alpha
     * is defaulted to 255.
     *
     * @param rgba     the combined RGBA components
     * @param hasalpha {@code true} if the alpha bits are valid;
     *                 {@code false} otherwise
     * @see ColorModel#getRGBdefault
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getAlpha
     * @see #getRGB
     */
    public PixelColor(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
    }

    /**
     * Creates an opaque sRGB color with the specified red, green, and blue
     * values in the range (0.0 - 1.0).  Alpha is defaulted to 1.0.  The
     * actual color used in rendering depends on finding the best
     * match given the color space available for a particular output
     * device.
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @throws IllegalArgumentException if {@code r}, {@code g}
     *                                  or {@code b} are outside of the range
     *                                  0.0 to 1.0, inclusive
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getRGB
     */
    public PixelColor(float r, float g, float b) {
        super(r, g, b);
    }

    /**
     * Creates an sRGB color with the specified red, green, blue, and
     * alpha values in the range (0.0 - 1.0).  The actual color
     * used in rendering depends on finding the best match given the
     * color space available for a particular output device.
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     * @throws IllegalArgumentException if {@code r}, {@code g}
     *                                  {@code b} or {@code a} are outside of the range
     *                                  0.0 to 1.0, inclusive
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @see #getAlpha
     * @see #getRGB
     */
    public PixelColor(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    /**
     * Creates a color in the specified {@code ColorSpace}
     * with the color components specified in the {@code float}
     * array and the specified alpha.  The number of components is
     * determined by the type of the {@code ColorSpace}.  For
     * example, RGB requires 3 components, but CMYK requires 4
     * components.
     *
     * @param cspace     the {@code ColorSpace} to be used to
     *                   interpret the components
     * @param components an arbitrary number of color components
     *                   that is compatible with the {@code ColorSpace}
     * @param alpha      alpha value
     * @throws IllegalArgumentException if any of the values in the
     *                                  {@code components} array or {@code alpha} is
     *                                  outside of the range 0.0 to 1.0
     * @see #getComponents
     * @see #getColorComponents
     */
    public PixelColor(ColorSpace cspace, float[] components, float alpha) {
        super(cspace, components, alpha);
    }

    /**
     * Generates the corresponding color with the supplied red value
     * @param red value of the red color
     * @return The Corresponding color
     */
    public Color withRed(int red){
        return new Color(red, getGreen(), getBlue());
    }

    /**
     * Generates the corresponding color with the supplied green value
     * @param green value of the green color
     * @return The Corresponding color
     */
    public Color withGreen(int green){
        return new Color(getRed(), green, getBlue());
    }

    /**
     * Generates the corresponding color with the supplied blue value
     * @param blue value of the blue color
     * @return The Corresponding color
     */
    public Color withBlue(int blue){
        return new Color(getRed(), getGreen(), blue);
    }

    /**
     * Generates the corresponding color with the supplied opacity
     * @param opacity opacity of the current color, allowed range is from 0f to 1f i.e. 0% to 100%
     * @return The Corresponding color
     */
    public Color withOpacity(float opacity){
        int alpha = (int)(opacity * 255);
        return new Color(getRed(), getGreen(), getBlue(), alpha);
    }
}
