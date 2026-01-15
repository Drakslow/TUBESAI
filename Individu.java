import java.util.ArrayList;
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
    public ArrayList<Integer> kromosom;

    /**
     * Construct individu baru dengan nilai peluang mutasi random
     *
     * @param rand objek random dengan seed
     */
    public Individu(Random rand) {
        this.rand = rand;
        this.kromosom = new ArrayList<>();
        this.fitness = 0.0; //inisialisasi fitness dari 0, karena yang diincar adalah 1.0 (semakin besar semakin baik, dengan range 0.0 - 1.0)
    }

    /**
     * Construct individu baru dengan seed random dan ukuran kromosom
     * setiap gen dirandomisasi untuk menentukan isi gen 0 atau 1
     *
     * @param rand objek random dengan seed
     * @param size ukuran kromosom atau banyak gen dari sebuah kromosom
     */
    public Individu(Random rand, int size) {
        this(rand);

        for (int i = 0; i < size; i++) {
            this.kromosom.add(rand.nextBoolean() ? 1 : 0);
        }
    }

    /**
     * Construct individu baru dengan menyalin individu lain
     *
     * @param other objek Individu yang menyimpan informasi individu lain
     */
    public Individu(Individu other) {
        this.rand = other.rand;
        this.fitness = other.fitness;
        this.kromosom = new ArrayList<>(other.kromosom);
    }

    public ArrayList<int[][]> cutBoardInto4Pieces() {

        int kromosomSize = kromosom.length;
        int mid = (kromosomSize + 1) / 2; // untuk ganjil dan genap

        ArrayList<int[][]> result = new ArrayList<>();

        // kiri atas
        result.add(copyPiece(0, mid, 0, mid));

        // kanan atas
        result.add(copyPiece(0, mid, mid, kromosomSize));

        // kiri bawah
        result.add(copyPiece(mid, kromosomSize, 0, mid));

        // kanan bawah
        result.add(copyPiece(mid, kromosomSize, mid, kromosomSize));

        return result;
    }

    private int[][] copyPiece(int rowStart, int rowEnd, int colStart, int colEnd) {

        int[][] piece = new int[rowEnd - rowStart][colEnd - colStart];

        for (int i = rowStart; i < rowEnd; i++) {
            for (int j = colStart; j < colEnd; j++) {
                piece[i - rowStart][j - colStart] = this.kromosom[i][j];
            }
        }

        return piece;
    }

    /**
     * Melakukan crossover dengan 3 tipe, yaitu single-point, two-point, dan uniform crossover
     *
     * @param other objek Individu yang menyimpan informasi individu lain
     * @return 2 anak hasil crossover yang disimpan dalam array of Individu
     */
    public Individu[] crossover(Individu other) {
        //buat 2 children baru
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        int size = this.kromosom.size();
        int crossoverType = 3; //ubah tipe crossover di sini

        // Untuk single dan two point
        //0 sampai first
        //(first+1) sampai second
        int firstCutPoint = rand.nextInt(size-1); // dari 0
        int secondCutPoint = firstCutPoint + rand.nextInt(size - firstCutPoint); // sampai size-1

        //pencegahan salah logic
        if (firstCutPoint == secondCutPoint) {
            secondCutPoint++;
        }

        if (crossoverType == 1) {
            //dalam first point, secondCutPoint menjadi pembaginya
            firstCutPoint = 0;
        }

        if (crossoverType < 3) { //yang bukan uniform
            for (int i = 0; i < size; i++) {
                //dibuat i < second cut karena dalam kasus one dan two point cut, jika second cut berada di size-1, size-1 bisa di crossover
                //sehingga children tidak copy dari orang tua
                if (i >= firstCutPoint && i < secondCutPoint) { //i < second karena menyisakan ujung untuk crossover
                    anak1.kromosom.add(this.kromosom.get(i));
                    anak2.kromosom.add(other.kromosom.get(i));
                } else { //untuk yang di bawah first cut dan di atas atau sama dengan second cut
                    anak1.kromosom.add(other.kromosom.get(i));
                    anak2.kromosom.add(this.kromosom.get(i));
                }
            }
        }
        else { //untuk uniform
            for (int i = 0; i < size; i++) {
                //setiap gen orang tua punya peluang 0.5 untuk ditempati di gen anak 1 atau 2
                if (rand.nextDouble() < 0.5) {
                    anak1.kromosom.add(this.kromosom.get(i));
                    anak2.kromosom.add(other.kromosom.get(i));
                } else {
                    anak1.kromosom.add(other.kromosom.get(i));
                    anak2.kromosom.add(this.kromosom.get(i));
                }
            }
        }

        return new Individu[]{anak1, anak2};
    }

    /**
     * Melakukan mutasi untuk pada individu
     *
     * @param mutationRate peluang mutasi untuk setiap gen
     */
    public void mutation(double mutationRate) {
        //lakukan mutasi di setiap gen
        for (int i = 0; i < this.kromosom.size(); i++) {
            if (rand.nextDouble() < mutationRate) {
                int currentVal = this.kromosom.get(i);
                //mutasi lgsg flip bitnya
                this.kromosom.set(i, currentVal == 1 ? 0 : 1);
            }
        }
    }

    /**
     * Menghitung fitness dari pada individu
     *
     * @return value fitness pada individu
     */
    public double setFitness() {
        //set fitness untuk individu ini
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