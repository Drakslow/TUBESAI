import java.util.ArrayList;
import java.util.Random;

/**
 * Kelas static yang menyediakan 3 metode parent selection.
 * Metode parent selection yang disediakan adalah Roulette Wheel Selection, Rank Selection, dan Tournament Selection
 *
 * @author Owen Lianggara
 * @author Andrew Kevin Alexander
 */
public class ParentSelection {
    /**
     * Metode Roulette Wheel Selection memilih 2 parent dengan bergantung pada fitness
     *
     * @param rand objek random dengan seed
     * @param populasi menyimpan Individu di ArrayList
     * @return parents, yaitu array of 2 Individu
     */
    public static Individu[] rouletteWheel(Random rand, ArrayList<Individu> populasi) {
        Individu[] parents = new Individu[2];
        double totalInverseFitness = 0;

        // Fitness terbaik = 0
        // Rumus invers dari fitness = 1 / (fitness + epsilon)
        double[] probs = new double[populasi.size()]; //probabilitas tiap individu

        for (int i = 0; i < populasi.size(); i++) {
            double fit = populasi.get(i).getFitness(); //ambil fitness
            double score = fit; // +0.1 menghindari div by zero jika solved
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

    /**
     * Metode Rank Selection memilih 2 parent dengan mengurutkan peringkat dari fitness
     *
     * @param rand objek random dengan seed
     * @param populasi menyimpan Individu di ArrayList
     * @return parents, yaitu array of 2 Individu
     */
    public static Individu[] rankSelection(Random rand, ArrayList<Individu> populasi) {
        Individu[] parents = new Individu[2];
        populasi.sort((idv1, idv2) -> idv1.compareTo(idv2));

        ArrayList<Double> beParentProbability = new ArrayList<>();

        //hitung total pembagi rank
        int sumRank = populasi.size() * (populasi.size() + 1) / 2;

        //hitung beParentProbability tiap individu
        for (int i = 0; i < populasi.size(); i++) {
            beParentProbability.add((double) (populasi.size() - i) / sumRank);
        }

        //cari 2 parent
        for (int n = 0; n < 2; n++) {
            int i = -1;
            double prob = rand.nextDouble();
            double sum = 0.0;

            //parent diambil jika probabilitasnya >= random
            do {
                i++;
                sum = sum + beParentProbability.get(i);
            } while (sum < prob);

            parents[n] = populasi.get(i);
        }
        return parents;
    }

    /**
     * Metode Tournamen Selection memilih 2 parent dengan memilih 10% calon parent dari populasi dan mengambil fitness tertinggi
     *
     * @param rand objek random dengan seed
     * @param populasi menyimpan Individu di ArrayList
     * @return parents, yaitu array of 2 Individu
     */
    public static Individu[] tournamentSelection(Random rand, ArrayList<Individu> populasi) {
        Individu[] parents = new Individu[2];

        // Pilih calon parent 10% dari populasi
        int maxRound = populasi.size()/10;

        for (int parent = 0; parent < 2; parent++) {
            //ambil calon parent sebanyak 10% populasi dan ambil individu dengan fitness terbesar
            for (int round = 0; round < maxRound; round++) {
                Individu calonParent = populasi.get(rand.nextInt(populasi.size()));

                if (parents[parent] == null || calonParent.compareTo(parents[parent]) < 0) {
                    parents[parent] = calonParent;
                }
            }
        }

        return parents;
    }
}
