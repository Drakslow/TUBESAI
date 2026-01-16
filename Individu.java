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
    public int[][] kromosom;

    /**
     * Construct individu baru dengan nilai peluang mutasi random
     *
     * @param rand objek random dengan seed
     */
    public Individu(Random rand) {
        this.rand = rand;
        this.kromosom = new int[MosaicGA.baris][MosaicGA.kolom];
        this.fitness = 0.0; // inisialisasi fitness dari 0, karena yang diincar adalah 1.0 (semakin besar
                            // semakin baik, dengan range 0.0 - 1.0)

        for (int y = 0; y < MosaicGA.baris; y++) {
            for (int x = 0; x < MosaicGA.kolom; x++) {

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

    /*
     * Method untuk melakukan uniform crossover dengan variasi pada kotak tidak pasti saja
     *
     * @param other adalah objek Individu yang menyimpan informasi individu (parent) lain
     * @param type adalah tipe crossover, 1 = hanya pada kotak tidak pasti, 2 = pada semua kotak
     *
     * @return 2 anak hasil crossover yang disimpan dalam array of Individu
     */
//    public Individu[] crossoverUniformVariations(Individu other, int type){
//        //inisialisasi anak1 dan anak2 terlebih dahulu
//        Individu anak1 = new Individu(this.rand);
//        Individu anak2 = new Individu(this.rand);
//
//        if(type == 1){ // jika tipe nya adalah 1, maka loop yang di cek hanya kotak tidak pasti saja
//            for (Koordinat k : MosaicGA.daftarKotakTidakPasti) {
//                //ambil baris (y) dan kolom (x) dari cell di kotak tidak pasti nya saja
//                int y = k.getY();
//                int x = k.getX();
//
//                //inisialisasi rnd sebagai random yg akan dipakai saat pemilihan anak (rentang nilai 0.0 sampai 1.0)
//                double rnd = rand.nextDouble();
//
//                if (rnd < 0.5) { //jika rnd kurang dari 0.5, maka anak1 akan diisi oleh kotak parent1 (this), dan anak2 diisi oleh kotak parent2 (other)
//                    anak1.kromosom[y][x] = this.kromosom[y][x];
//                    anak2.kromosom[y][x] = other.kromosom[y][x];
//                } else {//jika rnd lebih besar sama dengan  0.5 (>=0.5), maka anak1 akan diisi oleh kotak parent2 (other), dan anak2 diisi oleh kotak parent1 (this)
//                    anak1.kromosom[y][x] = other.kromosom[y][x];
//                    anak2.kromosom[y][x] = this.kromosom[y][x];
//                }
//            }
//            return new Individu[]{anak1, anak2};//return anak1 dan anak2
//        } else{
//            //ambil nilai kolom dan baris yg dimiliki oleh mosaic puzzle
//            int rows = MosaicGA.baris;
//            int cols = MosaicGA.kolom;
//
//            //loop setiap kotak di mosaic puzzle
//            for(int y = 0; y < rows; y++){
//                for(int x = 0; x < cols; x++){
//                    //inisialisasi rnd sebagai random yg akan dipakai saat pemilihan anak (rentang nilai 0.0 sampai 1.0)
//                    double rnd = rand.nextDouble();
//
//                    if (rnd < 0.5) { //jika rnd kurang dari 0.5, maka anak1 akan diisi oleh kotak parent1 (this), dan anak2 diisi oleh kotak parent2 (other)
//                        anak1.kromosom[y][x] = this.kromosom[y][x];
//                        anak2.kromosom[y][x] = other.kromosom[y][x];
//                    } else {//jika rnd lebih besar sama dengan  0.5 (>=0.5), maka anak1 akan diisi oleh kotak parent2 (other), dan anak2 diisi oleh kotak parent1 (this)
//                        anak1.kromosom[y][x] = other.kromosom[y][x];
//                        anak2.kromosom[y][x] = this.kromosom[y][x];
//                    }
//                }
//            }
//            return new Individu[]{anak1, anak2};
//        }
//    }

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
     * Method utama crossover yang memanggil berbagai jenis crossover.
     * Pilih tipe crossover dengan mengubah nilai crossoverType.
     *
     * @param other objek Individu yang menjadi pasangan crossover
     * @return 2 anak hasil crossover yang disimpan dalam array of Individu
     */
    public Individu[] crossover(Individu other) {
        int crossoverType = 5; // Ubah tipe crossover di sini (1-10)

        switch (crossoverType) {
            case 1:
                return crossoverHorizontal(other);
            case 2:
                return crossoverVertical(other);
            case 3:
                return crossoverBlock(other);
            case 4:
                return crossoverStripe(other);
            case 5:
                return crossoverMultiCross(other);
            case 6:
                return piecesCrossover(other);
            case 7:
                return crossoverDiagonalVariations(other, 1); // One-Point Diagonal
            case 8:
                return crossoverDiagonalVariations(other, 2); // Two-Point Diagonal
            case 9:
                return crossoverDiagonalVariations(other, 3); // Uniform Diagonal
            default:
                return crossoverDiagonalVariations(other, 3); // Default ke Uniform Diagonal
        }
    }


    /**
     * Crossover Block - Membuat persegi panjang random yang akan ditukar dengan individu lain
     *
     * @param other pasangan crossover
     * @return 2 anak hasil crossover
     */
    public Individu[] crossoverBlock(Individu other) {
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        int y1 = rand.nextInt(MosaicGA.baris);
        int y2 = y1 + rand.nextInt(MosaicGA.baris - y1);
        int x1 = rand.nextInt(MosaicGA.kolom);
        int x2 = x1 + rand.nextInt(MosaicGA.kolom - x1);

        for (int y = 0; y < MosaicGA.baris; y++) {
            for (int x = 0; x < MosaicGA.kolom; x++) {
                if (MosaicGA.fixedBoard[y][x] != -1) continue;

                boolean inBlock = (y >= y1 && y <= y2 && x >= x1 && x <= x2);

                if (inBlock) {
                    anak1.kromosom[y][x] = this.kromosom[y][x];
                    anak2.kromosom[y][x] = other.kromosom[y][x];
                } else {
                    anak1.kromosom[y][x] = other.kromosom[y][x];
                    anak2.kromosom[y][x] = this.kromosom[y][x];
                }
            }
        }
        return new Individu[]{anak1, anak2};
    }

    /**
     * Crossover Vertical - Memotong secara vertikal
     *
     * @param other pasangan crossover
     * @return 2 anak hasil crossover
     */
    private Individu[] crossoverVertical(Individu other) {
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        int lokasiCut = rand.nextInt(MosaicGA.kolom);

        for (int y = 0; y < MosaicGA.baris; y++) {
            for (int x = 0; x < MosaicGA.kolom; x++) {
                if (MosaicGA.fixedBoard[y][x] != -1) continue;
                if (x < lokasiCut) {
                    anak1.kromosom[y][x] = this.kromosom[y][x];
                    anak2.kromosom[y][x] = other.kromosom[y][x];
                } else {
                    anak1.kromosom[y][x] = other.kromosom[y][x];
                    anak2.kromosom[y][x] = this.kromosom[y][x];
                }
            }
        }
        return new Individu[]{anak1, anak2};
    }

    /**
     * Crossover Stripe - Pola garis-garis bergantian
     *
     * @param other pasangan crossover
     * @return 2 anak hasil crossover
     */
    private Individu[] crossoverStripe(Individu other) {
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        // Lebar stripe (bisa random 1-2)
        int lebarGaris = rand.nextInt(2) + 1;

        for (int y = 0; y < MosaicGA.baris; y++) {
            for (int x = 0; x < MosaicGA.kolom; x++) {
                boolean ortu1;

                ortu1 = (x / lebarGaris) % 2 == 0;

                if (ortu1) {
                    anak1.kromosom[y][x] = this.kromosom[y][x];
                    anak2.kromosom[y][x] = other.kromosom[y][x];
                } else {
                    anak1.kromosom[y][x] = other.kromosom[y][x];
                    anak2.kromosom[y][x] = this.kromosom[y][x];
                }
            }
        }

        return new Individu[]{anak1, anak2};
    }

    /**
     * Crossover Multi-Cross - Beberapa titik random dengan 4 tetangga (atas, bawah, kiri, kanan)
     * Jumlah cross point: minimal 7 + (baris / 5)
     *
     * @param other pasangan crossover
     * @return 2 anak hasil crossover
     */
    private Individu[] crossoverMultiCross(Individu other) {
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        // Jumlah cross dinamis berdasarkan ukuran board
        int minCrosses = 7 + (MosaicGA.baris / 5);
        int nCross = minCrosses + rand.nextInt(5); // minCrosses sampai minCrosses+4

        int[][] crossPoints = new int[nCross][2]; // [i][0] = y, [i][1] = x

        for (int i = 0; i < nCross; i++) {
            crossPoints[i][0] = rand.nextInt(MosaicGA.baris);
            crossPoints[i][1] = rand.nextInt(MosaicGA.kolom);
        }

        // Fill kromosom
        for (int y = 0; y < MosaicGA.baris; y++) {
            for (int x = 0; x < MosaicGA.kolom; x++) {
                if (MosaicGA.fixedBoard[y][x] != -1) continue;

                boolean isCrossPoint = false;

                // Cek apakah (y,x) adalah salah satu cross point atau tetangganya
                for (int i = 0; i < nCross; i++) {
                    int cy = crossPoints[i][0];
                    int cx = crossPoints[i][1];

                    // Cek apakah ini pusat cross atau salah satu dari 4 tetangga
                    if ((y == cy && x == cx) ||      // Pusat
                            (y == cy && x == cx + 1) ||  // Kanan
                            (y == cy && x == cx - 1) ||  // Kiri
                            (y == cy + 1 && x == cx) ||  // Bawah
                            (y == cy - 1 && x == cx)) {  // Atas

                        isCrossPoint = true;
                        break;
                    }
                }

                // Switch parent jika ketemu cross point
                if (isCrossPoint) {
                    anak1.kromosom[y][x] = other.kromosom[y][x];
                    anak2.kromosom[y][x] = this.kromosom[y][x];
                } else {
                    anak1.kromosom[y][x] = this.kromosom[y][x];
                    anak2.kromosom[y][x] = other.kromosom[y][x];
                }
            }
        }

        return new Individu[]{anak1, anak2};
    }


    private Individu[] crossoverHorizontal(Individu other) {
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        int lokasiCut = rand.nextInt(MosaicGA.baris);

        for (int y = 0; y < MosaicGA.baris; y++) {
            for (int x = 0; x < MosaicGA.kolom; x++) {
                if (MosaicGA.fixedBoard[y][x] != -1) continue;
                if (y < lokasiCut) {
                    anak1.kromosom[y][x] = this.kromosom[y][x];
                    anak2.kromosom[y][x] = other.kromosom[y][x];
                } else {
                    anak1.kromosom[y][x] = other.kromosom[y][x];
                    anak2.kromosom[y][x] = this.kromosom[y][x];
                }
            }
        }
        return new Individu[]{anak1, anak2};
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
        for (int y = 0; y < MosaicGA.baris; y++) {
            for (int x = 0; x < MosaicGA.kolom; x++) {
                if (MosaicGA.fixedBoard[y][x] != -1) continue;
                if (rand.nextDouble() < mutationRate) {
                    //mutasi lgsg flip bitnya
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