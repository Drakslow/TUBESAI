import java.util.ArrayList;
import java.util.Random;

public class Individu implements Comparable<Individu> {
    /**
     * Menyimpan fitness sebuah individu
     */
    public double fitness;

    /**
     * Random untuk peluang mutasi
     */
    public Random rand;

    /**
     * Simpan gen di dalam kromosom
     *
     * Kromosom: 0 = Putih, 1 = Hitam
     */
    public ArrayList<Integer> kromosom;

    /**
     * Construct individu baru dengan nilai peluang mutasi random
     *
     * @param rand peluang mutasi
     */
    public Individu(Random rand) {
        this.rand = rand;
        this.kromosom = new ArrayList<>();
        this.fitness = Double.MAX_VALUE;
    }

    public Individu(Random rand, int size) {
        this(rand);

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
        //buat 2 children baru
        Individu anak1 = new Individu(this.rand);
        Individu anak2 = new Individu(this.rand);

        int size = this.kromosom.size();
        int crossoverType = 3; //ubah tipe crossover di sini

        // Untuk single dan two point
        //0 sampai first
        //(first+1) sampai second
        int firstCutPoint = rand.nextInt(size-1); // dari 0
        int secondCutPoint = firstCutPoint + rand.nextInt(size - firstCutPoint); // sampai size-1

        //pencegahan salah logic
        if (firstCutPoint == secondCutPoint) {
            secondCutPoint++;
        }

        if (crossoverType == 1) {
            //dalam first point, secondCutPoint menjadi pembaginya
            firstCutPoint = 0;
        }

        if (crossoverType < 3) { //yang bukan uniform
            for (int i = 0; i < size; i++) {
                //dibuat i < second cut karena dalam kasus one dan two point cut, jika second cut berada di size-1, size-1 bisa di crossover
                //sehingga children tidak copy dari orang tua
                if (i >= firstCutPoint && i < secondCutPoint) { //i < second karena menyisakan ujung untuk crossover
                    anak1.kromosom.add(this.kromosom.get(i));
                    anak2.kromosom.add(other.kromosom.get(i));
                } else { //untuk yang di bawah first cut dan di atas atau sama dengan second cut
                    anak1.kromosom.add(other.kromosom.get(i));
                    anak2.kromosom.add(this.kromosom.get(i));
                }
            }
        }
        else { //untuk uniform
            for (int i = 0; i < size; i++) {
                //setiap gen orang tua punya peluang 0.5 untuk ditempati di gen anak 1 atau 2
                if (rand.nextDouble() < 0.5) {
                    anak1.kromosom.add(this.kromosom.get(i));
                    anak2.kromosom.add(other.kromosom.get(i));
                } else {
                    anak1.kromosom.add(other.kromosom.get(i));
                    anak2.kromosom.add(this.kromosom.get(i));
                }
            }
        }

        return new Individu[]{anak1, anak2};
    }

    public void mutation(double mutationRate) {
        //lakukan mutasi di setiap gen
        for (int i = 0; i < this.kromosom.size(); i++) {
            if (rand.nextDouble() < mutationRate) {
                int currentVal = this.kromosom.get(i);
                //mutasi lgsg flip bitnya
                this.kromosom.set(i, currentVal == 1 ? 0 : 1);
            }
        }
    }

    public double setFitness() {
        //set fitness untuk individu ini
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