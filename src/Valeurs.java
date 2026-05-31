import java.util.HashMap;
import java.util.Map;

/**
 * Représente la fonction de valeur L(X) pour chaque nœud du graphe.
 */
public class Valeurs {
    private Map<String, Double> distances;
    private Map<String, String> parents;

    /** @param noeuds liste des nœuds du graphe */
    public Valeurs(java.util.List<String> noeuds) {
        distances = new HashMap<>();
        parents = new HashMap<>();
        for (String n : noeuds) {
            distances.put(n, Double.MAX_VALUE);
            parents.put(n, null);
        }
    }

    /**
     * @param noeud identifiant du nœud
     * @return distance courante
     */
    public double getDistance(String noeud) { return distances.get(noeud); }

    /**
     * @param noeud identifiant du nœud
     * @param d nouvelle distance
     */
    public void setDistance(String noeud, double d) { distances.put(noeud, d); }

    /**
     * @param noeud identifiant du nœud
     * @return identifiant du parent
     */
    public String getParent(String noeud) { return parents.get(noeud); }

    /**
     * @param noeud identifiant du nœud
     * @param parent identifiant du parent
     */
    public void setParent(String noeud, String parent) { parents.put(noeud, parent); }
}
