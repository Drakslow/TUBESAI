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
        result.add(copyPiece(0, 0, mid));

        // kanan atas
        result.add(copyPiece(0, kromosomSize - mid, mid));

        // kiri bawah
        result.add(copyPiece(kromosomSize - mid, 0, mid));

        // kanan bawah
        result.add(copyPiece(kromosomSize - mid, kromosomSize - mid, mid));

        return result;
    }

    private int[][] copyPiece(int rowStart, int colStart, int size) {
        int[][] piece = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int r = rowStart + i;
                int c = colStart + j;

                if (r >= 0 && r < kromosom.length &&
                        c >= 0 && c < kromosom.length) {
                    piece[i][j] = kromosom[r][c];
                }
                else {
                    piece[i][j] = 0; // padding
                }
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
    public Individu[] piecesCrossover(Individu other) {

        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        // potong kromosom jadi 4 pieces
        ArrayList<int[][]> piecesA = this.cutBoardInto4Pieces();
        ArrayList<int[][]> piecesB = other.cutBoardInto4Pieces();

        int size = piecesA.size(); // harus 4
        int cutPoint = rand.nextInt(size - 1) + 1; // bebas: 1..3

        ArrayList<int[][]> childPieces1 = new ArrayList<>();
        ArrayList<int[][]> childPieces2 = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (i < cutPoint) {
                childPieces1.add(copyPieces(piecesA.get(i)));
                childPieces2.add(copyPieces(piecesB.get(i)));
            } else {
                childPieces1.add(copyPieces(piecesB.get(i)));
                childPieces2.add(copyPieces(piecesA.get(i)));
            }
        }

        // gabungkan kembali jadi kromosom utuh
        anak1.kromosom = mergePieces(childPieces1);
        anak2.kromosom = mergePieces(childPieces2);

        return new Individu[]{anak1, anak2};
    }

    private int[][] copyPieces(int[][] piece) {
        int[][] copy = new int[piece.length][piece[0].length];
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[0].length; j++) {
                copy[i][j] = piece[i][j];
            }
        }
        return copy;
    }

    private int[][] mergePieces(ArrayList<int[][]> pieces) {
        int[][] topLeft = pieces.get(0);
        int[][] topRight = pieces.get(1);
        int[][] bottomLeft = pieces.get(2);
        int[][] bottomRight = pieces.get(3);

        int pieceLength = topLeft.length;
        int[][] newKromosom = new int[kromosom.length][kromosom.length];

        paste(newKromosom, topLeft, 0, 0);
        paste(newKromosom, topRight, 0, pieceLength);
        paste(newKromosom, bottomLeft, pieceLength, 0);
        paste(newKromosom, bottomRight, pieceLength, pieceLength);

        return newKromosom;
    }

    private void paste(int[][] newKromosom, int[][] piece, int rowStart, int colStart) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[0].length; j++) {
                newKromosom[rowStart + i][colStart + j] = piece[i][j];
            }
        }
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