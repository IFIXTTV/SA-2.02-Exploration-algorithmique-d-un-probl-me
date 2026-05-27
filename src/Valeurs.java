import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Valeurs {
    Map<String, Double> valeur;
    Map<String, String> parent;

    public Valeurs() {
        this.valeur = new TreeMap<>();
        this.parent = new TreeMap<>();
    }

    public void setValeur(String nom, double valeur) {
        this.valeur.put(nom, valeur);
    }

    public void setParent(String nom, String parent) {
        this.parent.put(nom, parent);
    }

    public String getParent(String nom) {
        return this.parent.get(nom);
    }

    public double getValeur(String nom) {
        return this.valeur.get(nom);
    }

    public List<String> calculerChemin(String destination) {
        List<String> chemin = new ArrayList<>();
        String actuel = destination;
        while (actuel != null) {
            chemin.add(0, actuel);
            actuel = parent.get(actuel);
        }
        return chemin;
    }

    public String toString() {
        String res = "";
        for (String s : this.valeur.keySet()) {
            res += s + " -> V:" + valeur.get(s) + " p:" + parent.get(s) + "\n";
        }
        return res;
    }
}
