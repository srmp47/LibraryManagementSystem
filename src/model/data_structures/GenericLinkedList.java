package model.data_structures;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

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
                tail = null; // List became empty
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

    public void sort(Comparator<T> comparator) {
        if (size <= 1) return;

        @SuppressWarnings("unchecked")
        T[] array = (T[]) new Object[size];

        Node<T> current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.getData();
            current = current.getNext();
        }

        mergeSort(array, 0, size - 1, comparator);

        current = head;
        for (int i = 0; i < size; i++) {
            current.setData(array[i]);
            current = current.getNext();
        }
    }

    private void mergeSort(T[] array, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(array, left, mid, comparator);
            mergeSort(array, mid + 1, right, comparator);

            merge(array, left, mid, right, comparator);
        }
    }

    private void merge(T[] array, int left, int mid, int right, Comparator<T> comparator) {
        int leftSize = mid - left + 1;
        int rightSize = right - mid;

        @SuppressWarnings("unchecked")
        T[] leftArray = (T[]) new Object[leftSize];
        @SuppressWarnings("unchecked")
        T[] rightArray = (T[]) new Object[rightSize];

        System.arraycopy(array, left, leftArray, 0, leftSize);
        System.arraycopy(array, mid + 1, rightArray, 0, rightSize);

        int i = 0, j = 0, k = left;

        while (i < leftSize && j < rightSize) {
            if (comparator.compare(leftArray[i], rightArray[j]) <= 0) {
                array[k++] = leftArray[i++];
            } else {
                array[k++] = rightArray[j++];
            }
        }

        while (i < leftSize) {
            array[k++] = leftArray[i++];
        }

        while (j < rightSize) {
            array[k++] = rightArray[j++];
        }
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
}