package io.shruubi.memtree;

public final class MemTreeNode<TKey extends Comparable<TKey>, TVal> {
    private TKey[] keys;
    private TVal[] values;
    private MemTreeNode<TKey, TVal>[] children;
    private boolean isLeaf;
    private int numKeys;

    public MemTreeNode(int degree) {
        //noinspection unchecked
        this.keys = (TKey[]) new Comparable[2 * degree - 1];
        //noinspection unchecked
        this.values = (TVal[]) new Object[2 * degree - 1];
        //noinspection unchecked
        this.children = new MemTreeNode[2 * degree];
        this.isLeaf = true;
        this.numKeys = 0;
    }

    public TKey[] getKeys() {
        return keys;
    }

    public void setKeys(TKey[] keys) {
        this.keys = keys;
    }

    public MemTreeNode<TKey, TVal>[] getChildren() {
        return children;
    }

    public void setKey(int index, TKey key) {
        keys[index] = key;
    }

    public TKey getKey(int index) {
        return keys[index];
    }

    public TVal[] getValues() {
        return values;
    }

    public void setValues(TVal[] values) {
        this.values = values;
    }

    public void setValue(int index, TVal value) {
        values[index] = value;
    }

    public TVal getValue(int index) {
        return values[index];
    }

    public void setChildren(MemTreeNode<TKey, TVal>[] children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getNumKeys() {
        return numKeys;
    }

    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }
}
