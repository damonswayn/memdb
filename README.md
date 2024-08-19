# MemDB

![example workflow](https://github.com/damonswayn/memdb/actions/workflows/ci.yml/badge.svg)


A simple in-memory key-value store.

### Build

```bash
$ mvn clean package
```

### Usage

```bash
# starts prompt
$ java -jar target/memdb-1.0-SNAPSHOT.jar

# use the set command
memdb> SET <KEY> <VALUE>

# commands are case insensitive
memdb> set <KEY> <VALUE>

# use the get command
memdb> GET <KEY>

# use the delete command
memdb> DELETE <KEY>

# quit
memdb> QUIT
```

### Code

The underlying structure for the key-value store is a B-Tree which is used for an efficient storage and searching
for key/value pairs.

There are two modules in the project:
- `memdb` - Implements the interface for the key-value store.
- `memtree` - Implements the B-Tree structure.

### Tests

```bash
$ mvn test
```