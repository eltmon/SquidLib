package squidpony.gdx.tests;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.StringKit;
import squidpony.squidgrid.gui.gdx.*;

import static squidpony.StringKit.safeSubstring;

/**
 * Created by Tommy Ettinger on 12/27/2016.
 */
public class ColorTest extends ApplicationAdapter {
    /**
     * In number of cells
     */
    private static int gridWidth = 140;
    /**
     * In number of cells
     */
    private static int gridHeight = 27;

    /**
     * The pixel width of a cell
     */
    private static int cellWidth = 8;
    /**
     * The pixel height of a cell
     */
    private static int cellHeight = 16;

    private static int totalWidth = gridWidth * cellWidth, totalHeight = gridHeight * cellHeight;

    private Stage stage;
    private SpriteBatch batch;
    private Viewport viewport;
    private TextCellFactory tcf;
    private SquidLayers display;

    @Override
    public void create() {
        batch = new SpriteBatch();
        tcf = DefaultResources.getStretchableSlabFont();//.width(cellWidth).height(cellHeight).initBySize();
        viewport = new StretchViewport(totalWidth, totalHeight);
        display = new SquidLayers(gridWidth, gridHeight, cellWidth, cellHeight, tcf).setTextSize(9, 17);
        stage = new Stage(viewport, batch);
        SquidColorCenter scc = DefaultResources.getSCC();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if(keycode == Input.Keys.Q || keycode == Input.Keys.ESCAPE)
                    Gdx.app.exit();
                return true;
            }
        });
        Gdx.graphics.setTitle("SquidLib Demo: Colors");
//        for (int i = 0; i < 32; i++) {
//            SColor db = SColor.DAWNBRINGER_32[i];
//            display.putString(0, i, "                                ", db, db);
//            display.putString(1, i, db.name, scc.invert(db), db);
//        }
        for (int h = 0; h < 7; h++) {
            for (int v = 0; v < 27; v++) {
                SColor cw = SColor.COLOR_WHEEL_PALETTE[h * 27 + v];
                display.putString(h * 20, v, StringKit.padRightStrict(cw.name.substring(3), 20), scc.invert(cw), cw);
                //display.put(h, v, scc.getHSV(h * (1f / gridWidth), 0.75f, (8 - v) / 8f));
            }
        }

        stage.addActor(display);

   //This block, when uncommented, will generate the color wheel palette code for SColor and print it to stdout.
        /*

    /**
     * Color constant<PRE>
     * <font style='background-color: #ff0000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #000000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #888888; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ffffff; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ff0000; color: #000000'>&nbsp;@&nbsp;</font>
     * <font style='background-color: #ff0000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #000000; color: #ff0000'>&nbsp;@&nbsp;</font><font style='background-color: #888888; color: #ff0000'>&nbsp;@&nbsp;</font><font style='background-color: #ffffff; color: #ff0000'>&nbsp;@&nbsp;</font><font style='background-color: #ff0000; color: #888888'>&nbsp;@&nbsp;</font>
     * <font style='background-color: #ff0000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #000000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #888888; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ffffff; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ff0000; color: #ffffff'>&nbsp;@&nbsp;</font>
     * <br>
     * <font style='background-color: #ff0000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ffff00; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #00ff00; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #0000ff; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #964b00; color: #000000'>&nbsp;&nbsp;&nbsp;</font>
     * <font style='background-color: #ff0000; color: #ff0000'>&nbsp;@&nbsp;</font><font style='background-color: #ffff00; color: #ff0000'>&nbsp;@&nbsp;</font><font style='background-color: #00ff00; color: #ff0000'>&nbsp;@&nbsp;</font><font style='background-color: #0000ff; color: #ff0000'>&nbsp;@&nbsp;</font><font style='background-color: #964b00; color: #ff0000'>&nbsp;@&nbsp;</font>
     * <font style='background-color: #ff0000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ffff00; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #00ff00; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #0000ff; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #964b00; color: #000000'>&nbsp;&nbsp;&nbsp;</font></PRE>
     */
        String template = "/**\n" +
            "* This color constant \"Name\" has RGB code 0xFEDCBA, hue `HUE, saturation `SAT, and value `VAL.\n" +
            "* <pre>\n" +
            "* <font style='background-color: #FEDCBA; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #000000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #888888; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ffffff; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #FEDCBA; color: #000000'>&nbsp;@&nbsp;</font>\n" +
            "* <font style='background-color: #FEDCBA; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #000000; color: #FEDCBA'>&nbsp;@&nbsp;</font><font style='background-color: #888888; color: #FEDCBA'>&nbsp;@&nbsp;</font><font style='background-color: #ffffff; color: #FEDCBA'>&nbsp;@&nbsp;</font><font style='background-color: #FEDCBA; color: #888888'>&nbsp;@&nbsp;</font>\n" +
            "* <font style='background-color: #FEDCBA; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #000000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #888888; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ffffff; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #FEDCBA; color: #ffffff'>&nbsp;@&nbsp;</font>\n" +
            "* <br>\n" +
            "* <font style='background-color: #ff0000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ffff00; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #00ff00; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #0000ff; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #964b00; color: #000000'>&nbsp;&nbsp;&nbsp;</font>\n" +
            "* <font style='background-color: #ff0000; color: #FEDCBA'>&nbsp;@&nbsp;</font><font style='background-color: #ffff00; color: #FEDCBA'>&nbsp;@&nbsp;</font><font style='background-color: #00ff00; color: #FEDCBA'>&nbsp;@&nbsp;</font><font style='background-color: #0000ff; color: #FEDCBA'>&nbsp;@&nbsp;</font><font style='background-color: #964b00; color: #FEDCBA'>&nbsp;@&nbsp;</font>\n" +
            "* <font style='background-color: #ff0000; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #ffff00; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #00ff00; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #0000ff; color: #000000'>&nbsp;&nbsp;&nbsp;</font><font style='background-color: #964b00; color: #000000'>&nbsp;&nbsp;&nbsp;</font></pre>\n" +
            "*/\n" +
        "public static final SColor NAME = new SColor(0xFEDCBA, \"Name\");\n\n";
