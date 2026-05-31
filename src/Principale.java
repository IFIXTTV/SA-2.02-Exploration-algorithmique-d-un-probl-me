/**
 * Programme principal créant le graphe exemple du sujet (Figure 1).
 */
public class Principale {

    /** @param args arguments ignorés */
    public static void main(String[] args) {
        GrapheListe graphe = new GrapheListe();
        graphe.ajouterArc("A", "B", 12);
        graphe.ajouterArc("A", "D", 87);
        graphe.ajouterArc("B", "E", 11);
        graphe.ajouterArc("C", "A", 19);
        graphe.ajouterArc("D", "B", 23);
        graphe.ajouterArc("D", "C", 10);
        graphe.ajouterArc("E", "D", 43);

        graphe.afficher();
    }
}
