package util;

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

    public void addChildWithData(T data) {
        children.add(new Node<T>(data));
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

}
