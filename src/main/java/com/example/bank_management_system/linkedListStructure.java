package com.example.bank_management_system;

class Node<T> {
    T data;
    Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}

public class linkedListStructure<T> {
    private Node<T> top;

    public linkedListStructure() {
        this.top = null;
    }

    // Push a new element onto the stack
    public void push(T data) {
        Node<T> newNode = new Node<>(data);
        if (top == null) {
            top = newNode;
        } else {
            newNode.next = top;
            top = newNode;
        }
    }

    // Pop the top element from the stack
    public T pop() {
        if (top == null) {
            throw new IllegalStateException("Stack is empty");
        }
        T data = top.data;
        top = top.next;
        return data;
    }

    // Peek at the top element without removing it
    public T peek() {
        if (top == null) {
            throw new IllegalStateException("Stack is empty");
        }
        return top.data;
    }

    // Check if the stack is empty
    public boolean isEmpty() {
        return top == null;
    }

    // Print the stack
    public void printStack() {
        Node<T> current = top;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }
}
