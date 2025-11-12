package library.models.data_structures;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GenericLinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public GenericLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    public boolean remove(T data) {
        if (head == null) return false;

        if (head.getData().equals(data)) {
            head = head.getNext();
            if (head == null) {
                tail = null;
            }
            size--;
            return true;
        }

        Node<T> current = head;
        while (current.getNext() != null && !current.getNext().getData().equals(data)) {
            current = current.getNext();
        }

        if (current.getNext() != null) {
            Node<T> nodeToRemove = current.getNext();
            if (nodeToRemove == tail) {
                tail = current;
            }
            current.setNext(nodeToRemove.getNext());
            size--;
            return true;
        }

        return false;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void updateTail() {
        if (head == null) {
            tail = null;
            return;
        }

        Node<T> current = head;
        while (current.getNext() != null) {
            current = current.getNext();
        }
        tail = current;
    }

    public void sort(Comparator<T> comparator) {
        if (size <= 1) return;
        head = mergeSort(head, comparator);
        updateTail();
    }


    private Node<T> mergeSort(Node<T> head, Comparator<T> comparator) {
        if (head == null || head.getNext() == null) {
            return head;
        }

        Node<T> middle = getMiddle(head);
        Node<T> nextOfMiddle = middle.getNext();
        middle.setNext(null);

        Node<T> left = mergeSort(head, comparator);
        Node<T> right = mergeSort(nextOfMiddle, comparator);

        return merge(left, right, comparator);
    }

    private Node<T> getMiddle(Node<T> head) {
        if (head == null) return null;

        Node<T> slow = head;
        Node<T> fast = head;

        while (fast.getNext() != null && fast.getNext().getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }

        return slow;
    }

    private Node<T> merge(Node<T> left, Node<T> right, Comparator<T> comparator) {
        Node<T> dummy = new Node<>(null);
        Node<T> current = dummy;


        while (left != null && right != null) {
            if (comparator.compare(left.getData(), right.getData()) <= 0) {
                current.setNext(left);
                left = left.getNext();
            } else {
                current.setNext(right);
                right = right.getNext();
            }
            current = current.getNext();
        }


        if (left != null) {
            current.setNext(left);
        } else {
            current.setNext(right);
        }

        return dummy.getNext();
    }

    public GenericLinkedList<T> filter(Predicate<T> predicate) {
        GenericLinkedList<T> result = new GenericLinkedList<>();
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.getData())) {
                result.add(current.getData());
            }
            current = current.getNext();
        }
        return result;
    }

    public Stream<T> stream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED),
                false
        );
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                T data = current.getData();
                current = current.getNext();
                return data;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        if (array.length < size) {
            array = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), size);
        }

        Node<T> current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.getData();
            current = current.getNext();
        }

        if (array.length > size) {
            array[size] = null;
        }

        return array;
    }
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.getData());
            current = (current).getNext();
        }
        return list;
    }
}