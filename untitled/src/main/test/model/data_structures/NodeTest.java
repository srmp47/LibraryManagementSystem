package test.model.data_structures;

import model.data_structures.Node;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void testNodeCreation() {
        Node<String> node = new Node<>("Test Data");

        assertEquals("Test Data", node.getData());
        assertNull(node.getNext());
    }

    @Test
    void testSetData() {
        Node<Integer> node = new Node<>(10);
        assertEquals(10, node.getData());

        node.setData(20);
        assertEquals(20, node.getData());
    }

    @Test
    void testSetNext() {
        Node<String> first = new Node<>("First");
        Node<String> second = new Node<>("Second");

        first.setNext(second);

        assertEquals(second, first.getNext());
        assertEquals("Second", first.getNext().getData());
    }
}