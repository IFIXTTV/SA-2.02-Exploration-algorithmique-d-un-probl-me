/**
 * Représente un arc orienté vers un nœud cible avec un poids.
 */
public class Arc {
    private String cible;
    private double poids;

    /**
     * @param cible identifiant du nœud cible
     * @param poids poids de l'arc
     */
    public Arc(String cible, double poids) {
        this.cible = cible;
        this.poids = poids;
    }

    /** @return identifiant du nœud cible */
    public String getCible() { return cible; }

    /** @return poids de l'arc */
    public double getPoids() { return poids; }
}
