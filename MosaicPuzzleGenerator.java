import java.util.Random;
import java.util.Scanner;

public class MosaicPuzzleGenerator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("### GENERATOR PUZZLE MOSAIC (FORMAT ANGKA & -1) ###");

        // 1. Input Konfigurasi
        System.out.print("Masukkan Jumlah Baris (misal 5): ");
        int rows = scanner.nextInt();
        System.out.print("Masukkan Jumlah Kolom (misal 5): ");
        int cols = scanner.nextInt();

        System.out.print("Pilih Kesulitan (1 = Easy, 2 = Hard): ");
        int diffChoice = scanner.nextInt();

        System.out.print("Jumlah Puzzle yang ingin dibuat: ");
        int totalPuzzles = scanner.nextInt();

        // Konfigurasi Kesulitan
        double mineProb; // Peluang muncul ranjau (1)
        double hiddenProb; // Peluang petunjuk jadi -1 (disembunyikan)

        if (diffChoice == 1) { // EASY
            mineProb = 0.20; // 20% area adalah ranjau
            hiddenProb = 0.30; // 30% petunjuk ditutup jadi -1
        } else { // HARD
            mineProb = 0.40; // 40% area adalah ranjau
            hiddenProb = 0.60; // 60% petunjuk ditutup jadi -1 (lebih susah)
        }

        System.out.println("\nMemulai generate " + totalPuzzles + " puzzle...\n");

        // 2. Loop Pembuatan Puzzle
        for (int p = 1; p <= totalPuzzles; p++) {
            System.out.println("========================================");
            System.out.println("PUZZLE #" + p + " (Tipe: " + (diffChoice == 1 ? "Easy" : "Hard") + ")");
            System.out.println("========================================");

            // A. Generate Kunci Jawaban (Mines) 0 atau 1
            int[][] solutionGrid = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    // Jika random < mineProb, maka jadi ranjau (1), selain itu (0)
                    solutionGrid[i][j] = (random.nextDouble() < mineProb) ? 1 : 0;
                }
            }

            // B. Generate Soal (Clues) berdasarkan aturan Mosaic
            int[][] puzzleGrid = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    // Hitung total ranjau di sekeliling (termasuk diri sendiri)
                    int val = countNeighbors(solutionGrid, i, j, rows, cols);

                    // Putuskan apakah angka ini ditampilkan atau disembunyikan jadi -1
                    if (random.nextDouble() < hiddenProb) {
                        puzzleGrid[i][j] = -1; // Sembunyikan
                    } else {
                        puzzleGrid[i][j] = val; // Tampilkan angka
                    }
                }
            }

            // C. Cetak Output
            System.out.println("BENTUK PUZZLE:");
            printGrid(puzzleGrid, true); // true = format lebar agar -1 rapi

            System.out.println("\nHASILNYA (KUNCI JAWABAN):");
            printGrid(solutionGrid, false); // false = format rapat (0/1)

            System.out.println("\n");
        }

        scanner.close();
    }

    // Fungsi menghitung jumlah ranjau (1) di blok 3x3
    private static int countNeighbors(int[][] grid, int r, int c, int maxR, int maxC) {
        int count = 0;
        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                // Cek batas array agar tidak error
                if (i >= 0 && i < maxR && j >= 0 && j < maxC) {
                    count += grid[i][j]; // Tambahkan 1 jika ranjau, 0 jika kosong
                }
            }
        }
        return count;
    }

    // Fungsi print array rapi
    private static void printGrid(int[][] grid, boolean wideFormat) {
        for (int[] row : grid) {
            for (int val : row) {
                if (wideFormat) {
                    // Format %3d memberikan spasi agar -1 dan angka positif sejajar
                    System.out.printf("%3d ", val);
                } else {
                    // Format sederhana untuk 0 dan 1
                    System.out.print(val + " ");
                }
            }
            System.out.println();
        }
    }
}