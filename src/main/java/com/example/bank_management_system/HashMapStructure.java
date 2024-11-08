package com.example.bank_management_system;

class Entry<K, V> {
    final K key;
    V value;
    Entry<K, V> next;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
}

public class HashMapStructure<K, V> {
    private Entry<K, V>[] table;
    private int capacity = 16; // Default capacity

    public HashMapStructure() {
        table = new Entry[capacity];
    }

    // Hash function to calculate index for a key
    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    // Put key-value pair into the map
    public void put(K key, V value) {
        int index = hash(key);
        Entry<K, V> newEntry = new Entry<>(key, value);

        if (table[index] == null) {
            table[index] = newEntry;
        } else {
            Entry<K, V> current = table[index];
            Entry<K, V> prev = null;
            while (current != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                prev = current;
                current = current.next;
            }
            prev.next = newEntry;
        }
    }

    // Get value by key
    public V get(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    // Remove key-value pair by key
    public void remove(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                return;
            }
            prev = current;
            current = current.next;
        }
    }

    // Check if key exists
    public boolean containsKey(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Print the hashmap
    public void printMap() {
        for (int i = 0; i < capacity; i++) {
            Entry<K, V> current = table[i];
            if (current != null) {
                System.out.print("Index " + i + ": ");
                while (current != null) {
                    System.out.print("{" + current.key + "=" + current.value + "} ");
                    current = current.next;
                }
                System.out.println();
            }
        }
    }
}
