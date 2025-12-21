import java.io.*;
import java.util.*;

public class MosaicGA {
    // GA SPEC
    protected static int seed = 42;
    protected static final Random rnd = new Random(seed);

    // BOARD SPEC
    protected static int baris, kolom;
    protected static int[][] map;

    //TODO : Bikin Variasi lain dari fungsi fitness ini
    static double calcFitness(ArrayList<Integer> chromosome) {
        int totalError = 0;

        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                int clue = map[y][x];
                if (clue == -1) continue;

                int currentBlacks = hitungHitam(chromosome, x, y);
                totalError += Math.abs(currentBlacks - clue);
            }
        }
        return (double) totalError;
    }

    private static int hitungHitam(ArrayList<Integer> chromosome, int cX, int cY) {
        int count = 0;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nX = cX + dx;
                int nY = cY + dy;

                if (nX >= 0 && nX < kolom && nY >= 0 && nY < baris) {
                    // Konversi 2D (x,y) ke index 1D List
                    int index = nY * kolom + nX;
                    if (chromosome.get(index) == 1) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        baris = sc.nextInt();
        kolom = sc.nextInt();
        int inputMap[][] = new int[baris][kolom];

        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                inputMap[i][j] = sc.nextInt();
            }
        }

        map = inputMap;

        // Parameter
        int maxGenerations = 1000;
        int populasiSize = 100;
        double crossoverRate = 0.8;
        double elitismRate = 0.05;
        double mutationRate = 0.02;

        MosaicAlgoGA GA = new MosaicAlgoGA(rnd, populasiSize, maxGenerations, mutationRate, elitismRate, crossoverRate);

        Individu bestSolution = GA.run();

        System.out.println("\n=== Best Solution ===");
        System.out.printf("Final Fitness: %.0f\n", bestSolution.getFitness());

        printBestSolution(bestSolution.kromosom);
    }

    private static void printBestSolution(ArrayList<Integer> chromosome) {
        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                int index = y * kolom + x;
                int val = chromosome.get(index);
                // '#' = Hitam
                // '.' = Putih
                System.out.print((val == 1 ? "# " : ". "));
            }
            System.out.println();
        }
    }
}