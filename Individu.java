import java.util.ArrayList;
import java.util.Random;

class Individu implements Comparable<Individu> {
    public double fitness;
    public Random rand;
    public double beParentProbability;

    // Kromosom: 0 = Putih, 1 = Hitam
    public ArrayList<Integer> kromosom;

    public Individu(Random rand) {
        this.rand = rand;
        this.kromosom = new ArrayList<>();
        this.fitness = Double.MAX_VALUE;
        this.beParentProbability = 0;
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

    public Individu[] crossover(Individu other) {
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

    public void mutation(double mutationRate) {
        for (int i = 0; i < this.kromosom.size(); i++) {
            if (rand.nextDouble() < mutationRate) {
                int currentVal = this.kromosom.get(i);
                //mutasi lgsg flip bitnya
                this.kromosom.set(i, currentVal == 1 ? 0 : 1);
            }
        }
    }

    public double setFitness() {
        this.fitness = MosaicGA.calcFitness(this.kromosom);
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