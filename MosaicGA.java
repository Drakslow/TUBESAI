import java.io.*;
import java.util.*;

public class MosaicGA {
    protected static int baris, kolom;

    protected static int[][] map;

    protected static int seed = 42;
    protected static final Random rnd = new Random(seed);


    //TODO : Bikin Variasi lain dari fungsi fitness ini
    static double hitungFit(ArrayList<Integer> gene) {
        int totalError = 0;

        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                int clue = map[y][x];

                if (clue == -1) continue;

                int currentBlacks = hitungHitam(gene, x, y);

                totalError += Math.abs(currentBlacks - clue);
            }
        }
        return (double) totalError;
    }

    private static int hitungHitam(ArrayList<Integer> gene, int cX, int cY) {
        int count = 0;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nX = cX + dx;
                int nY = cY + dy;

                if (nX >= 0 && nX < kolom && nY >= 0 && nY < baris) {
                    // Konversi 2D (x,y) ke index 1D List
                    int index = nY * kolom + nX;
                    if (gene.get(index) == 1) {
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

        System.out.println("--- Mulai GA Mosaic ---");
        System.out.println("Target: Fitness 0.0");

        // Param pam pam (bisa diganti sesuai kebutuhan testing)
        int maxGenerations = 1000;
        int populasiSize = 100;
        double crossoverRate = 0.8;
        double elitismRate = 0.05;
        double mutationRate = 0.02;

        MosaicAlgoGA GA = new MosaicAlgoGA(rnd, populasiSize, maxGenerations, mutationRate, elitismRate, crossoverRate);

        Individu bestSolution = GA.run();

        System.out.println("\n--- Best Solution Found ---");
        System.out.printf("Final Fitness: %.0f (0 is Solved)\n", bestSolution.getFitness());

        printBoard(bestSolution.kromosom);
    }

    private static void printBoard(ArrayList<Integer> gene) {
        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                int index = y * kolom + x;
                int val = gene.get(index);
                // '#' untuk Hitam, '.' untuk Putih
                System.out.print((val == 1 ? "# " : ". "));
            }
            System.out.println();
        }
    }
}

class Individu implements Comparable<Individu> {
    public double fitness;
    public Random rand;
    
    // Kromosom: 0 = Putih, 1 = Hitam
    public ArrayList<Integer> kromosom;

    public Individu(Random rand) {
        this.rand = rand;
        this.kromosom = new ArrayList<>();
        this.fitness = Double.MAX_VALUE;
    }

    public Individu(Random rand, int size) {
        for (int i = 0; i < size; i++) {
            this.kromosom.add(rand.nextBoolean() ? 1 : 0);
        }
    }


    public Individu(Individu other) {
        this.rand = other.rand;
        this.fitness = other.fitness;
        this.kromosom = new ArrayList<>(other.kromosom);
    }

    public Individu[] doCrossover(Individu other) {
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        int size = this.kromosom.size();
        int cutPoint = rand.nextInt(size);

        for (int i = 0; i < size; i++) {
            if (i < cutPoint) {
                anak1.kromosom.add(this.kromosom.get(i));
                anak2.kromosom.add(other.kromosom.get(i));
            } else {
                anak1.kromosom.add(other.kromosom.get(i));
                anak2.kromosom.add(this.kromosom.get(i));
            }
        }

        return new Individu[]{anak1, anak2};
    }

    public void doMutation(double mutationRate) {
        for (int i = 0; i < this.kromosom.size(); i++) {
            if (rand.nextDouble() < mutationRate) {
                int currentVal = this.kromosom.get(i);
                //mutasi lgsg flip bitnya
                this.kromosom.set(i, currentVal == 1 ? 0 : 1);
            }
        }
    }

    public double setFitness() {
        this.fitness = MosaicGA.hitungFit(this.kromosom);
        return this.fitness;
    }

    public double getFitness() {
        return this.fitness;
    }

    @Override
    public int compareTo(Individu other) {
        return Double.compare(this.fitness, other.fitness);
    }
}


class Populasi {
    public ArrayList<Individu> populasi;
    private int maxPopulasi;
    public double elitismPct;
    public double mutationRate;
    public double crossoverRate;
    Random rand;

    public Populasi(int maxPop, double elitism, double mutRate, double crossRate, Random rand) {
        this.maxPopulasi = maxPop;
        this.elitismPct = elitism;
        this.mutationRate = mutRate;
        this.crossoverRate = crossRate;
        this.rand = rand;
        this.populasi = new ArrayList<>();
    }

    public void randomPopulasi() {
        int genomeSize = MosaicGA.baris * MosaicGA.kolom;
        for (int i = 0; i < maxPopulasi; i++) {
            Individu idv = new Individu(this.rand, genomeSize);
            this.populasi.add(idv);
        }
    }

