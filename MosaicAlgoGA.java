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

        currentPop.randomPopulasi();
        currentPop.calcAllFitnesses();

        Individu globalBest = currentPop.getBestIdv();

        while (generation <= maxGen) {
            Individu currentBest = currentPop.getBestIdv();

            if (currentBest.getFitness() < globalBest.getFitness()) {
                globalBest = currentBest;
            }

            System.out.printf("Gen: %d | Best Fitness: %.1f\n", generation, globalBest.getFitness());

            if (globalBest.getFitness() == 0) {
                System.out.println("Solution found: " + generation);
                break;
            }

            currentPop = currentPop.getNewPopulasiWElit();
            currentPop.calcAllFitnesses();
            generation++;
        }

        return globalBest;
    }
}