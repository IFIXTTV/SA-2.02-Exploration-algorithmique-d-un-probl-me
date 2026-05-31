import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Algorithme de Dijkstra pour le plus court chemin.
 */
public class Dijkstra {

    /**
     * Calcule les plus courts chemins depuis un nœud de départ.
     * @param graphe le graphe
     * @param depart nœud de départ
     * @return valeurs (distances et parents)
     */
    public static Valeurs calculer(Graphe graphe, String depart) {
        Valeurs valeurs = new Valeurs(graphe.getNoeuds());
        valeurs.setDistance(depart, 0.0);
        Set<String> visites = new HashSet<>();
        Map<String, Double> nonVisites = new HashMap<>();
        for (String n : graphe.getNoeuds()) nonVisites.put(n, valeurs.getDistance(n));

        while (!nonVisites.isEmpty()) {
            String courant = null;
            double minDist = Double.MAX_VALUE;
            for (Map.Entry<String, Double> e : nonVisites.entrySet()) {
                if (e.getValue() < minDist) {
                    minDist = e.getValue();
                    courant = e.getKey();
                }
            }
            if (courant == null) break;
            nonVisites.remove(courant);
            visites.add(courant);

            for (Arc arc : graphe.getAdjacents(courant).getListe()) {
                if (visites.contains(arc.getCible())) continue;
                double nouvelleVal = valeurs.getDistance(courant) + arc.getPoids();
                if (nouvelleVal < valeurs.getDistance(arc.getCible())) {
                    valeurs.setDistance(arc.getCible(), nouvelleVal);
                    valeurs.setParent(arc.getCible(), courant);
                    nonVisites.put(arc.getCible(), nouvelleVal);
                }
            }
        }
        return valeurs;
    }

    /**
     * Reconstruit le chemin depuis le départ jusqu'à l'arrivée.
     * @param valeurs résultat de l'algorithme
     * @param arrivee nœud d'arrivée
     * @return liste des nœuds du chemin
     */
    public static List<String> chemin(Valeurs valeurs, String arrivee) {
        List<String> chemin = new ArrayList<>();
        String courant = arrivee;
        while (courant != null) {
            chemin.add(courant);
            courant = valeurs.getParent(courant);
        }
        Collections.reverse(chemin);
        return chemin;
    }
}
