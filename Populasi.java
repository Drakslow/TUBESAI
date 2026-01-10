import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Populasi {
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
        //jumlah gen
        int genomeSize = MosaicGA.getChromosomeSize();

        for (int i = 0; i < maxPopulasi; i++) {
            // buat individu baru dengan jumlah gen dan masukkan ke dalam populasi
            Individu idv = new Individu(this.rand, genomeSize);
            this.populasi.add(idv);
        }
    }

    public void addIndividu(Individu idv) { //tambahkan individu ke dalam populasi
        if (this.populasi.size() < maxPopulasi) {
            this.populasi.add(idv);
        }
    }

    public void calcAllFitnesses() { //hitung fitness dan set ke dalam atribut fitness individu
        for (Individu idv : populasi) {
            idv.setFitness();
        }
    }

    public boolean isFilled() { //cek apakah populasi sudah penuh atau belum
        return populasi.size() >= maxPopulasi;
    }

    public Individu getBestIdv() { //ambil individu terbaik dalam populasi ini
        if (populasi.isEmpty()) return null;
        Collections.sort(populasi);
        return populasi.get(0);
    }

    public Populasi getNewPopulasiWElit() {
        //buat populasi baru
        Populasi newPop = new Populasi(maxPopulasi, elitismPct, mutationRate, crossoverRate, rand);

        //urutkan populasi dari yang terbaik (fitness 0) sampai yang terburuk (fitness tak hingga)
        Collections.sort(this.populasi);

        // Elitism
        // menyalin populasi dari yang terbaik sebanyak numElites
        int numElites = (int) (maxPopulasi * elitismPct);

        //masukkan individu elit ke dalam populasi baru
        for (int i = 0; i < numElites; i++) {
            newPop.addIndividu(new Individu(this.populasi.get(i)));
        }

        while (!newPop.isFilled()) {
            //pilih parent selection
            Individu[] parents = ParentSelection.rouletteWheel(rand, populasi);
//            Individu[] parents = ParentSelection.rankSelection(rand, populasi);
//            Individu[] parents = ParentSelection.tournamentSelection(rand, populasi);

            //hanya 2 parent yang akan dipilih dan menghasilkan 2 children baru

            Individu[] children;
            if (rand.nextDouble() < crossoverRate) { //lakukan crossover
                children = parents[0].crossover(parents[1]);
            }
            else { //crossover gagal
                //anaknya copy dari orang tua
                children = new Individu[]{
                    new Individu(parents[0]),
                    new Individu(parents[1])
                };
            }

            // Mutasi anak 1 dan 2 lalu tambahkan sebagai individu baru ke dalam populasi baru
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