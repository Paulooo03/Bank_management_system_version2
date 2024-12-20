package com.example.bank_management_system;

import java.util.Iterator;

public class MyArrayList<T> implements Iterable<T> {
    private Object[] elements;
    private int size;

    public MyArrayList() {
        elements = new Object[10];  // Initial capacity of 10
        size = 0;
    }

    // Add an element to the list
    public void add(T element) {
        if (size == elements.length) {
            ensureCapacity();
        }
        elements[size++] = element;
    }

    // Add all elements from another array to this list
    public void addAll(MyArrayList<T> otherList) {
        for (int i = 0; i < otherList.size(); i++) {
            add((T) otherList.get(i));
        }
    }

    // Get an element by index
    public T get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) elements[index];
    }

    // Remove an element at a specific index
    public void remove(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        // Shift elements to the left
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null;  // Clear the last element
    }

    // Return the size of the list
    public int size() {
        return size;
    }

    // Check if the list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Clear the list
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    // Ensure that the list has enough capacity to hold more elements
    private void ensureCapacity() {
        int newCapacity = elements.length * 2;
        Object[] newArray = new Object[newCapacity];
        for (int i = 0; i < elements.length; i++) {
            newArray[i] = elements[i];
        }
        elements = newArray;
    }

    // Implement the iterator method required for Iterable
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new IndexOutOfBoundsException("No more elements");
                }
                return (T) elements[index++];
            }
        };
    }
}
