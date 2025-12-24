import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
            Individu[] parents = rouletteWheel();

            Individu[] children;
            if (rand.nextDouble() < crossoverRate) {
                // Lakukan crossover (jika dapat peluang)
                children = parents[0].crossover(parents[1]);
            } else {
                // Jika tidak crossover, anaknya copy dari orang tua
                children = new Individu[]{
                        new Individu(parents[0]),
                        new Individu(parents[1])
                };
            }

            // Mutasi Child 1 & Add
            if (!newPop.isFilled()) {
                children[0].mutation(mutationRate);
                newPop.addIndividu(children[0]);
            }

            // Mutasi Child 2 & Add
            if (!newPop.isFilled()) {
                children[1].mutation(mutationRate);
                newPop.addIndividu(children[1]);
            }
        }

        return newPop;
    }

    // Roulette Wheel
    public Individu[] rouletteWheel() {
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