import java.util.ArrayList;
import java.util.List;

public class Arcs {
    private List<Arc> liste;

    public Arcs() {
        this.liste = new ArrayList<>();
    }

    public void ajouter(Arc a) {
        liste.add(a);
    }

    public List<Arc> getListe() {
        return liste;
    }
}
