import java.util.ArrayList;
import java.util.Random;

public class ParentSelection {
    public static Individu[] rouletteWheel(Random rand, ArrayList<Individu> populasi) {
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

    public static Individu[] rankSelection(Random rand, ArrayList<Individu> populasi) {
        Individu[] parents = new Individu[2];
        populasi.sort((idv1, idv2) -> idv1.compareTo(idv2));

        ArrayList<Double> beParentProbability = new ArrayList<>();

        //hitung total pembagi rank
        int sumRank = populasi.size() * (populasi.size() + 1) / 2;

        //calc beParentProbability
        for (int i = 0; i < populasi.size(); i++) {
            beParentProbability.add((double) (i + 1) / sumRank);
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

    public static Individu[] tournamentSelection(Random rand, ArrayList<Individu> populasi) {
        Individu[] parents = new Individu[2];

        // Pilih calon parent 10% dari populasi
        int maxRound = populasi.size()/10;
//        maxRound = 10;
        for (int parent = 0; parent < 2; parent++) {
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
