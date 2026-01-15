import java.util.Random;

/**
 * Menyimpan informasi sebuah kromosom.
 * Informasi tersebut berupa kromosom, fitness, dan nilai mutasi
 *
 * @author Owen Lianggara
 * @author Andrew Kevin Alexander
 */
public class Individu implements Comparable<Individu> {
    /**
     * Menyimpan fitness sebuah individu
     */
    public double fitness;

    /**
     * Random menyimpan seed
     * Random untuk membuat kromosom awal, crossover, dan mutasi
     */
    public Random rand;

    /**
     * Simpan gen di dalam kromosom.
     * Kromosom: 0 = Putih, 1 = Hitam
     */
    public int[][] kromosom;

    /**
     * Construct individu baru dengan nilai peluang mutasi random
     *
     * @param rand objek random dengan seed
     */
    public Individu(Random rand) {
        this.rand = rand;
        this.kromosom = new int[baris][kolom];
        this.fitness = 0.0; // inisialisasi fitness dari 0, karena yang diincar adalah 1.0 (semakin besar
                            // semakin baik, dengan range 0.0 - 1.0)

        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {

                if (MosaicGA.fixedBoard[y][x] == -1) {
                    kromosom[y][x] = rand.nextBoolean() ? 1 : 0;
                } else {
                    kromosom[y][x] = MosaicGA.fixedBoard[y][x];
                }

            }
        }
    }

    /**
     * Construct individu baru dengan seed random dan ukuran kromosom
     * setiap gen dirandomisasi untuk menentukan isi gen 0 atau 1
     *
     * @param rand objek random dengan seed
     */

    /**
     * Construct individu baru dengan menyalin individu lain
     *
     * @param other objek Individu yang menyimpan informasi individu lain
     */
    public Individu(Individu other) {
        this.rand = other.rand;
        this.fitness = other.fitness;
        this.kromosom = new int[MosaicGA.baris][MosaicGA.kolom];

        for (int i = 0; i < MosaicGA.baris; i++) {
            System.arraycopy(other.kromosom[i], 0, this.kromosom[i], 0, MosaicGA.kolom);
        }
    }

    /**
     * Melakukan crossover dengan 3 tipe, yaitu single-point, two-point, dan uniform
     * crossover
     *
     * @param other objek Individu yang menyimpan informasi individu lain
     * @return 2 anak hasil crossover yang disimpan dalam array of Individu
     */
    public Individu[] crossover(Individu other) {
        // buat 2 children baru
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        int size = this.kromosom.size();
        int crossoverType = 3; // ubah tipe crossover di sini

        // Untuk single dan two point
        // 0 sampai first
        // (first+1) sampai second
        int firstCutPoint = rand.nextInt(size - 1); // dari 0
        int secondCutPoint = firstCutPoint + rand.nextInt(size - firstCutPoint); // sampai size-1

        // pencegahan salah logic
        if (firstCutPoint == secondCutPoint) {
            secondCutPoint++;
        }

        if (crossoverType == 1) {
            // dalam first point, secondCutPoint menjadi pembaginya
            firstCutPoint = 0;
        }

        if (crossoverType < 3) { // yang bukan uniform
            for (int i = 0; i < size; i++) {
                // dibuat i < second cut karena dalam kasus one dan two point cut, jika second
                // cut berada di size-1, size-1 bisa di crossover
                // sehingga children tidak copy dari orang tua
                if (i >= firstCutPoint && i < secondCutPoint) { // i < second karena menyisakan ujung untuk crossover
                    anak1.kromosom.add(this.kromosom.get(i));
                    anak2.kromosom.add(other.kromosom.get(i));
                } else { // untuk yang di bawah first cut dan di atas atau sama dengan second cut
                    anak1.kromosom.add(other.kromosom.get(i));
                    anak2.kromosom.add(this.kromosom.get(i));
                }
            }
        } else { // untuk uniform
            for (int i = 0; i < size; i++) {
                // setiap gen orang tua punya peluang 0.5 untuk ditempati di gen anak 1 atau 2
                if (rand.nextDouble() < 0.5) {
                    anak1.kromosom.add(this.kromosom.get(i));
                    anak2.kromosom.add(other.kromosom.get(i));
                } else {
                    anak1.kromosom.add(other.kromosom.get(i));
                    anak2.kromosom.add(this.kromosom.get(i));
                }
            }
        }

        return new Individu[] { anak1, anak2 };
    }

    /**
     * Melakukan Crossover Diagonal dengan Variasi.
     * * @param other Pasangan (Induk B)
     * 
     * @param type 1 = One-Point (Belah Miring),
     *             2 = Two-Point (Pita Miring),
     *             3 = Uniform (Acak per Garis Miring)
     * @return Array 2 anak hasil crossover
     */
    public Individu[] crossoverDiagonalVariations(Individu other, int type) {
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        int rows = MosaicGA.baris;
        int cols = MosaicGA.kolom;

        // Rentang nilai diagonal (y - x) adalah dari -(cols-1) sampai +(rows-1)
        int minDiag = -(cols - 1);
        int maxDiag = (rows - 1);
        int totalDiagonals = maxDiag - minDiag + 1;

        // --- PERSIAPAN LOGIKA POTONG ---
        int cut1 = 0;
        int cut2 = 0;
        boolean[] uniformMap = null;

        if (type == 1) {
            // ONE POINT: Pilih satu garis potong diagonal secara acak
            // range random diantara minDiag dan maxDiag
            cut1 = rand.nextInt(totalDiagonals) + minDiag;
        } else if (type == 2) {
            // TWO POINT: Pilih dua garis potong untuk membuat "Pita"
            int r1 = 0;
            int r2 = 0;
            while (r1 == r2) {
                r1 = rand.nextInt(totalDiagonals) + minDiag;
                r2 = rand.nextInt(totalDiagonals) + minDiag;
            }
            cut1 = Math.min(r1, r2);
            cut2 = Math.max(r1, r2);
        } else if (type == 3) {
            // UNIFORM: Setiap garis diagonal punya peluang 50% untuk ditukar
            uniformMap = new boolean[totalDiagonals];
            for (int i = 0; i < totalDiagonals; i++) {
                uniformMap[i] = rand.nextBoolean();
            }
        }

        // --- PROSES LOOPING MATRIKS ---
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                // Hitung ID Diagonal sel saat ini
                // Nilai (y - x) menentukan posisi sel relatif terhadap diagonal utama
                int diagVal = y - x;

                boolean swap = false;

                // Tentukan apakah sel ini harus di-swap (tukar) berdasarkan tipe
                switch (type) {
                    case 1: // One Point
                        // Jika diagonal sel lebih besar dari titik potong (Area Bawah)
                        if (diagVal >= cut1)
                            swap = true;
                        break;

                    case 2: // Two Point
                        // Jika sel berada DI ANTARA dua garis potong (Di dalam Pita)
                        if (diagVal >= cut1 && diagVal <= cut2)
                            swap = true;
                        break;

                    case 3: // Uniform (Per Garis)
                        // Konversi diagVal ke index array (0 sampai totalDiagonals-1)
                        int mapIndex = diagVal - minDiag;
                        if (uniformMap[mapIndex])
                            swap = true;
                        break;
                }

                // --- EKSEKUSI PENUKARAN GEN ---
                if (swap) {
                    // Jika kondisi terpenuhi: Anak 1 ambil B, Anak 2 ambil A
                    anak1.kromosom[y][x] = other.kromosom[y][x];
                    anak2.kromosom[y][x] = this.kromosom[y][x];
                } else {
                    // Jika tidak: Anak 1 ambil A, Anak 2 ambil B
                    anak1.kromosom[y][x] = this.kromosom[y][x];
                    anak2.kromosom[y][x] = other.kromosom[y][x];
                }
            }
        }

        return new Individu[] { anak1, anak2 };
    }

    /**
     * Melakukan mutasi untuk pada individu
     *
     * @param mutationRate peluang mutasi untuk setiap gen
     */
    public void mutation(double mutationRate) {
        // lakukan mutasi di setiap gen
        for (int y = 0; y < MosaicGA.baris; y++) {
            for (int x = 0; x < MosaicGA.kolom; x++) {
                if (MosaicGA.fixedBoard[y][x] != -1)
                    continue;
                if (rand.nextDouble() < mutationRate) {
                    // mutasi lgsg flip bitnya
                    kromosom[y][x] = kromosom[y][x] == 1 ? 0 : 1;
                }
            }
        }
    }

    /**
     * Menghitung fitness dari pada individu
     *
     * @return value fitness pada individu
     */
    public double setFitness() {
        // set fitness untuk individu ini
        this.fitness = MosaicGA.calcFitness(this.kromosom);
        return this.fitness;
    }

    /**
     * Return fitness dari pada individu
     *
     * @return value fitness pada individu
     */
    public double getFitness() {
        return this.fitness;
    }

    /**
     *
     * @param other object Individu yang ingin di compare.
     * @return hasil integer dari komparasi untuk mengurutkan dari besar ke kecil
     */
    @Override
    public int compareTo(Individu other) {
        return Double.compare(other.fitness, this.fitness);
    }
}