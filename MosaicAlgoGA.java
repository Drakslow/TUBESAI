import java.util.Random;

/**
 * Menjalankan algoritma genetika untuk Mosaic Puzzle
 *
 * @author Owen Lianggara
 * @author Andrew Kevin Alexander
 */
public class MosaicAlgoGA {
    /**
     * maksimal generasi
     */
    private int maxGen;

    /**
     * ukuran populasi
     */
    private int popSize;

    /**
     * mutation rate
     */
    private double mutRate;

    /**
     * elitism rate
     */
    private double elitRate;

    /**
     * crossover rate
     */
    private double crossRate;

    /**
     * random dengan seed
     */
    private Random rand;

    /**
     * Construct genetic algorithm baru
     *
     * @param r random dengan seed
     * @param pSize ukuran populasi
     * @param maxG maksimal generasi
     * @param mRate mutation rate
     * @param eRate elitism rate
     * @param cRate crossover rate
     */
    public MosaicAlgoGA(Random r, int pSize, int maxG, double mRate, double eRate, double cRate) {
        this.rand = r;
        this.popSize = pSize;
        this.maxGen = maxG;
        this.mutRate = mRate;
        this.elitRate = eRate;
        this.crossRate = cRate;
    }

    /**
     * Menjalankan algoritma genetika.
     * Di awali dengan random populasi baru, kemudian loop untuk mengembangkan populasi sebanyak maksimal generasi,
     * Berhenti ketika sudah mencapai batas maksimal generasi atau menemukan solusi dengan fitness 1.0 (terbaik)
     *
     * @return individu dengan solusi terbaik
     */
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

            if (currentBest.getFitness() > globalBest.getFitness()) { //set generasi terbaik dalam seluruh generasi
                globalBest = currentBest;
            }

            System.out.printf("Generasi: %4d | Best Fitness: %.5f\n", generation, globalBest.getFitness());

            if (globalBest.getFitness() == 1.0) {
                System.out.println("Solution found: " + generation);
                break;
            }

            // Buat populasi baru dengan elitism
            currentPop = currentPop.getNewPopulasiWElit();
            currentPop.calcAllFitnesses();

            //Eksperimen
            MosaicGA.generasiBestF[MosaicGA.counterInput][generation]=currentPop.getBestIdv().getFitness();


            generation++;
        }

        //EKsperimen
        MosaicGA.generasiPerInput[MosaicGA.counterInput]=generation;

        return globalBest;
    }
}