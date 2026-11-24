public class BST<K extends Comparable<K>, T>
        implements IBST<K, T> {

    private BSTNode<K, T> root;
    private BSTNode<K, T> current;
    private int count;

    public BST() {
        root = null;
        current = null;
        count = 0;
    }

    @Override
    public boolean empty() {
        return root == null;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean findKey(K key) {

        if (key == null) {
            return false;
        }

        BSTNode<K, T> temp = root;

        while (temp != null) {

            int comparison = key.compareTo(temp.key);

            if (comparison == 0) {
                current = temp;
                return true;
            }

            if (comparison < 0) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }
        }

        return false;
    }

    @Override
    public T retrieve() {

        if (current == null) {
            return null;
        }

        return current.data;
    }

    @Override
    public boolean insert(K key, T data) {

        if (key == null) {
            return false;
        }

        BSTNode<K, T> newNode =
                new BSTNode<K, T>(key, data);

        if (root == null) {
            root = newNode;
            current = newNode;
            count++;
            return true;
        }

        BSTNode<K, T> temp = root;
        BSTNode<K, T> parent = null;

        while (temp != null) {

            parent = temp;

            int comparison =
                    key.compareTo(temp.key);

            if (comparison == 0) {
                return false;
            }

            if (comparison < 0) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }
        }

        if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        current = newNode;
        count++;

        return true;
    }

    @Override
    public boolean removeKey(K key) {

        if (key == null) {
            return false;
        }

        BSTNode<K, T> temp = root;
        BSTNode<K, T> parent = null;

        while (temp != null
                && key.compareTo(temp.key) != 0) {

            parent = temp;

            if (key.compareTo(temp.key) < 0) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }
        }

        if (temp == null) {
            return false;
        }

        /* Node with two children */
        if (temp.left != null
                && temp.right != null) {

            BSTNode<K, T> successorParent = temp;
            BSTNode<K, T> successor = temp.right;

            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }

            temp.key = successor.key;
            temp.data = successor.data;

            if (successorParent == temp) {
                successorParent.right =
                        successor.right;
            } else {
                successorParent.left =
                        successor.right;
            }

            current = temp;
        }

        /* Node with zero or one child */
        else {

            BSTNode<K, T> child;

            if (temp.left != null) {
                child = temp.left;
            } else {
                child = temp.right;
            }

            if (parent == null) {
                root = child;
            } else if (parent.left == temp) {
                parent.left = child;
            } else {
                parent.right = child;
            }

            current = child;
        }

        count--;

        return true;
    }

    @Override
    public LinkedList<T> inOrder() {

        LinkedList<T> result =
                new LinkedList<T>();

        inOrderRecursive(root, result);

        return result;
    }

    private void inOrderRecursive(
            BSTNode<K, T> node,
            LinkedList<T> result) {

        if (node == null) {
            return;
        }

        inOrderRecursive(node.left, result);

        result.insert(node.data);

        inOrderRecursive(node.right, result);
    }

    @Override
    public LinkedList<T> rangeSearch(
            K low, K high) {

        LinkedList<T> result =
                new LinkedList<T>();

        if (low == null || high == null) {
            return result;
        }

        if (low.compareTo(high) > 0) {
            return result;
        }

        rangeSearchRecursive(
                root,
                low,
                high,
                result
        );

        return result;
    }

    private void rangeSearchRecursive(
            BSTNode<K, T> node,
            K low,
            K high,
            LinkedList<T> result) {

        if (node == null) {
            return;
        }

        if (low.compareTo(node.key) < 0) {

            rangeSearchRecursive(
                    node.left,
                    low,
                    high,
                    result
            );
        }

        if (low.compareTo(node.key) <= 0
                && high.compareTo(node.key) >= 0) {

            result.insert(node.data);
        }

        if (high.compareTo(node.key) > 0) {

            rangeSearchRecursive(
                    node.right,
                    low,
                    high,
                    result
            );
        }
    }

    @Override
    public int height() {
        return heightRecursive(root);
    }

    private int heightRecursive(
            BSTNode<K, T> node) {

        if (node == null) {
            return 0;
        }

        int leftHeight =
                heightRecursive(node.left);

        int rightHeight =
                heightRecursive(node.right);

        if (leftHeight > rightHeight) {
            return leftHeight + 1;
        }

        return rightHeight + 1;
    }

    @Override
    public int nbKeyComp(K key) {

        if (key == null) {
            return 0;
        }

        int comparisons = 0;

        BSTNode<K, T> temp = root;

        while (temp != null) {

            comparisons++;

            int comparison =
                    key.compareTo(temp.key);

            if (comparison == 0) {
                return comparisons;
            }

            if (comparison < 0) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }
        }

        return comparisons;
    }
}