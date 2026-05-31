import java.util.List;

/**
 * Point d'entrée principal compatible avec la GUI.
 * Calcule le plus court chemin entre deux stations STAN via Dijkstra.
 */
public class Main {

    public static void main(String[] args)
    {
	if (args.length != 2) {
	    System.err.println("Erreur: usage java Main <STATION_DEPART> <STATION_ARRIVEE>");
	    System.exit(1);
	}
	else {
	    // On retourne un chemin contenant les deux arrêts sélectionnés
	    // (ce chemin est un exemple, il n'est pas valide car il ne vérifie pas que
	    //  le chemin en question utilise les arcs du graphe!)
	    String from  = args[0];
	    String to    = args[1];
	    String regex = "\\[|\\]";
	    String idFrom = from.split(regex)[1];
	    String idTo   = to.split(regex)[1];

        try {
            GrapheListe graphe = GrapheReader.charger("stan_edges.txt");
            Valeurs valeurs = Dijkstra.calculer(graphe, idFrom);
            List<String> chemin = Dijkstra.chemin(valeurs, idTo);

            if (chemin.size() <= 1 || !chemin.get(0).equals(idFrom)) {
                System.out.println(idFrom + ";" + idTo);
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < chemin.size(); i++) {
                    if (i > 0) sb.append(";");
                    sb.append(chemin.get(i));
                }
                System.out.println(sb.toString());
            }
        } catch (Exception e) {
            System.out.println(idFrom + ";" + idTo);
        }
	}
    }
}
