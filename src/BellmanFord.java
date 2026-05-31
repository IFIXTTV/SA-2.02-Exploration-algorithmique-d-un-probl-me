import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Algorithme du point fixe (Bellman-Ford) pour le plus court chemin.
 */
public class BellmanFord {

    /**
     * Calcule les plus courts chemins depuis un nœud de départ.
     * @param graphe le graphe
     * @param depart nœud de départ
     * @return valeurs (distances et parents)
     */
    public static Valeurs calculer(Graphe graphe, String depart) {
        Valeurs valeurs = new Valeurs(graphe.getNoeuds());
        valeurs.setDistance(depart, 0.0);

        boolean modifie = true;
        while (modifie) {
            modifie = false;
            for (String noeud : graphe.getNoeuds()) {
                double distNoeud = valeurs.getDistance(noeud);
                if (distNoeud == Double.MAX_VALUE) continue;
                for (Arc arc : graphe.getAdjacents(noeud).getListe()) {
                    double nouvelleVal = distNoeud + arc.getPoids();
                    if (nouvelleVal < valeurs.getDistance(arc.getCible())) {
                        valeurs.setDistance(arc.getCible(), nouvelleVal);
                        valeurs.setParent(arc.getCible(), noeud);
                        modifie = true;
                    }
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
