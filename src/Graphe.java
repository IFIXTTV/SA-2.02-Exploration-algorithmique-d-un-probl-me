import java.util.List;

public interface Graphe {
    List<String> getNoeuds();
    Arcs getAdjacents(String noeud);
}
