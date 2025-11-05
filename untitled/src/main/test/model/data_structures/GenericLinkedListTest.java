package test.model.data_structures;

import model.data_structures.GenericLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Comparator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericLinkedListTest {
    private GenericLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new GenericLinkedList<>();
    }

    @Test
    void testAddAndSize() {
        assertEquals(0, list.getSize());
        assertTrue(list.isEmpty());

        list.add("First");
        list.add("Second");

        assertEquals(2, list.getSize());
        assertFalse(list.isEmpty());
    }

    @Test
    void testRemove() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        assertTrue(list.remove("Second"));
        assertEquals(2, list.getSize());

        assertFalse(list.remove("NonExistent"));
        assertEquals(2, list.getSize());

        assertTrue(list.remove("First"));
        assertTrue(list.remove("Third"));
        assertTrue(list.isEmpty());
    }

    @Test
    void testIterator() {
        list.add("A");
        list.add("B");
        list.add("C");

        int count = 0;
        for (String item : list) {
            assertNotNull(item);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testSort() {
        list.add("Charlie");
        list.add("Alpha");
        list.add("Bravo");

        list.sort(Comparator.naturalOrder());

        String[] expected = {"Alpha", "Bravo", "Charlie"};
        String[] actual = list.toArray(new String[0]);

        assertArrayEquals(expected, actual);
    }

    @Test
    void testFilter() {
        list.add("Apple");
        list.add("Banana");
        list.add("Cherry");
        list.add("Date");

        GenericLinkedList<String> filtered = list.filter(s -> s.startsWith("A") || s.startsWith("B"));

        assertEquals(2, filtered.getSize());
        assertTrue(filtered.stream().anyMatch(s -> s.equals("Apple")));
        assertTrue(filtered.stream().anyMatch(s -> s.equals("Banana")));
        assertFalse(filtered.stream().anyMatch(s -> s.equals("Cherry")));
    }

    @Test
    void testToList() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        List<String> javaList = list.toList();

        assertEquals(3, javaList.size());
        assertEquals("First", javaList.get(0));
        assertEquals("Second", javaList.get(1));
        assertEquals("Third", javaList.get(2));
    }

    @Test
    void testToArray() {
        list.add("One");
        list.add("Two");

        String[] array = list.toArray(new String[0]);

        assertEquals(2, array.length);
        assertEquals("One", array[0]);
        assertEquals("Two", array[1]);
    }
}