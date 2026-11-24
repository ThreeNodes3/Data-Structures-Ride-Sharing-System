public class BSTNode<K extends Comparable<K>, T> {

    K key;
    T data;

    BSTNode<K, T> left;
    BSTNode<K, T> right;

    public BSTNode(K key, T data) {
        this.key = key;
        this.data = data;
        this.left = null;
        this.right = null;
    }
}