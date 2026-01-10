import java.util.Random;

public class MosaicAlgoGA {
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

        //buat populasi baru
        Populasi currentPop = new Populasi(popSize, elitRate, mutRate, crossRate, rand);

        // Random Populasi Awal
        currentPop.randomPopulasi();
        currentPop.calcAllFitnesses();

        //individu terbaik dalam seluruh generasi
        Individu globalBest = currentPop.getBestIdv();

        //loop sebanyak maksimal generasi
        while (generation <= maxGen) {
            //individu terbaik di generasi saat ini
            Individu currentBest = currentPop.getBestIdv();

            if (currentBest.getFitness() < globalBest.getFitness()) { //set generasi terbaik dalam seluruh generasi
                globalBest = currentBest;
            }

            System.out.printf("Generasi: %d | Best Fitness: %.1f\n", generation, globalBest.getFitness());

            if (globalBest.getFitness() == 0) {
                System.out.println("Solution found: " + generation);
                break;
            }

            // Buat populasi baru dengan elitism
            currentPop = currentPop.getNewPopulasiWElit();
            currentPop.calcAllFitnesses();
            generation++;
        }

        return globalBest;
    }
}