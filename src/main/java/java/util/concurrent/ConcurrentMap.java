package java.util.concurrent;

import java.util.Map;

/// Models lib: don't change anything about Map -- since we don't model
/// concurrency at present there's no need to provide any atomicity guarantees.
public interface ConcurrentMap<K, V> extends Map<K, V> {
}
