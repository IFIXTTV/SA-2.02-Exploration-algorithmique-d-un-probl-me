import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GrapheListeTest {

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
    void testNoeuds() {
        GrapheListe g = grapheExemple();
        assertTrue(g.getNoeuds().containsAll(List.of("A", "B", "C", "D", "E")));
    }

    @Test
    void testAdjacents() {
        GrapheListe g = grapheExemple();
        assertEquals(2, g.getAdjacents("A").getListe().size());
    }

    @Test
    void testPoids() {
        GrapheListe g = grapheExemple();
        Arc premier = g.getAdjacents("A").getListe().get(0);
        assertEquals("B", premier.cible);
        assertEquals(12.0, premier.poids);
    }
}
