package io.shruubi.memtree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class MemTreeTest {

    private MemTree<String, String> memTree;

    @BeforeEach
    void setUp() {
        memTree = new MemTree<>(MemTree.DEFAULT_DEGREE);
    }

    @Test
    public void insert() {
        memTree.insert("key1", "value1");
        assertEquals("key1", this.memTree.getRoot().getKey(0));
        assertEquals("value1", this.memTree.getRoot().getValue(0));
    }

    @Test
    public void search() {
        memTree.insert("key1", "value1");
        assertEquals("value1", this.memTree.search("key1"));
    }

    @Test
    public void searchWithLargeDataset() {
        for (int i = 0; i < 1000; i++) {
            memTree.insert("key" + i, "value" + i);
        }
        for (int i = 0; i < 1000; i++) {
            assertEquals("value" + i, this.memTree.search("key" + i));
        }
    }

    @Test
    public void delete() {
        memTree.insert("key1", "value1");
        memTree.delete("key1");
        assertNull(this.memTree.search("key1"));
    }
}