import java.util.List;

/**
 * Tests unitaires pour GrapheListe, BellmanFord et Dijkstra.
 */
public class TestGraphe {

    private static int ok = 0;
    private static int ko = 0;

    private static void assertTrue(String nom, boolean condition) {
        if (condition) {
            System.out.println("[OK] " + nom);
            ok++;
        } else {
            System.out.println("[ECHEC] " + nom);
            ko++;
        }
    }

    private static GrapheListe grapheExemple() {
        GrapheListe g = new GrapheListe();
        g.ajouterArc("A", "B", 12);
        g.ajouterArc("A", "D", 87);
        g.ajouterArc("B", "E", 11);
        g.ajouterArc("C", "A", 19);
        g.ajouterArc("D", "B", 23);
        g.ajouterArc("D", "C", 10);
        g.ajouterArc("E", "D", 43);
        return g;
    }

    /** @param args arguments ignorés */
    public static void main(String[] args) {
        GrapheListe g = grapheExemple();

        assertTrue("Graphe contient 5 noeuds", g.getNoeuds().size() == 5);
        assertTrue("A a 2 arcs sortants", g.getAdjacents("A").getListe().size() == 2);
        assertTrue("B a 1 arc sortant", g.getAdjacents("B").getListe().size() == 1);
        assertTrue("Arc A->B poids 12", g.getAdjacents("A").getListe().get(0).getPoids() == 12.0);
        assertTrue("Arc A->D poids 87", g.getAdjacents("A").getListe().get(1).getPoids() == 87.0);

        Valeurs vBF = BellmanFord.calculer(g, "A");
        assertTrue("BF: distance A->A = 0", vBF.getDistance("A") == 0.0);
        assertTrue("BF: distance A->B = 12", vBF.getDistance("B") == 12.0);
        assertTrue("BF: distance A->E = 23", vBF.getDistance("E") == 23.0);
        assertTrue("BF: distance A->D = 66", vBF.getDistance("D") == 66.0);
        assertTrue("BF: distance A->C = 76", vBF.getDistance("C") == 76.0);

        List<String> cheminBF = BellmanFord.chemin(vBF, "C");
        assertTrue("BF: chemin A->C taille 5", cheminBF.size() == 5);
        assertTrue("BF: chemin commence par A", cheminBF.get(0).equals("A"));
        assertTrue("BF: chemin finit par C", cheminBF.get(cheminBF.size() - 1).equals("C"));

        Valeurs vD = Dijkstra.calculer(g, "A");
        assertTrue("Dijkstra: distance A->C = 76", vD.getDistance("C") == 76.0);
        assertTrue("Dijkstra: distance A->D = 66", vD.getDistance("D") == 66.0);

        List<String> cheminD = Dijkstra.chemin(vD, "C");
        assertTrue("Dijkstra: chemin A->C taille 5", cheminD.size() == 5);

        System.out.println("\nResultat: " + ok + " OK, " + ko + " ECHEC");
    }
}
