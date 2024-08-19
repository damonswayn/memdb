package io.shruubi.memtree;

public interface MemTreeInterface<TKey extends Comparable<TKey>, TVal> {
    MemTreeNode<TKey, TVal> getRoot();

    void insert(TKey key, TVal value);

    TVal search(TKey key);

    void delete(TKey key);
}
