import java.util.List;

/**
 * Interface représentant un graphe orienté pondéré.
 */
public interface Graphe {
    /** @return la liste des identifiants des nœuds */
    List<String> getNoeuds();

    /**
     * @param noeud identifiant du nœud
     * @return la liste d'adjacence du nœud
     */
    Arcs getAdjacents(String noeud);
}
