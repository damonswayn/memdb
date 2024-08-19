package io.shruubi.memdb;

public interface MemDbInterface {
    void set(String key, String value);

    String get(String key);

    void delete(String key);
}
