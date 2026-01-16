import java.io.*;
import java.util.*;

/**
 * Main Class.
 * Input board dan parameter untuk genetic algorithm.
 * Melakukan preprocessing pada input sebelum diproses oleh algoritma genetika
 *
 * @author Owen Lianggara
 * @author Andrew Kevin Alexander
 */
public class MosaicGA {
    /**
     * seed untuk random yang akan menjadi parameter constructor di beberapa kelas
     */
    private static int seed = 42;

    /**
     * random yang akan menjadi parameter constructor di beberapa kelas
     */
    private static Random rnd = new Random(seed);

    /**
     * ukuran baris dan kolom pada board
     */
    protected static int baris, kolom;

    /**
     * Menyimpan input berupa clue
     */
    private static int[][] map;

    /**
     * board untuk simpan jawaban yg sudah pasti (0 = putih, 1=hitam, null = tidak diketahui)
     */
    protected static int[][] fixedBoard;


    /**
     * variable untuk menyimpan total kemungkinan maksimal eror yang dapat terjadi
     */
    private static double probMaxError;

    static double generasiBestF[][] = new double[20][5000];
    static double minimumFitness = Double.MAX_VALUE;
    static double waktuPerInput[] = new double[20];
    static double generasiPerInput[] = new double[20];
    static double bestFPerInput[] = new double[20];
    static int counterInput = 0;

