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
}
