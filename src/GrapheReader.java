import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Chargement d'un graphe depuis les fichiers stan_nodes.txt et stan_edges.txt.
 */
public class GrapheReader {

    /**
     * Charge un GrapheListe depuis les fichiers de nœuds et d'arcs.
     * @param edgesFile chemin vers le fichier d'arcs (format: source;destination;poids)
     * @return le graphe chargé
     * @throws IOException en cas d'erreur de lecture
     */
    public static GrapheListe charger(String edgesFile) throws IOException {
        GrapheListe graphe = new GrapheListe();
        try (BufferedReader br = new BufferedReader(new FileReader(edgesFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    graphe.ajouterArc(parts[0], parts[1], Double.parseDouble(parts[2]));
                }
            }
        }
        return graphe;
    }
}
