package io.shruubi.memtree;

public class MemTree<TKey extends Comparable<TKey>, TVal> implements MemTreeInterface<TKey, TVal> {
    public static final int DEFAULT_DEGREE = 5;

    private MemTreeNode<TKey, TVal> root;
    private int degree;

    public MemTree(int degree) {
        this.root = new MemTreeNode<>(degree);
        this.degree = degree;
    }

    public MemTreeNode<TKey, TVal> getRoot() {
        return this.root;
    }

    @Override
    public void insert(TKey tKey, TVal value) {
        MemTreeNode<TKey, TVal> r = this.root;
        TVal existingValue = this.search(tKey);
        if (existingValue != null) {
            this.updateValue(r, tKey, value);
        } else {
            if (r.getNumKeys() == 2 * degree - 1) {
                MemTreeNode<TKey, TVal> s = new MemTreeNode<>(degree);
                s.setIsLeaf(false);
                s.setNumKeys(0);
                s.getChildren()[0] = r;
                this.setRoot(s);
                this.splitChild(s, 0);
                this.insertNonFull(s, tKey, value);
            } else {
                this.insertNonFull(r, tKey, value);
            }
        }
    }

    @Override
    public TVal search(TKey tKey) {
        return this.searchInternal(this.getRoot(), tKey);
    }

    @Override
    public void delete(TKey tKey) {
        if (this.getRoot() == null) {
            return;
        }

        delete(this.getRoot(), tKey);

        if (this.getRoot().getNumKeys() == 0) {
            if (this.getRoot().isLeaf()) {
                this.setRoot(null);
            } else {
                this.setRoot(this.getRoot().getChildren()[0]);
            }
        }
    }

