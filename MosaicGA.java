import java.io.*;
import java.util.*;

public class MosaicGA {
    // GA SPEC
    protected static int seed = 42;
    protected static final Random rnd = new Random(seed);

    // BOARD SPEC
    protected static int baris, kolom;
    protected static int[][] map;

    protected static Integer[][] fixedBoard; //board untuk simpan jawaban yg sudah pasti (0 = putih, 1=hitam, null = belum tau)
    protected static ArrayList<Koordinat> daftarKotakTidakPasti;//list yang menyimpan koordinat kotak belum pasti
    
    // logika heuristik awal untuk mengisi fixedBoard dan daftarKotakTidakPasti
    private static void runHeuristics() {
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
                if (fixedBoard[y][x] == null) {
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

    private static boolean isCorner(int x, int y) {//method untuk cek apakah koordinat (x,y) di bagian sudut
        if((x==0 || x==kolom-1) && (y==0 || y==baris-1)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isEdge(int x, int y) {//method untuk cek apakah koordinat (x,y) di bagian sisi
        if( (!isCorner(x, y) ) && ( (x==0 || x==kolom-1) || (y==0 || y==baris-1) ) ) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean setNeighbors(int posisiX, int posisiY, int warna) {
        boolean perubahan = false;
        //loop tetangga termasuk dirinya sendiri (3x3)
        for (int cekY = -1; cekY <= 1; cekY++) {
            for (int cekX = -1; cekX <= 1; cekX++) {
                //nilai dari koordinat tetangga + dirinya sendiri
                int nilaiX = posisiX + cekX;
                int nilaiY = posisiY + cekY;
                if (nilaiX >= 0 && nilaiX < kolom && nilaiY >= 0 && nilaiY < baris) {//cek apabila masih masuk ke dalam peta
                    if (fixedBoard[nilaiY][nilaiX] == null) {//jika kotak belum di set (masih null), maka set warna nya
                        fixedBoard[nilaiY][nilaiX] = warna;
                        perubahan = true;
                    } 
                }
            }
        }
        return perubahan;
    }

    static double calcFitness(ArrayList<Integer> chromosome) {
        int[][] currentBoard = new int[baris][kolom];
        
        //ambil kotak yang sudah pasti
        for(int y=0; y<baris; y++){
            for(int x=0; x<kolom; x++){
                if(fixedBoard[y][x] != null) {
                    currentBoard[y][x] = fixedBoard[y][x];
                }
            }
        }

        //ambil kotak yg blm pasti
        if (chromosome != null && !chromosome.isEmpty()) {
            for (int i = 0; i < chromosome.size(); i++) {
                Koordinat koorSekarang = daftarKotakTidakPasti.get(i);
                currentBoard[koorSekarang.getY()][koorSekarang.getX()] = chromosome.get(i);
            }
        }

        //hitung banyak eror nya (kotak hitam tidak sesuai clue)
        int totalError = 0;
        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                int clue = map[y][x]; //ambil value dr kotak nya
                if (clue==-1) continue;//kalo isinya -1 lanjut aja (di skip)
                int currentBlacks = hitungHitam(currentBoard, x, y); //ambil banyak hitam dari kotak clue sekarang
                totalError += Math.abs(currentBlacks - clue); //cek berapa banyak selisih antara hitam sekarang dan clue
            }
        }
        return (double) totalError;
    }


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

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("input.txt");
        Scanner sc = new Scanner(file);

        baris = sc.nextInt();
        kolom = sc.nextInt();
        map = new int[baris][kolom];
        fixedBoard = new Integer[baris][kolom];

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

        runHeuristics();
        
        MosaicAlgoGA GA = new MosaicAlgoGA(rnd, populasiSize, maxGenerations, mutationRate, elitismRate, crossoverRate);
        Individu bestSolution = GA.run();

        System.out.println("\n=== Best Solution Found ===");
        System.out.printf("Final Fitness: %.0f\n", bestSolution.getFitness());

        printBestSolution(bestSolution.kromosom);


    }

    private static void printYgMasihError(int [][]finalBoard){
        System.out.println("\n=== Yang Masih Error ===");
        int cnt = 1;
        for (int y = 0; y < kolom; y++) {
            for (int x = 0; x < kolom; x++) {
                if (map[y][x]>-1) {
                    int countBlack = hitungHitam(finalBoard, x, y);
                    int clue = map[y][x];
                    if(clue!=countBlack){
                        System.out.printf("%d. pos(%d,%d), Nemu = %d, Harusnya = %d\n", cnt++, x, y,countBlack,clue);
                    }
                }
            }
        }
    }
    private static void printBestSolution(ArrayList<Integer> chromosome) {
        int[][] finalBoard = new int[baris][kolom];
                
        //ambil kotak yg sudah pasti
        for(int y=0; y<baris; y++){
            for(int x=0; x<kolom; x++){
                if(fixedBoard[y][x] != null) {
                    finalBoard[y][x] = fixedBoard[y][x];
                }
            }
        }
        
        //ambil kotak yg blm pasti
        if (chromosome != null && !chromosome.isEmpty()) {
            for (int i = 0; i < chromosome.size(); i++) {
                Koordinat koorSekarang = daftarKotakTidakPasti.get(i);
                finalBoard[koorSekarang.getY()][koorSekarang.getX()] = chromosome.get(i);
            }
        }

        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                System.out.print((finalBoard[y][x] == 1 ? "# " : ". "));
            }
            System.out.println();
        }
        printYgMasihError(finalBoard);
    }

    public static int getChromosomeSize() {//method untuk ambil ukuran kromosom (banyak kotak yang tidak pasti)
        return daftarKotakTidakPasti.size();
    }
}