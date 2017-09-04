package squidpony.squidgrid.gui.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import squidpony.ArrayTools;
import squidpony.annotation.Beta;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Radius;
import squidpony.squidmath.*;

import java.util.List;

/**
 * Various special effects that can be applied to a {@link SquidPanel} or {@link SparseLayers} as an
 * {@link com.badlogic.gdx.scenes.scene2d.Action}. The PanelEffect class is abstract and has implementations as static
 * inner classes, such as {@link ExplosionEffect}. Each PanelEffect specifically affects one {@link IPackedColorPanel}
 * (an interface that both SquidPanel and SparseLayers implement), which can be a layer in a SquidLayers object or a
 * SquidPanel/SparseLayers on its own. By adding the PanelEffect to any actor using
 * {@link com.badlogic.gdx.scenes.scene2d.Actor#addAction(Action)} when the Actor has its act() method called after the
 * normal map parts of the panel have been placed, the PanelEffect will advance and change what chars/colors are in
 * the panel as specified in its implementation of the {@link #update(float)} method. Typically the Actor you add this
 * to is the SquidPanel or SparseLayers this affects, but it could also be a SquidLayers. Most PanelEffect
 * implementations should allow most configuration to be set in their constructors.
 * <br>
 * Created by Tommy Ettinger on 5/24/2017.
 */
@Beta
public abstract class PanelEffect extends TemporalAction{
    public IPackedColorPanel target;
    public GreasedRegion validCells;

