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

    public void calcAllFitnesses() {
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

        // Elitism
        int numElites = (int) (maxPopulasi * elitismPct);
        for (int i = 0; i < numElites; i++) {
            newPop.addIndividu(new Individu(this.populasi.get(i)));
        }

        while (!newPop.isFilled()) {
//            Individu[] parents = ParentSelection.rouletteWheel(rand, populasi);
            Individu[] parents = ParentSelection.rankSelection(rand, populasi);


            Individu[] children;
            if (rand.nextDouble() < crossoverRate) {
                children = parents[0].crossover(parents[1]);
            } else {
                // Jika tidak crossover, anaknya copy dari orang tua
                children = new Individu[]{
                        new Individu(parents[0]),
                        new Individu(parents[1])
                };
            }

            // Mutasi anak 1 dan 2
            if (!newPop.isFilled()) {
                children[0].mutation(mutationRate);
                newPop.addIndividu(children[0]);
            }

            if (!newPop.isFilled()) {
                children[1].mutation(mutationRate);
                newPop.addIndividu(children[1]);
            }
        }

        return newPop;
    }

}