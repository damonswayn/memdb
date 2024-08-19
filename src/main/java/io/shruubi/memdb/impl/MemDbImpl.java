package io.shruubi.memdb.impl;

import io.shruubi.memdb.MemDbInterface;
import io.shruubi.memtree.MemTree;

public final class MemDbImpl implements MemDbInterface {
    private final MemTree<String, String> tree;

    public MemDbImpl() {
        this.tree = new MemTree<>(MemTree.DEFAULT_DEGREE);
    }

    @Override
    public void set(String key, String value) {
        this.tree.insert(key, value);
    }

    @Override
    public String get(String key) {
        return this.tree.search(key);
    }

    @Override
    public void delete(String key) {
        this.tree.delete(key);
    }
}
