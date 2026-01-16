import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Menyimpan informasi kumpulan individu.
 * Informasi tersebut berupa kumpulan individu, maksimal populasi, elitism rate, mutation rate, dan crossover rate
 *
 * @author Owen Lianggara
 * @author Andrew Kevin Alexander
 */
public class Populasi {
    /**
     * Menyimpan kumpulan individu
     */
    public ArrayList<Individu> populasi;

    /**
     * batas maksimal individu dalam sebuah populasi
     */
    private int maxPopulasi;

    /**
     * elitism rate
     */
    public double elitismPct;

    /**
     * mutation rate
     */
    public double mutationRate;

    /**
     * crossover rate
     */
    public double crossoverRate;

    /**
     * random dengan seed yang akan diberikan sebagai parameter
     */
    Random rand;

    /**
     * Construct Populasi baru
     *
     * @param maxPop maksimum individu dalam sebuah populasi
     * @param elitism elitism rate
     * @param mutRate mutation rate
     * @param crossRate crossover rate
     * @param rand random dengan seed
     */
    public Populasi(int maxPop, double elitism, double mutRate, double crossRate, Random rand) {
        this.maxPopulasi = maxPop;
        this.elitismPct = elitism;
        this.mutationRate = mutRate;
        this.crossoverRate = crossRate;
        this.rand = rand;
        this.populasi = new ArrayList<>();
    }

    /**
     * Membuat populasi di mana setiap individu memiliki gen yang ditentukan secara random
     */
    public void randomPopulasi() {
        for (int i = 0; i < maxPopulasi; i++) {
            // buat individu baru dengan jumlah gen dan masukkan ke dalam populasi
            Individu idv = new Individu(this.rand);
            this.populasi.add(idv);
        }
    }

    /**
     * Menambahkan individu pada populasi
     *
     * @param idv objek individu yang ingin dimasukkan ke populasi
     */
    public void addIndividu(Individu idv) { //tambahkan individu ke dalam populasi
        if (this.populasi.size() < maxPopulasi) {
            this.populasi.add(idv);
        }
    }

    /**
     * Menghitung semua fitness individu pada populasi
     */
    public void calcAllFitnesses() { //hitung fitness dan set ke dalam atribut fitness individu
        for (Individu idv : populasi) {
            idv.setFitness();
        }
    }

    /**
     * Memeriksa apakah populasi sudah penuh atau belum.
     *
     * @return Belum penuh: False, sudah penuh: True
     */
    public boolean isFilled() { //cek apakah populasi sudah penuh atau belum
        return populasi.size() >= maxPopulasi;
    }

    /**
     * Mengambil individu terbaik pada populasi.
     * Mengurutkan individu berdasarkan fitness pada populasi dan ambil individu yang pertama
     *
     * @return individu dengan fitness terbaik (terkecil)
     */
    public Individu getBestIdv() { //ambil individu terbaik dalam populasi ini
        if (populasi.isEmpty()) return null;
        Collections.sort(populasi);
        return populasi.get(0);
    }

    /**
     * Membuat populasi baru dengan menerapkan elitism
     *
     * @return populasi baru
     */
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
//            Individu[] parents = ParentSelection.rouletteWheel(rand, populasi);
//            Individu[] parents = ParentSelection.rankSelection(rand, populasi);
            Individu[] parents = ParentSelection.tournamentSelection(rand, populasi);

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