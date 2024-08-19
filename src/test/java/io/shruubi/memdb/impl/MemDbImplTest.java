package io.shruubi.memdb.impl;

import io.shruubi.memdb.MemDbInterface;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class MemDbImplTest {
    private MemDbInterface memDb;

    @BeforeEach
    void setUp() {
        this.memDb = new MemDbImpl();
    }

    @Test
    public void setSingleValue() {
        this.memDb.set("key", "value");
        assertEquals("value", this.memDb.get("key"));
    }

    @Test
    public void setMultipleValues() {
        this.memDb.set("key1", "value1");
        this.memDb.set("key2", "value2");
        this.memDb.set("key3", "value3");
        assertEquals("value1", this.memDb.get("key1"));
        assertEquals("value2", this.memDb.get("key2"));
        assertEquals("value3", this.memDb.get("key3"));
    }

    @Test
    public void setDuplicateKey() {
        this.memDb.set("key", "value1");
        this.memDb.set("key", "value2");
        assertEquals("value2", this.memDb.get("key"));
    }

    @Test
    public void getNonExistentKey() {
        assertNull(this.memDb.get("key"));
    }

    @Test
    public void getNullKey() {
        assertNull(this.memDb.get(null));
    }

    @Test
    public void setNullKey() {
        this.memDb.set(null, "value");
        assertNull(this.memDb.get(null));
    }

    @Test
    public void setNullValue() {
        this.memDb.set("key", null);
        assertNull(this.memDb.get("key"));
    }

    @Test
    public void setNullKeyAndValue() {
        this.memDb.set(null, null);
        assertNull(this.memDb.get(null));
    }

    @Test
    public void setEmptyKey() {
        this.memDb.set("", "value");
        assertEquals("value", this.memDb.get(""));
    }

    @Test
    @Ignore
    public void handleALargeDataset() {
        for (int i = 0; i < 1000000; i++) {
            this.memDb.set(String.valueOf(i), String.valueOf(i));
        }

        for (int i = 0; i < 1000000; i++) {
            assertEquals(String.valueOf(i), this.memDb.get(String.valueOf(i)));
        }
    }

    @Test
    @Ignore
    public void handleAVeryLargeDataset() {
        for (int i = 0; i < 10000000; i++) {
            this.memDb.set(String.valueOf(i), String.valueOf(i));
        }

        for (int i = 0; i < 10000000; i++) {
            assertEquals(String.valueOf(i), this.memDb.get(String.valueOf(i)));
        }
    }
}