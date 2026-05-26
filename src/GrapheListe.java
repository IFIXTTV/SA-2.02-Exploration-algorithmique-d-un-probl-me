import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrapheListe implements Graphe {
    List<String> noeuds;
    Map<String, Arcs> adjacence;

    public GrapheListe() {
        this.noeuds = new ArrayList<>();
        this.adjacence = new HashMap<>();
    }

    public List<String> getNoeuds() {
        return noeuds;
    }

    public Arcs getAdjacents(String noeud) {
        return adjacence.getOrDefault(noeud, new Arcs());
    }

    public void ajouterArc(String source, String dest, double poids) {
        if (!noeuds.contains(source)) {
            noeuds.add(source);
            adjacence.put(source, new Arcs());
        }
        if (!noeuds.contains(dest)) {
            noeuds.add(dest);
            adjacence.put(dest, new Arcs());
        }
        adjacence.get(source).ajouter(new Arc(dest, poids));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String n : noeuds) {
            sb.append(n).append(" -> ");
            for (Arc a : adjacence.get(n).getListe()) {
                sb.append(a.cible).append("(").append((int) a.poids).append(") ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
