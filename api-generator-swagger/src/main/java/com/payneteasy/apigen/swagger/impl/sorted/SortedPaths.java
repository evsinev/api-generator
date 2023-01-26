package com.payneteasy.apigen.swagger.impl.sorted;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SortedPaths extends Paths {

    @Override
    public Set<Map.Entry<String, PathItem>> entrySet() {
        TreeSet<Map.Entry<String, PathItem>> set = new TreeSet<>();
        for (Map.Entry<String, PathItem> entry : super.entrySet()) {
            set.add(new SortedMapEntry(entry));
        }
        return set;
    }

}
