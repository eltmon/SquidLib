package squidpony.squidgrid.mapping.styled;

/**
 * Part of the JSON that defines a tileset.
 * Created by Tommy Ettinger on 3/10/2015.
 */
public class Tile {
    public int a_constraint, b_constraint, c_constraint, d_constraint, e_constraint, f_constraint;
    public String[] data;

    /**
     * Probably not something you will construct manually. See DungeonGen .
     */
    public Tile() {
        a_constraint = 0;
        b_constraint = 0;
        c_constraint = 0;
        d_constraint = 0;
        e_constraint = 0;
        f_constraint = 0;
        data = new String[]{};
    }

    public Tile(int a_constraint, int b_constraint, int c_constraint, int d_constraint, int e_constraint, int f_constraint, String[] data) {
        this.a_constraint = a_constraint;
        this.b_constraint = b_constraint;
        this.c_constraint = c_constraint;
        this.d_constraint = d_constraint;
        this.e_constraint = e_constraint;
        this.f_constraint = f_constraint;
        this.data = data;
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder(512);
        sb.append('"');
        sb.append(data[0]);
        sb.append('"');
        for (int i = 1; i < data.length; i++) {
            sb.append(", \"");
            sb.append(data[i]);
            sb.append('"');
        }
        return "new Tile(" + a_constraint + ", " + b_constraint + ", " + c_constraint + ", " + d_constraint
                + ", " + e_constraint + ", " + f_constraint + ", new String[]{" + sb +  "})";
    }
}