import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation d'un graphe par liste d'adjacence.
 */
public class GrapheListe implements Graphe {
    private List<String> noeuds;
    private List<Arcs> adjacence;

    /** Crée un graphe vide. */
    public GrapheListe() {
        this.noeuds = new ArrayList<>();
        this.adjacence = new ArrayList<>();
    }

    /**
     * Ajoute un nœud s'il n'existe pas déjà.
     * @param id identifiant du nœud
     */
    public void ajouterNoeud(String id) {
        if (!noeuds.contains(id)) {
            noeuds.add(id);
            adjacence.add(new Arcs());
        }
    }

    /**
     * Ajoute un arc entre deux nœuds (crée les nœuds si absents).
     * @param source identifiant du nœud source
     * @param destination identifiant du nœud destination
     * @param poids poids de l'arc
     */
    public void ajouterArc(String source, String destination, double poids) {
        ajouterNoeud(source);
        ajouterNoeud(destination);
        int idx = noeuds.indexOf(source);
        adjacence.get(idx).ajouter(new Arc(destination, poids));
    }

    @Override
    public List<String> getNoeuds() { return noeuds; }

    @Override
    public Arcs getAdjacents(String noeud) {
        int idx = noeuds.indexOf(noeud);
        if (idx == -1) return new Arcs();
        return adjacence.get(idx);
    }

    /** Affiche le graphe au format texte. */
    public void afficher() {
        for (String noeud : noeuds) {
            StringBuilder sb = new StringBuilder(noeud + " -> ");
            for (Arc arc : getAdjacents(noeud).getListe()) {
                sb.append(arc.getCible()).append("(").append((int) arc.getPoids()).append(") ");
            }
            System.out.println(sb.toString().trim());
        }
    }
}
