public class BellmanFord {

    public Valeurs resoudre(Graphe g, String depart) {
        Valeurs v = new Valeurs();
        for (String n : g.getNoeuds()) {
            v.setValeur(n, Double.MAX_VALUE);
            v.setParent(n, null);
        }
        v.setValeur(depart, 0);

        boolean modifie = true;
        while (modifie) {
            modifie = false;
            for (String n : g.getNoeuds()) {
                if (v.getValeur(n) == Double.MAX_VALUE) continue;
                for (Arc a : g.getAdjacents(n).getListe()) {
                    double nouv = v.getValeur(n) + a.poids;
                    if (nouv < v.getValeur(a.cible)) {
                        v.setValeur(a.cible, nouv);
                        v.setParent(a.cible, n);
                        modifie = true;
                    }
                }
            }
        }
        return v;
    }
}
