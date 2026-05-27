import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DijkstraTest {

    GrapheListe grapheExemple() {
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

    @Test
    void testDistanceC() {
        Valeurs v = new Dijkstra().resoudre(grapheExemple(), "A");
        assertEquals(76.0, v.getValeur("C"));
    }

    @Test
    void testCheminC() {
        Valeurs v = new Dijkstra().resoudre(grapheExemple(), "A");
        assertEquals(List.of("A", "B", "E", "D", "C"), v.calculerChemin("C"));
    }

    @Test
    void testParentD() {
        Valeurs v = new Dijkstra().resoudre(grapheExemple(), "A");
        assertEquals("E", v.getParent("D"));
    }
}