    /**
     * logika heuristik awal untuk mengisi fixedBoard dan daftarKotakTidakPasti dengan menerapkan trik bermain
     */
    private static void runHeuristics() { //method untuk menerapkan aturan trik permainan
        //TODO: kenapa harus pakai while? cukup sekali loop tidak bisa?

        boolean berubah = true;// flag untuk mengecek apakah ada perubahan pada iterasi terakhir
        while(berubah) { //loop sampai tidak ada perubahan lagi
            berubah = false;

            //loop setiap kotak di papan, dan jika ada menyinggung aturan permainan, maka tetangga2nya di set sesuai aturan
            for (int y = 0; y < baris; y++) {
                for (int x = 0; x < kolom; x++) {
                    //ambil nilai kotak saat ini
                    int valueKotak = map[y][x];
                    if (valueKotak == -1) {//jika kotak ber angka -1 lanjut aja
                        continue;
                    }else if (valueKotak == 0) {//jika kotak ber angka 0, maka semua tetangga dan diriny sendiri putih (0)
                        if (setNeighbors(x, y, 0)) {
                            berubah = true;
                        }
                    }
                    else if (valueKotak == 9) {//jika kotak ber angka 9, maka semua tetangga dan diriny sendiri hitam (1)
                        if (setNeighbors(x, y, 1)) {
                            berubah = true;
                        }
                    }
                    else if (valueKotak == 4 && isCorner(x, y)) {//jika kotak ber angka 4 dan di sudut, maka semua tetangga dan diriny sendiri hitam (1)
                        if (setNeighbors(x, y, 1)) {
                            berubah = true;
                        }
                    }
                    else if (valueKotak == 6 && isEdge(x, y)) {//jika kotak ber angka 6 dan di bagian sisi, maka semua tetangga dan diriny sendiri hitam (1)
                        if (setNeighbors(x, y, 1)) {
                            berubah = true;
                        }
                    }
                }
            }
        }

        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                if (fixedBoard[y][x] == -1 && !disekitarClue(x, y)) {
                    fixedBoard[y][x] = 0; // terisolasi berarti putih
                }
            }
        }
    }

    /**
     * Memeriksa apakah sebuah kotak merupakan kotak yang sudah pasti atau bukan
     *
     * @param cx center x, koordinat x sebuah kotak
     * @param cy center y, koordinat y sebuah kotak
     * @return ada clue di tetangga: True, tidak ada clue di tetangga: False
     */
    //TODO: kotak sendiri kenapa diperiksa juga?
    private static boolean disekitarClue(int cx, int cy) {
        // Loop radius 3x3
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nx = cx + dx;
                int ny = cy + dy;

                // Cek batas array agar tidak error
                if (nx >= 0 && nx < kolom && ny >= 0 && ny < baris) {
                    // Jika tetangganya adalah ANGKA (bukan -1/kosong),
                    // brarti cell ini kemasuk g pasti
                    if (map[ny][nx] != -1) {
                        return true;
                    }
                }
            }
        }
        return false; // Tidak ada clue sama sekali di sekitar sel ini (Terisolasi)
    }

    /**
     * Memeriksa apakah koordinat kotak tersebut berada di sisi board
     *
     * @param x koordinat x sebuah kotak
     * @param y koordinat y sebuah kotak
     * @return berada di sisi: True, tidak berada di sisi: False
     */
    private static boolean isCorner(int x, int y) {//method untuk cek apakah koordinat (x,y) di bagian sudut
        if((x==0 || x==kolom-1) && (y==0 || y==baris-1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Memeriksa apakah koordinat kotak tersebut berada di sudut board
     *
     * @param x koordinat x sebuah kotak
     * @param y koordinat y sebuah kotak
     * @return berada di sudut: True, tidak berada di sudut: False
     */
    private static boolean isEdge(int x, int y) {//method untuk cek apakah koordinat (x,y) di bagian sisi
        if( (!isCorner(x, y) ) && ( (x==0 || x==kolom-1) || (y==0 || y==baris-1) ) ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Memasukkan hitam/putih ke dalam sebuah kotak yang sudah pasti
     *
     * @param posisiX koordinat x sebuah kotak
     * @param posisiY koordinat y sebuah kotak
     * @param warna warna pada kotak, 0 (putih) atau 1 (hitam)
     * @return menambahkan warna di tetangg: True, tidak menambahkan warna di tetangg: False
     */
    private static boolean setNeighbors(int posisiX, int posisiY, int warna) {
        boolean perubahan = false;
        //loop tetangga termasuk dirinya sendiri (3x3)
        for (int cekY = -1; cekY <= 1; cekY++) {
            for (int cekX = -1; cekX <= 1; cekX++) {
                //nilai dari koordinat tetangga + dirinya sendiri
                int nilaiX = posisiX + cekX;
                int nilaiY = posisiY + cekY;
                if (nilaiX >= 0 && nilaiX < kolom && nilaiY >= 0 && nilaiY < baris) {//cek apabila masih masuk ke dalam peta
                    if (fixedBoard[nilaiY][nilaiX] == -1) {//jika kotak belum di set (masih null), maka set warna nya
                        fixedBoard[nilaiY][nilaiX] = warna;
                        perubahan = true;
                    } 
                }
            }
        }
        return perubahan;
    }

    /**
     * Menghitung total kemungkinan maksimal eror yang dapat terjadi
     */
    public static int hitungMaxError() {
        int totalMax = 0;

        // Loop sebanyak baris dan kolom yang dimiliki peta (setiap cell diperiksa)
        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                if (map[y][x] != -1) { // Pengecekan hanya dilakukan untuk koordinat/cell yang ada clue (angka selain -1)

                    // Cek apakah clue berada di corner (Pojok)
                    if (isCorner(x, y)) {
                        totalMax += 4;
                    }
                    // Cek apakah clue berada di edge (Tepi, tapi bukan pojok)
                    else if (isEdge(x, y)) {
                        totalMax += 6;
                    }
                    // Sisanya berada di middle (Tengah)
                    else {
                        totalMax += 9;
                    }
                }
            }
        }
        return totalMax;
    }

    /**
     * Menghitung sebuah fitness kromosom tanpa melibatkan objek Individu
     *
     * @param chromosome menyimpan daftar kotak tidak pasti berupa koordinat
     * @return fitness dari sebuah kromosom
     */
    static double calcFitness(int[][] chromosome) {

        //inisialisasi total error yg ada saat ini dengan 0
        int totalError = 0;

        //loop untuk menghitung fitness nya
        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                int clue = map[y][x]; //ambil value dr kotak nya
                if (clue == -1) continue; //kalo isinya -1 lanjut aja (di skip)
                
                int currentBlacks = hitungHitam(chromosome, x, y); //ambil banyak hitam dari kotak clue sekarang
                totalError += Math.abs(currentBlacks - clue); //cek berapa banyak selisih antara hitam sekarang dan clue
            }
        }
        //hitung fitness berdasarkan total error (semakin besar fitnessnya semakin baik)
        double fitness = (probMaxError - (double)totalError + 1) / (probMaxError+1);

        // Jaga jaga agar tidak return hasil negatif
        return Math.max(0.0, fitness);
    }

    /**
     * Menghitung jumlah kotak berwarna hitam di tetangga dan diri sendiri
     *
     * @param board board yang menyimpan 0, 1, atau null
     * @param posisiX koordinat x sebuah kotak
     * @param posisiY koordinat y sebuah kotak
     * @return jumlah hitam pada tetangga kotak dan diri sendiri
     */
    private static int hitungHitam(int[][] board, int posisiX, int posisiY){
        int count = 0;
        //loop tetangga termasuk dirinya sendiri (3x3)
        for (int cekY = -1; cekY <= 1; cekY++) {
            for (int cekX = -1; cekX <= 1; cekX++) {
                //nilai dari koordinat tetangga + dirinya sendiri
                int nilaiX = posisiX + cekX;
                int nilaiY = posisiY + cekY;
                if (nilaiX >= 0 && nilaiX < kolom && nilaiY >= 0 && nilaiY < baris) {//cek apabila masih masuk ke dalam peta
                    if (board[nilaiY][nilaiX] == 1) {//jika kotak bernilai 1 (warna hitam), maka count akan bertambah
                        count++;
                    } 
                }
            }
        }
        return count;
    }

    /**
     * Main method untuk menjalankan preprocessing dan  program genetic algorithm
     *
     * @param args
     * @throws FileNotFoundException throw file input yang tidak ditemukan
     */
    public static void main(String[] args) throws FileNotFoundException {
        for (int input = 1; input < 21 ; input++) {
            PrintStream out = new PrintStream(new File(String.format("./Input/10x10/OutputTest%d.txt",input)));
            System.setOut(out);
            rnd = new Random(seed);
            File file = new File(String.format("./Input/10x10/input%d.txt",input));
            Scanner sc = new Scanner(file);

            baris = sc.nextInt();
            kolom = sc.nextInt();
            map = new int[baris][kolom];
            fixedBoard = new int[baris][kolom];
            for (int y = 0; y < baris; y++) {
                Arrays.fill(fixedBoard[y], -1);
            }
            

            for (int i = 0; i < baris; i++) {
                for (int j = 0; j < kolom; j++) {
                    map[i][j] = sc.nextInt();
                }
            }

            //parameter (baca dari file juga)
//            int maxGenerations = sc.nextInt();// maksimal generasi yang akan dimiliki oleh GA
//            int populasiSize = sc.nextInt(); // banyak individu dalam 1 populasi
//            double crossoverRate = sc.nextDouble();// probabilitas kemungkinan parents melakukan crossover
//            double elitismRate = sc.nextDouble();// presentase individu terbaik akan disimpan ke generasi berikutnya
//            double mutationRate = sc.nextDouble();//probabilitas gen pada kromosom mengalami mutasi

            int maxGenerations = 5000;// maksimal generasi yang akan dimiliki oleh GA
            int populasiSize = 500; // banyak individu dalam 1 populasi
            double crossoverRate = 0.8;// probabilitas kemungkinan parents melakukan crossover
            double elitismRate = 0.1;// presentase individu terbaik akan disimpan ke generasi berikutnya
            double mutationRate = 0.015;//probabilitas gen pada kromosom mengalami mutasi

            //Melakukan preprocessing
            runHeuristics();

            // Memulai perhitungan total kemungkinan maksimal eror yang dapat terjadi sekaligus menyimpannya di variabel global
            probMaxError = (double) hitungMaxError();

            MosaicAlgoGA GA = new MosaicAlgoGA(rnd, populasiSize, maxGenerations, mutationRate, elitismRate, crossoverRate);

            long mulai = System.currentTimeMillis();
            Individu bestSolution = GA.run();
            long akhir = System.currentTimeMillis();

            //Simpan Eksperimen
            waktuPerInput[input-1]=(akhir-mulai)/1000.0;
            bestFPerInput[input-1]=bestSolution.getFitness();
            minimumFitness=Math.min(minimumFitness,bestSolution.getFitness());
            System.out.println("\n=== Parameters ===");
            System.out.println("MaxGeneration : "+maxGenerations);
            System.out.println("PopulasiSize : "+populasiSize);
            System.out.println("CrossoverRate : "+crossoverRate);
            System.out.println("ElitismRate : "+elitismRate);
            System.out.println("MutationRate : "+mutationRate);
            System.out.println("Seed : "+seed);
            System.out.println("\n=== Waktu Selesai ===");
            System.out.println("Time : "+(akhir-mulai)/1000.0+"(s)");
            System.out.println("\n=== Best Solution Found ===");
            System.out.printf("Final Fitness: %.5f\n", bestSolution.getFitness());

            printBestSolution(bestSolution.kromosom);

            counterInput++;
        }
        printRataRataEksperimen();
    }
    static class BestFEntry {
        int inputIndex;   // indeks asli (0-based)
        double value;

        BestFEntry(int inputIndex, double value) {
            this.inputIndex = inputIndex;
            this.value = value;
        }
    }

    private static void printRataRataEksperimen() throws FileNotFoundException {
        PrintStream out = new PrintStream(
                new File("./Input/10x10/EksperimenRata_Rata.txt")
        );
        System.setOut(out);

        System.out.println("========================================");
        System.out.println("        HASIL RATA-RATA EKSPERIMEN       ");
        System.out.println("========================================\n");

        // ================= HARMONIC MEAN =================
        System.out.println("==== Harmonic Mean Waktu Per Input ====");
        System.out.println(harmonicMean(waktuPerInput));
        System.out.println();

        System.out.println("==== Harmonic Mean Generasi Per Input ====");
        System.out.println(harmonicMean(generasiPerInput));
        System.out.println();

        System.out.println();
        System.out.println("==== Min Generation per Input (Ranking) ====");

// ubah array jadi list of pair
        List<BestFEntry> genList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            genList.add(new BestFEntry(i, generasiPerInput[i]));
        }

// sort ASCENDING (paling kecil = paling cepat)
        genList.sort((a, b) -> Double.compare(a.value, b.value));

        for (int rank = 0; rank < genList.size(); rank++) {
            BestFEntry e = genList.get(rank);
            System.out.printf(
                    "%2d. Input %2d (index %2d) = %.0f generasi%n",
                    rank + 1,
                    e.inputIndex + 1,   // input 1-based
                    e.inputIndex,       // indeks asli
                    e.value
            );
        }

        System.out.println();
        System.out.println("==== Harmonic Mean Best Fitness Per Input ====");
        System.out.println(harmonicMean(bestFPerInput));
        System.out.println();
        System.out.println("Best F per Input (Ranking):");

// ubah array jadi list of pair
        List<BestFEntry> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new BestFEntry(i, bestFPerInput[i]));
        }

        list.sort((a, b) -> Double.compare(b.value, a.value));

        for (int rank = 0; rank < list.size(); rank++) {
            BestFEntry e = list.get(rank);
            System.out.printf(
                    "%2d. Input %2d (index %2d) = %.5f%n",
                    rank + 1,
                    e.inputIndex + 1,   // input 1-based
                    e.inputIndex,       // indeks asli
                    e.value
            );
        }
        System.out.println();

        System.out.println("==== Harmonic Mean Generasi Best Fitness ====");
        harmonicMean2D(generasiBestF);
        System.out.println();

        out.close();
    }

    private static void harmonicMean2D(double[][] data) {
        int jumlahInput = data.length;        // 20
        int jumlahGenerasi = data[0].length;  // 5001

        for (int gen = 0; gen < jumlahGenerasi; gen++) {
            double sumPenyebut = 0.0;

            for (int input = 0; input < jumlahInput; input++) {
                sumPenyebut += 1.0 / data[input][gen];
            }

            double hm = jumlahInput / sumPenyebut;

            System.out.printf("Generasi %4d : %.6f\n", gen+1, hm);
        }
    }





    private static double harmonicMean(double[] data) {
        double sumPenyebut = 0.0;
        int n = data.length;

        for (double x : data) {
            sumPenyebut += 1.0 / x;
        }

        return n / sumPenyebut;
    }

    /**
     * Print jumlah hitam yang sebenarnya dan jumlah hitam yang dikerjakan oleh genetic algorithm
     *
     * @param finalBoard board yang telah dipecahkan oleh genetic algorithm
     */
    private static void printYgMasihError(int[][] finalBoard) {
        System.out.println("\n=== Yang Masih Error ===");
        int cnt = 1;
        for (int y = 0; y < kolom; y++) {
            for (int x = 0; x < kolom; x++) {
                if (map[y][x] > -1) {
                    int countBlack = hitungHitam(finalBoard, x, y);
                    int clue = map[y][x];
                    if (clue != countBlack) {
                        System.out.printf("%2d. pos(%2d,%2d) | Nemu = %d, Harusnya = %d\n", cnt++, x + 1, y + 1, countBlack, clue);
                    }
                }
            }
        }
    }

    /**
     * Print board solusi best terakhir
     *
     * @param finalBoard menyimpan daftar kotak yang final
     */
    private static void printBestSolution(int[][] finalBoard) {
        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                System.out.print(finalBoard[y][x] == 1 ? "# " : ". ");
            }
            System.out.println();
        }

        printYgMasihError(finalBoard);
    }

}