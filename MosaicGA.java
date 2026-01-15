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
    private static final Random rnd = new Random(seed);

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
     * list yang menyimpan koordinat kotak belum pasti
     */
    private static ArrayList<Koordinat> daftarKotakTidakPasti;
    
    /**
     * variable untuk menyimpan total kemungkinan maksimal eror yang dapat terjadi
     */
    private static double probMaxError;

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
        
        // ambil kotak yg masih null dari map td dan masukkan ke daftarKotakTidakPasti 
        daftarKotakTidakPasti = new ArrayList<>();
        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                if (fixedBoard[y][x] == -1) {
                    if (disekitarClue(x, y)) {
                        //jika penting, masukin ke kromosom
                        daftarKotakTidakPasti.add(new Koordinat(x, y));
                    }else{
                        // Jika terisolasi (tidak ada guna)
                        fixedBoard[y][x] = 0;
                    }

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
        // File file = new File("Input/10x10Easy_3_283_336.txt");
        File file = new File("Input/10x10Easy_3_283_336.txt");

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
        int maxGenerations = sc.nextInt();// maksimal generasi yang akan dimiliki oleh GA 
        int populasiSize = sc.nextInt(); // banyak individu dalam 1 populasi 
        double crossoverRate = sc.nextDouble();// probabilitas kemungkinan parents melakukan crossover
        double elitismRate = sc.nextDouble();// presentase individu terbaik akan disimpan ke generasi berikutnya
        double mutationRate = sc.nextDouble();//probabilitas gen pada kromosom mengalami mutasi

        //Melakukan preprocessing
        runHeuristics();
        
        // Memulai perhitungan total kemungkinan maksimal eror yang dapat terjadi sekaligus menyimpannya di variabel global 
        probMaxError = (double) hitungMaxError();

        MosaicAlgoGA GA = new MosaicAlgoGA(rnd, populasiSize, maxGenerations, mutationRate, elitismRate, crossoverRate);

        long mulai = System.currentTimeMillis();
        Individu bestSolution = GA.run();
        long akhir = System.currentTimeMillis();

        System.out.println("\n=== Parameters ===");
        System.out.println("MaxGeneration : "+maxGenerations);
        System.out.println("PopulasiSize : "+populasiSize);
        System.out.println("CrossoverRate : "+crossoverRate);
        System.out.println("ElitismRate : "+elitismRate);
        System.out.println("MutationRate : "+mutationRate);
        System.out.println("Seed : "+seed);
        System.out.println("\n=== Waktu Selesai ===");
        System.out.println("Time : "+(akhir-mulai)/1000+"(s)");
        System.out.println("\n=== Best Solution Found ===");
        System.out.printf("Final Fitness: %.5f\n", bestSolution.getFitness());

        printBestSolution(bestSolution.kromosom);


    }

    /**
     * Print jumlah hitam yang sebenarnya dan jumlah hitam yang dikerjakan oleh genetic algorithm
     *
     * @param finalBoard board yang telah dipecahkan oleh genetic algorithm
     */
    private static void printYgMasihError(int [][]finalBoard){
        System.out.println("\n=== Yang Masih Error ===");
        int cnt = 1;
        for (int y = 0; y < kolom; y++) {
            for (int x = 0; x < kolom; x++) {
                if (map[y][x]>-1) {
                    int countBlack = hitungHitam(finalBoard, x, y);
                    int clue = map[y][x];
                    if(clue!=countBlack){
                        System.out.printf("%2d. pos(%2d,%2d) | Nemu = %d, Harusnya = %d\n", cnt++, x+1, y+1,countBlack,clue);
                    }
                }
            }
        }
    }

    /**
     * Print board solusi.
     * Menggabunglan daftar kotak tidak pasti (kromosom) dengan final board
     *
     * @param finalBoard menyimpan daftar kotak yang tidak pasti
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

    /**
     * Mengambil ukuran kromosom (banyak kotak yang tidak pasti)
     *
     * @return jumlah kotak yang tidak pasti
     */
    public static int getChromosomeSize() {
        return daftarKotakTidakPasti.size();
    }
}