    protected PanelEffect(IPackedColorPanel targeting)
    {
        target = targeting;
        validCells = new GreasedRegion(targeting.gridWidth(), targeting.gridHeight()).allOn();
    }
    protected PanelEffect(IPackedColorPanel targeting, float duration)
    {
        target = targeting;
        setDuration(duration);
        validCells = new GreasedRegion(targeting.gridWidth(), targeting.gridHeight()).allOn();
    }
    protected PanelEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid)
    {
        target = targeting;
        setDuration(duration);
        validCells = valid;
    }
    @Beta
    public static class ExplosionEffect extends PanelEffect
    {
        /**
         * Normally you should set this in the constructor, and not change it later.
         */
        public Coord center;
        /**
         * Normally you should set this in the constructor, and not change it later.
         */
        public int radius = 2;
        /**
         * The default explosion colors are normal for (non-chemical, non-electrical) fire and smoke, going from orange
         * at the start to yellow, very light yellow, and then back to a different orange before going to smoke and
         * fading out to translucent and then transparent by the end.
         * <br>
         * If you want to change the colors the explosion uses, you can either pass a List of Color (or SColor or other
         * subclasses) to the constructor or change this array directly. The float items assigned to this should be the
         * result of calling {@link Color#toFloatBits()} or possibly the result of mixing multiple existing floats with
         * {@link SColor#lerpFloatColors(float, float, float)}; other floats that can be directly used by libGDX, that
         * is, packed as ABGR floats (usually the docs will call this a packed float), can also be used.
         */
        public float[] colors = {
                SColor.floatGet(0xFF4F00FF), // SColor.INTERNATIONAL_ORANGE
                SColor.floatGet(0xFFB94EFF), // SColor.FLORAL_LEAF
                SColor.floatGet(0xFDE910FF), // SColor.LEMON
                SColor.floatGet(0xFFFACDFF), // SColor.LEMON_CHIFFON
                SColor.floatGet(0xFF6600EE), // SColor.SAFETY_ORANGE
                SColor.floatGet(0x595652DD), // SColor.DB_SOOT
                SColor.floatGet(0x59565299)  // SColor.DB_SOOT
        };
        /**
         * The internal representation of how affected each cell is by the explosion, based on proximity to center.
         */
        public double[][] lightMap;
        /**
         * The raw list of Coords that might be affected by the explosion; may include some cells that aren't going to
         * show as exploding (it usually has some false positives), but shouldn't exclude any cells that should show as
         * such (no false negatives). You can edit this if you need to, but it isn't recommended.
         */
        public List<Coord> affected;
        /**
         * Constructs an ExplosionEffect with explicit settings for some fields. The valid cells this can affect will be
         * the full expanse of the IPackedColorPanel. The duration will be 1 second.
         * @param targeting the IPackedColorPanel to affect
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         */

        public ExplosionEffect(IPackedColorPanel targeting, Coord center, int radius)
        {
            this(targeting, 1f, center, radius);
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for some fields. The valid cells this can affect will be
         * the full expanse of the IPackedColorPanel.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         */
        public ExplosionEffect(IPackedColorPanel targeting, float duration, Coord center, int radius)
        {
            super(targeting, duration);
            this.center = center;
            this.radius = radius;
            double[][] resMap = new double[validCells.width][validCells.height];
            lightMap = new double[validCells.width][validCells.height];
            FOV.reuseFOV(resMap, lightMap, center.x, center.y, radius);
            affected = Radius.inCircle(center.x, center.y, radius, false, validCells.width, validCells.height);
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         */
        public ExplosionEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius)
        {
            super(targeting, duration, valid);
            this.center = center;
            this.radius = radius;
            double[][] resMap = ArrayTools.fill(1.0, validCells.width, validCells.height);
            validCells.writeDoublesInto(resMap, 0.0);
            lightMap = new double[validCells.width][validCells.height];
            FOV.reuseFOV(resMap, lightMap, center.x, center.y, radius + 0.5);
            validCells.not().writeDoublesInto(lightMap, 0.0);
            validCells.not();
            affected = Radius.inCircle(center.x, center.y, radius, false, validCells.width, validCells.height);
        }

        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using fiery/smoke colors.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param coloring a List of Color or subclasses thereof that will replace the default fire/smoke colors here
         */
        public ExplosionEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, List<? extends Color> coloring)
        {
            this(targeting, duration, valid, center, radius);
            if(colors.length != coloring.size())
                colors = new float[coloring.size()];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = coloring.get(i).toFloatBits();
            }
        }

        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using fiery/smoke colors.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param coloring an array of colors as packed floats that will replace the default fire/smoke colors here
         */
        public ExplosionEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, float[] coloring)
        {
            this(targeting, duration, valid, center, radius);
            if(coloring == null) return;
            if(colors.length != coloring.length)
                colors = new float[coloring.length];
            System.arraycopy(coloring, 0, colors, 0, coloring.length);
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields; this constructor allows the case where
         * an explosion is directed in a cone or sector shape. It will center the sector on {@code angle} (in degrees)
         * and will cover an amount of the circular area (in degrees) equal to {@code span}.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param angle the angle, in degrees, that will be the center of the sector-shaped effect
         * @param span the span, in degrees, of the full arc at the end of the sector-shaped effect
         */
        public ExplosionEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, double angle, double span)
        {
            super(targeting, duration, valid);
            this.center = center;
            this.radius = radius;
            double[][] resMap = ArrayTools.fill(1.0, validCells.width, validCells.height);
            validCells.writeDoublesInto(resMap, 0.0);
            lightMap = new double[validCells.width][validCells.height];
            FOV.reuseFOV(resMap, lightMap, center.x, center.y, radius + 0.5, Radius.CIRCLE, angle, span);
            validCells.not().writeDoublesInto(lightMap, 0.0);
            validCells.not();
            affected = Radius.inCircle(center.x, center.y, radius, false, validCells.width, validCells.height);
        }

        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using fiery/smoke colors; this constructor allows
         * the case where an explosion is directed in a cone or sector shape. It will center the sector on {@code angle}
         * (in degrees) and will cover an amount of the circular area (in degrees) equal to {@code span}.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param angle the angle, in degrees, that will be the center of the sector-shaped effect
         * @param span the span, in degrees, of the full arc at the end of the sector-shaped effect
         * @param coloring a List of Color or subclasses thereof that will replace the default fire/smoke colors here
         */
        public ExplosionEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, double angle, double span, List<? extends Color> coloring)
        {
            this(targeting, duration, valid, center, radius, angle, span);
            if(colors.length != coloring.size())
                colors = new float[coloring.size()];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = coloring.get(i).toFloatBits();
            }
        }

        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using fiery/smoke colors; this constructor allows
         * the case where an explosion is directed in a cone or sector shape. It will center the sector on {@code angle}
         * (in degrees) and will cover an amount of the circular area (in degrees) equal to {@code span}.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param angle the angle, in degrees, that will be the center of the sector-shaped effect
         * @param span the span, in degrees, of the full arc at the end of the sector-shaped effect
         * @param coloring an array of colors as packed floats that will replace the default fire/smoke colors here
         */
        public ExplosionEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, double angle, double span, float[] coloring)
        {
            this(targeting, duration, valid, center, radius, angle, span);
            if(coloring == null) return;
            if(colors.length != coloring.length)
                colors = new float[coloring.length];
            System.arraycopy(coloring, 0, colors, 0, coloring.length);
        }
        /**
         * Called each frame.
         *
         * @param percent The percentage of completion for this action, growing from 0 to 1 over the duration. If
         *                {@link #setReverse(boolean) reversed}, this will shrink from 1 to 0.
         */
        @Override
        protected void update(float percent) {
            int len = affected.size();
            Coord c;
            float f, color;
            int idx, seed = System.identityHashCode(this);
            for (int i = 0; i < len; i++) {
                c = affected.get(i);
                if(lightMap[c.x][c.y] <= 0.0)// || 0.6 * (lightMap[c.x][c.y] + percent) < 0.25)
                    continue;
                f = (float)SeededNoise.noise(c.x * 1.5, c.y * 1.5, percent * 5, seed)
                        * 0.17f + percent * 1.2f;
                if(f < 0f || 0.5 * lightMap[c.x][c.y] + f < 0.4)
                    continue;
                idx = (int) (f * colors.length);
                if(idx >= colors.length - 1)
                    color = SColor.lerpFloatColors(colors[colors.length-1], NumberTools.setSelectedByte(colors[colors.length-1], 3, (byte)0), (Math.min(0.99f, f) * colors.length) % 1f);
                else
                    color = SColor.lerpFloatColors(colors[idx], colors[idx+1], (f * colors.length) % 1f);
                target.put(c.x, c.y, color);
            }
        }
    }
    public static class GibberishEffect extends ExplosionEffect
    {
        /**
         * This char array contains all characters that can be used in the foreground of this effect. You can assign
         * another char array, such as if you take {@link squidpony.StringKit#PUNCTUATION} and call
         * {@link String#toCharArray()} on it, to this at any time between calls to {@link #update(float)} (which is
         * usually called indirectly via Stage's {@link com.badlogic.gdx.scenes.scene2d.Stage#act()} method if this has
         * been added to an Actor on that Stage). These chars are pseudo-randomly selected approximately once every
         * eighth of a second, and may change sooner if the effect expands more quickly than that.
         */
        public char[] choices = "`~!@#$%^&*()-_=+\\|][}{'\";:/?.>,<".toCharArray();

        /**
         * Sets the colors this GibberishEffect uses to go from through various shades of gray-purple before fading.
         * Meant for electrical bursts, this will affect character foregrounds in a GibberishEffect. This should look
         * like sparks in GibberishEffect if the chars in {@link #choices} are selected in a way that fits that theme.
         */
        public void useElectricColors()
        {
            colors[0] = SColor.floatGet(0xCCCCFFEE); // SColor.PERIWINKLE
            colors[1] = SColor.floatGet(0xBF00FFFF); // SColor.ELECTRIC_PURPLE
            colors[2] = SColor.floatGet(0xCC99CCFF); // SColor.MEDIUM_LAVENDER_MAGENTA
            colors[3] = SColor.floatGet(0xC8A2C8EE); // SColor.LILAC
            colors[4] = SColor.floatGet(0xBF00FFDD); // SColor.ELECTRIC_PURPLE
            colors[5] = SColor.floatGet(0x6022EEBB); // SColor.ELECTRIC_INDIGO
            colors[6] = SColor.floatGet(0x4B008277); // SColor.INDIGO
        }

        /**
         * Sets the colors this GibberishEffect uses to go from orange, to yellow, to orange, to dark gray, then fade.
         * Meant for fiery explosions with smoke, this will affect character foregrounds in a GibberishEffect.
         * This may look more like a fiery blast if used with {@link ExplosionEffect}.
         */
        public void useFieryColors()
        {
            colors[0] = SColor.floatGet(0xFF4F00FF); // SColor.INTERNATIONAL_ORANGE
            colors[1] = SColor.floatGet(0xFFB94EFF); // SColor.FLORAL_LEAF
            colors[2] = SColor.floatGet(0xFDE910FF); // SColor.LEMON
            colors[3] = SColor.floatGet(0xFFFACDFF); // SColor.LEMON_CHIFFON
            colors[4] = SColor.floatGet(0xFF6600EE); // SColor.SAFETY_ORANGE
            colors[5] = SColor.floatGet(0x595652DD); // SColor.DB_SOOT
            colors[6] = SColor.floatGet(0x59565299); // SColor.DB_SOOT

        }
        public GibberishEffect(IPackedColorPanel targeting, Coord center, int radius)
        {
            super(targeting, 1f, center, radius);
            useElectricColors();
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for some fields. The valid cells this can affect will be
         * the full expanse of the IPackedColorPanel.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, Coord center, int radius) {
            super(targeting, duration, center, radius);
            useElectricColors();
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for some fields. The valid cells this can affect will be
         * the full expanse of the IPackedColorPanel.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, Coord center, int radius, char[] choices) {
            super(targeting, duration, center, radius);
            this.choices = choices;
            useElectricColors();
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius)
        {
            super(targeting, duration, valid, center, radius);
            useElectricColors();
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, char[] choices)
        {
            super(targeting, duration, valid, center, radius);
            this.choices = choices;
            useElectricColors();
        }

        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using purple spark colors.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param coloring a List of Color or subclasses thereof that will replace the default purple spark colors here
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, List<? extends Color> coloring)
        {
            super(targeting, duration, valid, center, radius, coloring);
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using purple spark colors.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param coloring a List of Color or subclasses thereof that will replace the default purple spark colors here
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, List<? extends Color> coloring, char[] choices)
        {
            super(targeting, duration, valid, center, radius, coloring);
            this.choices = choices;
        }

        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using purple spark colors.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param coloring an array of colors as packed floats that will replace the default purple spark colors here
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, float[] coloring)
        {
            super(targeting, duration, valid, center, radius, coloring);
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using purple spark colors.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param coloring an array of colors as packed floats that will replace the default purple spark colors here
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, float[] coloring, char[] choices)
        {
            super(targeting, duration, valid, center, radius, coloring);
            this.choices = choices;
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields; this constructor allows
         * the case where an explosion is directed in a cone or sector shape. It will center the sector on {@code angle}
         * (in degrees) and will cover an amount of the circular area (in degrees) equal to {@code span}.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param angle the angle, in degrees, that will be the center of the sector-shaped effect
         * @param span the span, in degrees, of the full arc at the end of the sector-shaped effect
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, double angle, double span, char[] choices)
        {
            super(targeting, duration, valid, center, radius, angle, span);
            this.choices = choices;
            useElectricColors();
        }

        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using purple spark colors; this constructor allows
         * the case where an explosion is directed in a cone or sector shape. It will center the sector on {@code angle}
         * (in degrees) and will cover an amount of the circular area (in degrees) equal to {@code span}.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param angle the angle, in degrees, that will be the center of the sector-shaped effect
         * @param span the span, in degrees, of the full arc at the end of the sector-shaped effect
         * @param coloring a List of Color or subclasses thereof that will replace the default purple spark colors here
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, double angle, double span, List<? extends Color> coloring)
        {
            super(targeting, duration, valid, center, radius, angle, span, coloring);
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using purple spark colors; this constructor allows
         * the case where an explosion is directed in a cone or sector shape. It will center the sector on {@code angle}
         * (in degrees) and will cover an amount of the circular area (in degrees) equal to {@code span}.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param angle the angle, in degrees, that will be the center of the sector-shaped effect
         * @param span the span, in degrees, of the full arc at the end of the sector-shaped effect
         * @param coloring a List of Color or subclasses thereof that will replace the default purple spark colors here
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, double angle, double span, List<? extends Color> coloring, char[] choices)
        {
            super(targeting, duration, valid, center, radius, angle, span, coloring);
            this.choices = choices;
        }

        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using purple spark colors; this constructor allows
         * the case where an explosion is directed in a cone or sector shape. It will center the sector on {@code angle}
         * (in degrees) and will cover an amount of the circular area (in degrees) equal to {@code span}.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param angle the angle, in degrees, that will be the center of the sector-shaped effect
         * @param span the span, in degrees, of the full arc at the end of the sector-shaped effect
         * @param coloring an array of colors as packed floats that will replace the default purple spark colors here
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, double angle, double span,  float[] coloring)
        {
            super(targeting, duration, valid, center, radius, angle, span, coloring);
        }
        /**
         * Constructs an ExplosionEffect with explicit settings for most fields but also an alternate group of Color
         * objects that it will use to color the explosion instead of using purple spark colors; this constructor allows
         * the case where an explosion is directed in a cone or sector shape. It will center the sector on {@code angle}
         * (in degrees) and will cover an amount of the circular area (in degrees) equal to {@code span}.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param center the center of the explosion
         * @param radius the radius of the explosion, in cells
         * @param angle the angle, in degrees, that will be the center of the sector-shaped effect
         * @param span the span, in degrees, of the full arc at the end of the sector-shaped effect
         * @param coloring an array of colors as packed floats that will replace the default purple spark colors here
         */
        public GibberishEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord center, int radius, double angle, double span, float[] coloring, char[] choices)
        {
            super(targeting, duration, valid, center, radius, angle, span, coloring);
            this.choices = choices;
        }
        /**
         * Called each frame.
         *
         * @param percent The percentage of completion for this action, growing from 0 to 1 over the duration. If
         *                {@link #setReverse(boolean) reversed}, this will shrink from 1 to 0.
         */
        @Override
        protected void update(float percent) {
            int len = affected.size();
            Coord c;
            float f, color;
            int idx, seed = System.identityHashCode(this), clen = choices.length;
            final long tick = LightRNG.determine((System.currentTimeMillis() >>> 7) * seed);
            for (int i = 0; i < len; i++) {
                c = affected.get(i);
                if(lightMap[c.x][c.y] <= 0.0)// || 0.6 * (lightMap[c.x][c.y] + percent) < 0.25)
                    continue;
                f = (float)SeededNoise.noise(c.x * 1.5, c.y * 1.5, percent * 5, seed)
                        * 0.17f + percent * 1.2f;
                if(f < 0f || 0.5 * lightMap[c.x][c.y] + f < 0.4)
                    continue;
                idx = (int) (f * colors.length);
                if(idx >= colors.length - 1)
                    color = SColor.lerpFloatColors(colors[colors.length-1], NumberTools.setSelectedByte(colors[colors.length-1], 3, (byte)0), (Math.min(0.99f, f) * colors.length) % 1f);
                else
                    color = SColor.lerpFloatColors(colors[idx], colors[idx+1], (f * colors.length) % 1f);
                target.put(c.x, c.y, choices[(int) ((clen * (LightRNG.determine(tick + i) & 0x7FFFFFFFL)) >> 31)], color);
            }
        }

    }
    @Beta
    public static class ProjectileEffect extends PanelEffect
    {
        /**
         * Normally you should set this in the constructor, and not change it later.
         */
        public Coord startPoint;
        /**
         * Normally you should set this in the constructor, and not change it later.
         */
        public Coord endPoint;

        /**
         * The char to show at each stage of the projectile's path; defaults to a Unicode bullet symbol, '·'.
         */
        public char shown = '·';
        /**
         * The color used for the projectile as a packed float.
         */
        public float color = Color.WHITE.toFloatBits();
        /**
         * The raw list of Coords that might be affected by the projectile, or are on its (potential) path. You can edit
         * this if you need to, but it isn't recommended; because it is an array you would need to assign a new Coord
         * array if the length changes.
         */
        public Coord[] affected;
        /**
         * Constructs a ProjectileEffect with explicit settings for some fields. The valid cells this can affect will be
         * the full expanse of the IPackedColorPanel. The duration will be 1 second.
         * @param targeting the IPackedColorPanel to affect
         * @param startPoint the starting point of the projectile; may be best if it is adjacent to whatever fires it
         * @param endPoint the point to try to hit with the projectile; this should always succeed with no obstructions
         */
        public ProjectileEffect(IPackedColorPanel targeting, Coord startPoint, Coord endPoint)
        {
            this(targeting, 1f, startPoint, endPoint);
        }
        /**
         * Constructs a ProjectileEffect with explicit settings for some fields. The valid cells this can affect will be
         * the full expanse of the IPackedColorPanel.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param startPoint the starting point of the projectile; may be best if it is adjacent to whatever fires it
         * @param endPoint the point to try to hit with the projectile; this should always succeed with no obstructions
         */
        public ProjectileEffect(IPackedColorPanel targeting, float duration, Coord startPoint, Coord endPoint)
        {
            super(targeting, duration);
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            affected = Bresenham.line2D_(startPoint.x,  startPoint.y, endPoint.x, endPoint.y);
        }
        /**
         * Constructs a ProjectileEffect with explicit settings for most fields.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param startPoint the starting point of the projectile; may be best if it is adjacent to whatever fires it
         * @param endPoint the point to try to hit with the projectile; this may not be reached if the path crosses a cell not in valid
         */
        public ProjectileEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord startPoint, Coord endPoint)
        {
            super(targeting, duration, valid);
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            affected = Bresenham.line2D_(startPoint.x,  startPoint.y, endPoint.x, endPoint.y);
            for (int i = 0; i < affected.length; i++) {
                if(!validCells.contains(affected[i]))
                    affected[i] = null;
            }
        }

        /**
         * Constructs a ProjectileEffect with explicit settings for most fields but also an alternate Color
         * object for the projectile instead of the default white color.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param startPoint the starting point of the projectile; may be best if it is adjacent to whatever fires it
         * @param endPoint the point to try to hit with the projectile; this may not be reached if the path crosses a cell not in valid
         * @param shown the char to show at each step of the projectile's path as it advances
         * @param coloring a Color or subclass thereof that will replace the default white color here
         */
        public ProjectileEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord startPoint, Coord endPoint, char shown, Color coloring)
        {
            this(targeting, duration, valid, startPoint, endPoint);
            this.shown = shown;
            if(coloring != null)
                color = coloring.toFloatBits();
        }

        /**
         * Constructs a ProjectileEffect with explicit settings for most fields but also an alternate Color
         * object for the projectile instead of the default white color.
         * @param targeting the IPackedColorPanel to affect
         * @param duration the duration of this PanelEffect in seconds, as a float
         * @param valid the valid cells that can be changed by this PanelEffect, as a GreasedRegion
         * @param startPoint the starting point of the projectile; may be best if it is adjacent to whatever fires it
         * @param endPoint the point to try to hit with the projectile; this may not be reached if the path crosses a cell not in valid
         * @param shown the char to show at each step of the projectile's path as it advances
         * @param coloring an array of colors as packed floats that will replace the default white color here
         */
        public ProjectileEffect(IPackedColorPanel targeting, float duration, GreasedRegion valid, Coord startPoint, Coord endPoint, char shown, float coloring)
        {
            this(targeting, duration, valid, startPoint, endPoint);
            this.shown = shown;
            color = coloring;
        }

        /**
         * Makes this ProjectileEffect take an "arc-like" path toward the target, where it is fast at the
         * beginning and end of its motion and is reaching the height of its arc at the center.
         */
        public void useArcPathInterpolation()
        {
            setInterpolation(fastInSlowMidFastOut);
        }

        /**
         * Makes this ProjectileEffect take a direct path to the target, traveling at uniform speed throughout its path.
         */
        public void useStraightPathInterpolation()
        {
            setInterpolation(Interpolation.linear);
        }
        /**
         * Called each frame.
         *
         * @param percent The percentage of completion for this action, growing from 0 to 1 over the duration. If
         *                {@link #setReverse(boolean) reversed}, this will shrink from 1 to 0.
         */
        @Override
        protected void update(float percent) {
            int len = affected.length, index = (int)((len - 1) * percent);
            if(index < 0 || index >= len) return;
            Coord c = affected[index];
            if(c != null)
            {
                target.put(c.x, c.y, shown, color);
            }
            else {
                for (int i = index + 1; i < len; i++) {
                    affected[i] = null;
                }
            }
        }
    }

    public static Interpolation fastInSlowMidFastOut = new Interpolation() {
        private final float value = 2, power = 3;
        @Override
        public float apply(float a) {
            if (a <= 0.5f) return (1 - ((float)Math.pow(value, -power * (a * 2)) - 0.125f) * 1.1428572f) * 0.5f;
            return (1 + (float) Math.pow(value, power * (a * 2 - 2)) - 0.25f) * 0.5714286f;
        }
    };

    /**
     * Convenience method to make a ProjectileEffect take an "arc-like" path toward the target, where it is fast at the
     * beginning and end of its motion and is reaching the height of its arc at the center, before triggering another
     * Action when the projectile stops (often this might be an {@link PanelEffect.ExplosionEffect}, but could be any
     * scene2d Action).
     * @param projectile a {@link PanelEffect.ProjectileEffect} to run as the first step
     * @param result an {@link Action} to run after the ProjectileEffect completes
     * @return an Action that can be added to a scene2d Actor, such as a SquidLayers or SparseLayers
     */
    public static Action makeGrenadeEffect(ProjectileEffect projectile, Action result)
    {
        projectile.useArcPathInterpolation();
        return Actions.sequence(projectile, result);
    }
}
