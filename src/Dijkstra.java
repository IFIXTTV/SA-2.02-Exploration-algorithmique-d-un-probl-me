import java.util.ArrayList;
import java.util.List;

public class Dijkstra {

    public Valeurs resoudre(Graphe g, String depart) {
        Valeurs v = new Valeurs();
        List<String> Q = new ArrayList<>();

        for (String n : g.getNoeuds()) {
            v.setValeur(n, Double.MAX_VALUE);
            v.setParent(n, null);
            Q.add(n);
        }
        v.setValeur(depart, 0);

        while (!Q.isEmpty()) {
            String u = null;
            for (String n : Q) {
                if (u == null || v.getValeur(n) < v.getValeur(u)) u = n;
            }
            Q.remove(u);

            if (v.getValeur(u) == Double.MAX_VALUE) break;

            for (Arc a : g.getAdjacents(u).getListe()) {
                if (!Q.contains(a.cible)) continue;
                double d = v.getValeur(u) + a.poids;
                if (d < v.getValeur(a.cible)) {
                    v.setValeur(a.cible, d);
                    v.setParent(a.cible, u);
                }
            }
        }
        return v;
    }
}
