/**
 * Menyimpan koordinat x dan y, di mana x adalah lokasi kolom dan y adalah lokasi baris.
 * Kelas ini digunakan untuk menyimpan posisi kotak yang tidak pasti
 * di dalam variabel daftarKotakTidakPasti.
 *
 * @author Owen Lianggara
 * @author Andrew Kevin Alexander
 */
public class Koordinat {

    /**
     * Koordinat x.
     */
    private int x;

    /**
     * Koordinat y.
     */
    private int y;

    /**
     * Construct objek Koordinat dengan x dan y yang spesifik.
     *
     * @param x koordinat x
     * @param y koordinat y
     */
    public Koordinat(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns koordinat value x.
     *
     * @return koordinat value x
     */
    public int getX() {
        return x;
    }

    /**
     * Set koordinat value x.
     *
     * @param x koordinat x yang baru
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Return koordinat value y.
     *
     * @return koordinat value y
     */
    public int getY() {
        return y;
    }

    /**
     * Set koordinat value y.
     *
     * @param y koordinat y yang baru
     */
    public void setY(int y) {
        this.y = y;
    }
}