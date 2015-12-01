package util;

import Model.Election;

import java.util.ArrayList;

/**
 * Created by AriApar on 26/11/2015.
 */
public class Node<T> {

    private T data;
    private ArrayList<Node<T>> children;

    public Node(T data) {
        this(data, new ArrayList<Node<T>>());
    }

    public Node(T data, ArrayList<Node<T>> children) {
        this.data = data;
        this.children = children;
    }

    public T getData() {
        return data;
    }

    public Node<T> addChildWithData(T data) {
        Node<T> child = new Node<T>(data); children.add(child); return child;
    }

    public void addChild(Node<T> childNode) {
        children.add(childNode);
    }

    public boolean hasChild() {
        return children.size() > 0;
    }

    public ArrayList<Node<T>> getChildren(){
        return children;
    }

    public void removeChild(Node<T> child) { children.remove(child); }

}
