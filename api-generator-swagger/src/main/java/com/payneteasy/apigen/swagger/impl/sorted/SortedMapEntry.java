package com.payneteasy.apigen.swagger.impl.sorted;

import io.swagger.v3.oas.models.PathItem;

import javax.annotation.Nonnull;
import java.util.Map;

public class SortedMapEntry implements Map.Entry<String, PathItem>, Comparable<SortedMapEntry> {
    private final Map.Entry<String, PathItem> delegate;

    public SortedMapEntry(Map.Entry<String, PathItem> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compareTo(@Nonnull SortedMapEntry another) {
        return delegate.getKey().compareTo(another.getKey());
    }

    @Override
    public String getKey() {
        return delegate.getKey();
    }

    @Override
    public PathItem getValue() {
        return delegate.getValue();
    }

    @Override
    public PathItem setValue(PathItem value) {
        return delegate.setValue(value);
    }
}