    public void addIndividu(Individu idv) {
        if (this.populasi.size() < maxPopulasi) {
            this.populasi.add(idv);
        }
    }

    public void computeAllFitnesses() {
        for (Individu idv : populasi) {
            idv.setFitness();
        }
    }

    public boolean isFilled() {
        return populasi.size() >= maxPopulasi;
    }

    public Individu getBestIdv() {
        if (populasi.isEmpty()) return null;
        Collections.sort(populasi);
        return populasi.get(0);
    }

    public Populasi getNewPopulasiWElit() {
        Populasi newPop = new Populasi(maxPopulasi, elitismPct, mutationRate, crossoverRate, rand);

        Collections.sort(this.populasi);
        
        // elitism
        int numElites = (int) (maxPopulasi * elitismPct);
        for (int i = 0; i < numElites; i++) {
            newPop.addIndividu(new Individu(this.populasi.get(i)));
        }

        while (!newPop.isFilled()) {
            Individu[] parents = selectParentRoulette();

            Individu[] children;
            if (rand.nextDouble() < crossoverRate) {
                // Lakukan crossover (jika dapat peluang)
                children = parents[0].doCrossover(parents[1]);
            } else {
                // Jika tidak crossover, anaknya copy dari orang tua
                children = new Individu[]{
                        new Individu(parents[0]),
                        new Individu(parents[1])
                };
            }

            // Mutasi Child 1 & Add
            if (!newPop.isFilled()) {
                children[0].doMutation(mutationRate);
                newPop.addIndividu(children[0]);
            }

            // Mutasi Child 2 & Add
            if (!newPop.isFilled()) {
                children[1].doMutation(mutationRate);
                newPop.addIndividu(children[1]);
            }
        }

        return newPop;
    }

    // Roulette Wheel
    public Individu[] selectParentRoulette() {
        Individu[] parents = new Individu[2];
        double totalInverseFitness = 0;

        // Fitness terbaik = 0
        // Rumus invers dari fitness = 1 / (fitness + epsilon)
        double[] probs = new double[populasi.size()];

        for (int i = 0; i < populasi.size(); i++) {
            double fit = populasi.get(i).getFitness();
            double score = 1.0 / (fit + 0.1); // +0.1 menghindari div by zero jika solved
            probs[i] = score;
            totalInverseFitness += score;
        }

        // Normalisasi
        for (int i = 0; i < populasi.size(); i++) {
            probs[i] = probs[i] / totalInverseFitness;
        }

        // Pilih 2 Parent
        for (int p = 0; p < 2; p++) {
            double r = rand.nextDouble();
            double sum = 0;
            int selectedIdx = populasi.size() - 1;
            for (int i = 0; i < populasi.size(); i++) {
                sum += probs[i];
                if (sum >= r) {
                    selectedIdx = i;
                    break;
                }
            }
            parents[p] = populasi.get(selectedIdx);
        }
        return parents;
    }
}

/**
 * Controller Logika Utama GA
 */
class MosaicAlgoGA {
    private int maxGen;
    private int popSize;
    private double mutRate;
    private double elitRate;
    private double crossRate;
    private Random rand;

    public MosaicAlgoGA(Random r, int pSize, int maxG, double mRate, double eRate, double cRate) {
        this.rand = r;
        this.popSize = pSize;
        this.maxGen = maxG;
        this.mutRate = mRate;
        this.elitRate = eRate;
        this.crossRate = cRate;
    }

    public Individu run() {
        int generation = 1;
        Populasi currentPop = new Populasi(popSize, elitRate, mutRate, crossRate, rand);

        // Random Populasi Awal
        currentPop.randomPopulasi();
        currentPop.computeAllFitnesses();

        Individu globalBest = currentPop.getBestIdv();

        // Loop Generasi
        while (generation <= maxGen) {
            // Cek apakah sudah solved (Fitness 0)
            Individu currentBest = currentPop.getBestIdv();
            if (currentBest.getFitness() < globalBest.getFitness()) {
                globalBest = currentBest;
            }

            // Print info per 10 generasi (atau jika solved)
            if (generation % 10 == 0 || globalBest.getFitness() == 0) {
                System.out.printf("Gen: %d | Best Fitness: %.1f\n", generation, globalBest.getFitness());
            }

            if (globalBest.getFitness() == 0) {
                System.out.println("Solution found at generation " + generation);
                break;
            }

            // Buat populasi baru
            currentPop = currentPop.getNewPopulasiWElit();
            currentPop.computeAllFitnesses();
            generation++;
        }

        return globalBest;
    }
}