    public void setRoot(MemTreeNode<TKey, TVal> root) {
        this.root = root;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    private void insertNonFull(MemTreeNode<TKey, TVal> node, TKey tKey, TVal value) {
        int i = node.getNumKeys() - 1;

        if (node.isLeaf()) {
            while (i >= 0 && tKey.compareTo(node.getKey(i)) < 0) {
                node.setKey(i + 1, node.getKey(i));
                node.setValue(i + 1, node.getValue(i));
                i--;
            }

            node.setKey(i + 1, tKey);
            node.setValue(i + 1, value);
            node.setNumKeys(node.getNumKeys() + 1);
        } else {
            while (i >= 0 && tKey.compareTo(node.getKey(i)) < 0) {
                i--;
            }

            i++;

            if (node.getChildren()[i].getNumKeys() == 2 * degree - 1) {
                this.splitChild(node, i);
                if (tKey.compareTo(node.getKey(i)) > 0) {
                    i++;
                }
            }

            this.insertNonFull(node.getChildren()[i], tKey, value);
        }
    }

    private void splitChild(MemTreeNode<TKey, TVal> parent, int i) {
        MemTreeNode<TKey, TVal> fullChild = parent.getChildren()[i];
        MemTreeNode<TKey, TVal> newChild = new MemTreeNode<>(degree);

        newChild.setIsLeaf(fullChild.isLeaf());
        newChild.setNumKeys(degree - 1);

        for (int j = 0; j < degree - 1; j++) {
            newChild.setKey(j, fullChild.getKey(j + degree));
            newChild.setValue(j, fullChild.getValue(j + degree));
        }

        if (!fullChild.isLeaf()) {
            if (degree >= 0) {
                System.arraycopy(fullChild.getChildren(), degree, newChild.getChildren(), 0, degree);
            }
        }

        fullChild.setNumKeys(degree - 1);

        for (int j = parent.getNumKeys(); j >= i + 1; j--) {
            parent.getChildren()[j + 1] = parent.getChildren()[j];
        }

        parent.getChildren()[i + 1] = newChild;

        for (int j = parent.getNumKeys() - 1; j >= i; j--) {
            parent.setKey(j + 1, parent.getKey(j));
            parent.setValue(j + 1, parent.getValue(j));
        }

        parent.setKey(i, fullChild.getKey(degree - 1));
        parent.setValue(i, fullChild.getValue(degree - 1));
        parent.setNumKeys(parent.getNumKeys() + 1);
    }

    private TVal searchInternal(MemTreeNode<TKey, TVal> node, TKey tKey) {
        if (node == null) {
            return null;
        }

        if (tKey == null) {
            return null;
        }

        int i = 0;
        while (i < node.getNumKeys() && tKey.compareTo(node.getKey(i)) > 0) {
            i++;
        }

        if (i < node.getNumKeys() && tKey.compareTo(node.getKey(i)) == 0) {
            return node.getValue(i);
        }

        if (node.isLeaf()) {
            return null;
        } else {
            return searchInternal(node.getChildren()[i], tKey);
        }
    }

    private void delete(MemTreeNode<TKey, TVal> node, TKey key) {
        int idx = this.findKey(node, key);

        if (idx < node.getNumKeys() && node.getKey(idx).compareTo(key) == 0) {
            if (node.isLeaf()) {
                this.removeFromLeaf(node, idx);
            } else {
                this.removeFromNonLeaf(node, idx);
            }
        } else {
            if (node.isLeaf()) {
                return;
            }

            boolean flag = (idx == node.getNumKeys());

            if (node.getChildren()[idx].getNumKeys() < degree) {
                this.fill(node, idx);
            }

            if (flag && idx > node.getNumKeys()) {
                this.delete(node.getChildren()[idx - 1], key);
            } else {
                this.delete(node.getChildren()[idx], key);
            }
        }
    }

    private int findKey(MemTreeNode<TKey, TVal> node, TKey key) {
        int idx = 0;

        while (idx < node.getNumKeys() && node.getKey(idx).compareTo(key) < 0) {
            idx++;
        }

        return idx;
    }

    private void removeFromLeaf(MemTreeNode<TKey, TVal> node, int idx) {
        for (int i = idx + 1; i < node.getNumKeys(); ++i) {
            node.setKey(i - 1, node.getKey(i));
            node.setValue(i - 1, node.getValue(i));
        }

        node.setNumKeys(node.getNumKeys() - 1);
    }

    private void removeFromNonLeaf(MemTreeNode<TKey, TVal> node, int idx) {
        TKey key = node.getKey(idx);

        if (node.getChildren()[idx].getNumKeys() >= degree) {
            TKey pred = getPredecessor(node, idx);
            TVal predVal = getPredecessorValue(node, idx);
            node.setKey(idx, pred);
            node.setValue(idx, predVal);
            this.delete(node.getChildren()[idx], pred);
        } else if (node.getChildren()[idx + 1].getNumKeys() >= degree) {
            TKey succ = getSuccessor(node, idx);
            TVal succVal = getSuccessorValue(node, idx);
            node.setKey(idx, succ);
            node.setValue(idx, succVal);
            this.delete(node.getChildren()[idx + 1], succ);
        } else {
            merge(node, idx);
            this.delete(node.getChildren()[idx], key);
        }
    }

    private TKey getPredecessor(MemTreeNode<TKey, TVal> node, int idx) {
        MemTreeNode<TKey, TVal> cur = node.getChildren()[idx];

        while (!cur.isLeaf()) {
            cur = cur.getChildren()[cur.getNumKeys()];
        }

        return cur.getKey(cur.getNumKeys() - 1);
    }

    private TVal getPredecessorValue(MemTreeNode<TKey, TVal> node, int idx) {
        MemTreeNode<TKey, TVal> cur = node.getChildren()[idx];

        while (!cur.isLeaf()) {
            cur = cur.getChildren()[cur.getNumKeys()];
        }

        return cur.getValue(cur.getNumKeys() - 1);
    }

    private TKey getSuccessor(MemTreeNode<TKey, TVal> node, int idx) {
        MemTreeNode<TKey, TVal> cur = node.getChildren()[idx + 1];

        while (!cur.isLeaf()) {
            cur = cur.getChildren()[0];
        }

        return cur.getKey(0);
    }

    private TVal getSuccessorValue(MemTreeNode<TKey, TVal> node, int idx) {
        MemTreeNode<TKey, TVal> cur = node.getChildren()[idx + 1];

        while (!cur.isLeaf()) {
            cur = cur.getChildren()[0];
        }

        return cur.getValue(0);
    }

    private void fill(MemTreeNode<TKey, TVal> node, int idx) {
        if (idx != 0 && node.getChildren()[idx - 1].getNumKeys() >= degree) {
            this.borrowFromPrev(node, idx);
        } else if (idx != node.getNumKeys() && node.getChildren()[idx + 1].getNumKeys() >= degree) {
            this.borrowFromNext(node, idx);
        } else {
            if (idx != node.getNumKeys()) {
                this.merge(node, idx);
            } else {
                this.merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(MemTreeNode<TKey, TVal> node, int idx) {
        MemTreeNode<TKey, TVal> child = node.getChildren()[idx];
        MemTreeNode<TKey, TVal> sibling = node.getChildren()[idx - 1];

        for (int i = child.getNumKeys() - 1; i >= 0; --i) {
            child.setKey(i + 1, child.getKey(i));
            child.setValue(i + 1, child.getValue(i));
        }

        if (!child.isLeaf()) {
            for (int i = child.getNumKeys(); i >= 0; --i) {
                child.getChildren()[i + 1] = child.getChildren()[i];
            }
        }

        child.setKey(0, node.getKey(idx - 1));
        child.setValue(0, node.getValue(idx - 1));

        if (!node.isLeaf()) {
            child.getChildren()[0] = sibling.getChildren()[sibling.getNumKeys()];
        }

        node.setKey(idx - 1, sibling.getKey(sibling.getNumKeys() - 1));
        node.setValue(idx - 1, sibling.getValue(sibling.getNumKeys() - 1));

        child.setNumKeys(child.getNumKeys() + 1);
        sibling.setNumKeys(sibling.getNumKeys() - 1);
    }

    private void borrowFromNext(MemTreeNode<TKey, TVal> node, int idx) {
        MemTreeNode<TKey, TVal> child = node.getChildren()[idx];
        MemTreeNode<TKey, TVal> sibling = node.getChildren()[idx + 1];

        child.setKey(child.getNumKeys(), node.getKey(idx));
        child.setValue(child.getNumKeys(), node.getValue(idx));

        if (!child.isLeaf()) {
            child.getChildren()[child.getNumKeys() + 1] = sibling.getChildren()[0];
        }

        node.setKey(idx, sibling.getKey(0));
        node.setValue(idx, sibling.getValue(0));

        for (int i = 1; i < sibling.getNumKeys(); ++i) {
            sibling.setKey(i - 1, sibling.getKey(i));
            sibling.setValue(i - 1, sibling.getValue(i));
        }

        if (!sibling.isLeaf()) {
            for (int i = 1; i <= sibling.getNumKeys(); ++i) {
                sibling.getChildren()[i - 1] = sibling.getChildren()[i];
            }
        }

        child.setNumKeys(child.getNumKeys() + 1);
        sibling.setNumKeys(sibling.getNumKeys() - 1);
    }

    private void merge(MemTreeNode<TKey, TVal> node, int idx) {
        MemTreeNode<TKey, TVal> child = node.getChildren()[idx];
        MemTreeNode<TKey, TVal> sibling = node.getChildren()[idx + 1];

        child.setKey(degree - 1, node.getKey(idx));
        child.setValue(degree - 1, node.getValue(idx));

        for (int i = 0; i < sibling.getNumKeys(); ++i) {
            child.setKey(i + degree, sibling.getKey(i));
            child.setValue(i + degree, sibling.getValue(i));
        }

        if (!child.isLeaf()) {
            if (sibling.getNumKeys() + 1 >= 0) {
                System.arraycopy(sibling.getChildren(), 0, child.getChildren(), degree, sibling.getNumKeys() + 1);
            }
        }

        for (int i = idx + 1; i < node.getNumKeys(); ++i) {
            node.setKey(i - 1, node.getKey(i));
            node.setValue(i - 1, node.getValue(i));
        }

        for (int i = idx + 2; i <= node.getNumKeys(); ++i) {
            node.getChildren()[i - 1] = node.getChildren()[i];
        }

        child.setNumKeys(child.getNumKeys() + sibling.getNumKeys() + 1);
        node.setNumKeys(node.getNumKeys() - 1);
    }

    private void updateValue(MemTreeNode<TKey, TVal> node, TKey tKey, TVal value) {
        int i = 0;

        while (i < node.getNumKeys() && tKey.compareTo(node.getKey(i)) > 0) {
            i++;
        }

        if (i < node.getNumKeys() && tKey.compareTo(node.getKey(i)) == 0) {
            node.setValue(i, value);
        } else if (!node.isLeaf()) {
            this.updateValue(node.getChildren()[i], tKey, value);
        }
    }
}