//        // 0 red, 2 orange, 3 apricot, 4 gold, 5 yellow, 6 chartreuse, 7 lime, 8 honeydew, 10 green, 12 jade,
//        // 14 seafoam, 16 cyan, 17 azure, 19 blue, 21 sapphire, 22 indigo, 24 violet, 26 purple, 28 magenta, 30 rose
//        String[] names = {"Red", null, "Orange", "Apricot", "Gold", "Yellow", "Chartreuse", "Lime", "Honeydew", null,
//                "Green", null, null, "Jade", "Seafoam", null, "Cyan", "Azure", null, "Blue", null, "Sapphire",
//                "Indigo", null, "Violet", null, "Purple", null, "Magenta", null, "Rose", null};
//        OrderedMap<String, Float> satMods = Maker.makeOM("Red", 0f,
//                "Orange", 0.025f,
//                "Apricot", -0.1f,
//                "Gold", 0.05f,
//                "Yellow", 0.025f,
//                "Chartreuse", 0.025f,
//                "Lime", 0.075f,
//                "Honeydew", -0.12f,
//                "Green", 0f,
//                "Jade", -0.13f,
//                "Seafoam", -0.05f,
//                "Cyan", 0.075f,
//                "Azure", -0.05f,
//                "Blue", 0f,
//                "Sapphire", 0.025f,
//                "Indigo", 0.1f,
//                "Violet", 0.04f,
//                "Purple", -0.05f,
//                "Magenta", 0.035f,
//                "Rose", 0.05f);
//        OrderedMap<String, Float> valMods = Maker.makeOM("Red", 0.01f,
//                "Orange", 0.02f,
//                "Apricot", 0.035f,
//                "Gold", -0.005f,
//                "Yellow", 0.04f,
//                "Chartreuse", 0.01f,
//                "Lime", 0.01f,
//                "Honeydew", 0.04f,
//                "Green", -0.01f,
//                "Jade", -0.04f,
//                "Seafoam", 0.03f,
//                "Cyan", -0.01f,
//                "Azure", -0.03f,
//                "Blue", -0.01f,
//                "Sapphire", -0.02f,
//                "Indigo", -0.03f,
//                "Violet", -0.02f,
//                "Purple", -0.01f,
//                "Magenta", -0.02f,
//                "Rose", -0.03f);
//        for (int i = 0; i < 32; i++) {
//            String nm = names[i];
//            if(nm == null)
//                continue;
//            Color baseColor = scc.getHSV(i * 0.03125f, 0.825f + satMods.getOrDefault(nm, 0f), 0.925f + valMods.getOrDefault(nm, 0f));
//            System.out.println(template.replace("Name", "CW " + nm)
//                    .replace("NAME", "CW_" + nm.toUpperCase())
//                    .replace("FEDCBA", baseColor.toString().substring(0, 6)));
//            System.out.println(template.replace("Name", "CW Faded " + nm)
//                    .replace("NAME", "CW_FADED_" + nm.toUpperCase())
//                    .replace("FEDCBA", scc.desaturate(scc.light(baseColor, 0.15f), 0.5f).toString().substring(0, 6)));
//            System.out.println(template.replace("Name", "CW Flush " + nm)
//                    .replace("NAME", "CW_FLUSH_" + nm.toUpperCase())
//                    .replace("FEDCBA", scc.saturate(scc.dim(baseColor, 0.05f), 0.5f).toString().substring(0, 6)));
//
//            System.out.println(template.replace("Name", "CW Light " + nm)
//                    .replace("NAME", "CW_LIGHT_" + nm.toUpperCase())
//                    .replace("FEDCBA", scc.desaturate(scc.light(baseColor, 0.4f), 0.1f).toString().substring(0, 6)));
//            System.out.println(template.replace("Name", "CW Pale " + nm)
//                    .replace("NAME", "CW_PALE_" + nm.toUpperCase())
//                    .replace("FEDCBA", scc.desaturate(scc.light(baseColor, 0.55f), 0.3f).toString().substring(0, 6)));
//            System.out.println(template.replace("Name", "CW Bright " + nm)
//                    .replace("NAME", "CW_BRIGHT_" + nm.toUpperCase())
//                    .replace("FEDCBA", scc.saturate(scc.light(baseColor, 0.35f), 0.5f).toString().substring(0, 6)));
//
//            System.out.println(template.replace("Name", "CW Dark " + nm)
//                    .replace("NAME", "CW_DARK_" + nm.toUpperCase())
//                    .replace("FEDCBA", scc.saturate(scc.dim(baseColor, 0.25f), 0.2f).toString().substring(0, 6)));
//            System.out.println(template.replace("Name", "CW Drab " + nm)
//                    .replace("NAME", "CW_DRAB_" + nm.toUpperCase())
//                    .replace("FEDCBA", scc.desaturate(scc.dim(baseColor, 0.2f), 0.4f).toString().substring(0, 6)));
//            System.out.println(template.replace("Name", "CW Rich " + nm)
//                    .replace("NAME", "CW_RICH_" + nm.toUpperCase())
//                    .replace("FEDCBA", scc.saturate(scc.dim(baseColor, 0.2f), 0.5f).toString().substring(0, 6)));
//        }
        String data = Gdx.files.classpath("ColorData.txt").readString();
        String[] lines = StringKit.split(data, "\n"), rec = new String[3];
        Color c = new Color();
        StringBuilder sb = new StringBuilder(100000);
        for (int i = 0; i < lines.length; i++) {
            tabSplit(rec, lines[i]);
            Color.argb8888ToColor(c, Integer.parseInt(rec[1], 16));
            sb.append(template.replace("Name", rec[2])
                    .replace("NAME", rec[0])
                    .replace("FEDCBA", rec[1].toUpperCase())
                    .replace("`HUE", Float.toString(scc.getHue(c)))
                    .replace("`SAT", Float.toString(scc.getSaturation(c)))
                    .replace("`VAL", Float.toString(scc.getValue(c)))
            );
        }
        Gdx.files.local("ColorOutput.txt").writeString(sb.toString(), false);
    }
    public static void tabSplit(String[] receiving, String source) {
        int dl = 1, idx = -1, idx2;
        for (int i = 0; i < 2; i++) {
            receiving[i] = safeSubstring(source, idx+dl, idx = source.indexOf('\t', idx+dl));
        }
        if((idx2 = source.indexOf('\t', idx+dl)) < 0)
        {
            receiving[2] = safeSubstring(source, idx+dl, source.length());
        }
        else
        {
            receiving[2] = safeSubstring(source, idx+dl, idx2);
        }
    }

    @Override
    public void render() {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().update(totalWidth, totalHeight, true);
        stage.getViewport().apply(true);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        totalWidth = width;
        totalHeight = height;
        stage.getViewport().update(width, height, true);
    }
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "SquidLib Demo: Colors";
        config.width = totalWidth;
        config.height = totalHeight;
        config.addIcon("Tentacle-16.png", Files.FileType.Internal);
        config.addIcon("Tentacle-32.png", Files.FileType.Internal);
        config.addIcon("Tentacle-128.png", Files.FileType.Internal);
        new LwjglApplication(new ColorTest(), config);
    }

}