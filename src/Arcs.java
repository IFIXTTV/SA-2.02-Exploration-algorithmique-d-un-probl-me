import java.util.ArrayList;
import java.util.List;

/**
 * Liste d'arcs sortants d'un nœud.
 */
public class Arcs {
    private List<Arc> liste;

    /** Crée une liste vide d'arcs. */
    public Arcs() {
        this.liste = new ArrayList<>();
    }

    /**
     * Ajoute un arc à la liste.
     * @param arc arc à ajouter
     */
    public void ajouter(Arc arc) {
        liste.add(arc);
    }

    /** @return la liste des arcs */
    public List<Arc> getListe() { return liste; }
}
