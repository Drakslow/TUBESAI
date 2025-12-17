import java.io.*;
import java.util.*;

public class MosaicGA {


    protected static int baris, kolom;


    protected static int[][] map;

    protected static int seed = 42;
    protected static final Random rnd = new Random(seed);

    static double hitungFit(ArrayList<Integer> genes) {
        int totalError = 0;

        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                int clue = map[y][x];

                if (clue == -1) continue;

                int currentBlacks = hitungHitam(genes, x, y);

                totalError += Math.abs(currentBlacks - clue);
            }
        }
        return (double) totalError;
    }

    private static int hitungHitam(ArrayList<Integer> genes, int cX, int cY) {
        int count = 0;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nX = cX + dx;
                int nY = cY + dy;

                if (nX >= 0 && nX < kolom && nY >= 0 && nY < baris) {
                    // Konversi 2D (x,y) ke index 1D List
                    int index = nY * kolom + nX;
                    if (genes.get(index) == 1) {
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

        System.out.println("--- Starting Mosaic GA ---");
        System.out.println("Target: Fitness 0.0");

        // Param pam pam
        int maxGenerations = 1000;
        int populationSize = 100;
        double crossoverRate = 0.8;
        double elitismRate = 0.05;
        double mutationRate = 0.02;

        MosaicAlgoGA GA = new MosaicAlgoGA(rnd, populationSize, maxGenerations, mutationRate, elitismRate, crossoverRate);

        Individual bestSolution = GA.run();

        System.out.println("\n--- Best Solution Found ---");
        System.out.printf("Final Fitness: %.0f (0 is Solved)\n", bestSolution.getFitness());

        printBoard(bestSolution.chromosome);
    }

    private static void printBoard(ArrayList<Integer> genes) {
        for (int y = 0; y < baris; y++) {
            for (int x = 0; x < kolom; x++) {
                int index = y * kolom + x;
                int val = genes.get(index);
                // Cetak '#' untuk Hitam, '.' untuk Putih
                System.out.print((val == 1 ? "# " : ". "));
            }
            System.out.println();
        }
    }
}

class Individual implements Comparable<Individual> {
    public double fitness;
    public Random MyRand;
    // Kromosom: 0 = Putih, 1 = Hitam
    public ArrayList<Integer> chromosome;

    public Individual(Random MyRand) {
        this.MyRand = MyRand;
        this.chromosome = new ArrayList<>();
        this.fitness = Double.MAX_VALUE;
    }

    public Individual(Random MyRand, int size) {
        for (int i = 0; i < size; i++) {
            this.chromosome.add(MyRand.nextBoolean() ? 1 : 0);
        }
    }

    // Deep Copy
    public Individual(Individual other) {
        this.MyRand = other.MyRand;
        this.fitness = other.fitness;
        this.chromosome = new ArrayList<>(other.chromosome);
    }

    public Individual[] doCrossover(Individual other) {
        Individual child1 = new Individual(this.MyRand);
        Individual child2 = new Individual(this.MyRand);

        int size = this.chromosome.size();
        int cutPoint = MyRand.nextInt(size);

        for (int i = 0; i < size; i++) {
            if (i < cutPoint) {
                child1.chromosome.add(this.chromosome.get(i));
                child2.chromosome.add(other.chromosome.get(i));
            } else {
                child1.chromosome.add(other.chromosome.get(i));
                child2.chromosome.add(this.chromosome.get(i));
            }
        }

        return new Individual[]{child1, child2};
    }

    public void doMutation(double mutationRate) {
        for (int i = 0; i < this.chromosome.size(); i++) {
            if (MyRand.nextDouble() < mutationRate) {
                int currentVal = this.chromosome.get(i);
                this.chromosome.set(i, currentVal == 1 ? 0 : 1);
            }
        }
    }

    public double setFitness() {
        this.fitness = MosaicGA.hitungFit(this.chromosome);
        return this.fitness;
    }

    public double getFitness() {
        return this.fitness;
    }

    @Override
    public int compareTo(Individual other) {
        return Double.compare(this.fitness, other.fitness);
    }
}


class Population {
    public ArrayList<Individual> population;
    private int maxPopulationSize;
    public double elitismPct;
    public double mutationRate;
    public double crossoverRate;
    Random MyRand;

    public Population(int maxPop, double elitism, double mutRate, double crossRate, Random rand) {
        this.maxPopulationSize = maxPop;
        this.elitismPct = elitism;
        this.mutationRate = mutRate;
        this.crossoverRate = crossRate;
        this.MyRand = rand;
        this.population = new ArrayList<>();
    }

    public void randomPopulation() {
        int genomeSize = MosaicGA.baris * MosaicGA.kolom;
        for (int i = 0; i < maxPopulationSize; i++) {
            Individual idv = new Individual(this.MyRand, genomeSize);
            this.population.add(idv);
        }
    }

    public void addIndividual(Individual idv) {
        if (this.population.size() < maxPopulationSize) {
            this.population.add(idv);
        }
    }

    public void computeAllFitnesses() {
        for (Individual idv : population) {
            idv.setFitness();
        }
    }

    public boolean isFilled() {
        return population.size() >= maxPopulationSize;
    }

    public Individual getBestIdv() {
        if (population.isEmpty()) return null;
        Collections.sort(population);
        return population.get(0);
    }

    public Population getNewPopulationWElit() {
        Population newPop = new Population(maxPopulationSize, elitismPct, mutationRate, crossoverRate, MyRand);

        Collections.sort(this.population);
        //Elitism
        int numElites = (int) (maxPopulationSize * elitismPct);
        for (int i = 0; i < numElites; i++) {
            // Gunakan Copy Constructor agar aman
            newPop.addIndividual(new Individual(this.population.get(i)));
        }

        while (!newPop.isFilled()) {
            Individual[] parents = selectParentRoulette();

            Individual[] children;
            // Crossover Check
            if (MyRand.nextDouble() < crossoverRate) {
                children = parents[0].doCrossover(parents[1]);
            } else {
                // Jika tidak crossover, anak adalah copy dari orang tua
                children = new Individual[]{
                        new Individual(parents[0]),
                        new Individual(parents[1])
                };
            }

            // Mutasi Child 1 & Add
            if (!newPop.isFilled()) {
                children[0].doMutation(mutationRate);
                newPop.addIndividual(children[0]);
            }

            // Mutasi Child 2 & Add
            if (!newPop.isFilled()) {
                children[1].doMutation(mutationRate);
                newPop.addIndividual(children[1]);
            }
        }

        return newPop;
    }

    // Seleksi Roulette Wheel
    public Individual[] selectParentRoulette() {
        Individual[] parents = new Individual[2];
        double totalInverseFitness = 0;

        // Mosaic Fitness: 0 adalah terbaik. Kita butuh inversenya.
        // Rumus sederhana: 1 / (fitness + epsilon)
        double[] probs = new double[population.size()];

        for (int i = 0; i < population.size(); i++) {
            double fit = population.get(i).getFitness();
            double score = 1.0 / (fit + 0.1); // +0.1 menghindari div by zero jika solved
            probs[i] = score;
            totalInverseFitness += score;
        }

        // Normalisasi
        for (int i = 0; i < population.size(); i++) {
            probs[i] = probs[i] / totalInverseFitness;
        }

        // Pilih 2 Parent
        for (int p = 0; p < 2; p++) {
            double r = MyRand.nextDouble();
            double sum = 0;
            int selectedIdx = population.size() - 1;
            for (int i = 0; i < population.size(); i++) {
                sum += probs[i];
                if (sum >= r) {
                    selectedIdx = i;
                    break;
                }
            }
            parents[p] = population.get(selectedIdx);
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

    public Individual run() {
        int generation = 1;
        Population currentPop = new Population(popSize, elitRate, mutRate, crossRate, rand);

        // 1. Random Populasi Awal
        currentPop.randomPopulation();
        currentPop.computeAllFitnesses();

        Individual globalBest = currentPop.getBestIdv();

        // 2. Loop Generasi
        while (generation <= maxGen) {
            // Cek apakah sudah solved (Fitness 0)
            Individual currentBest = currentPop.getBestIdv();
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
            currentPop = currentPop.getNewPopulationWElit();
            currentPop.computeAllFitnesses();
            generation++;
        }

        return globalBest;
    }
}