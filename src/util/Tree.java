package util;

import java.util.ArrayList;

/**
 * Created by AriApar on 26/11/2015.
 */
public class Tree<T> {

    private Node<T> root;

    public Tree(Node<T> root) {
        this.root = root;
    }

    public ArrayList<Node<T>> getNodesAtLevel(int level) {
        //level 0 = root
        ArrayList<Node<T>> currLevel = new ArrayList<>();
        currLevel.add(root);
        for (int i = 0; i <  level; i++) {
            ArrayList<Node<T>> nextLevel = new ArrayList<>();
            for (Node<T> currNode : currLevel) {
                nextLevel.addAll(currNode.getChildren());
            }
            currLevel = nextLevel;
        }
        return currLevel;
    }
}
