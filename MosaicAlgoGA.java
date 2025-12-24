import java.util.Random;

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