package com.payneteasy.apigen.swagger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

public class MarkdownHeaders {

    private final Map<String, String> map;

    public MarkdownHeaders(File aFile) {
        map = parseHeaders(aFile);
    }

    private static Map<String, String> parseHeaders(File aFile) {
        Map<String, String> map = new HashMap<>();
        try {
            try (LineNumberReader in = new LineNumberReader(new InputStreamReader(new FileInputStream(aFile), UTF_8))) {
                String        line;
                StringBuilder pendingContent = new StringBuilder();
                String        pendingHeader  = null;
                while ((line = in.readLine()) != null) {

                    if (line.startsWith("## ") | line.startsWith("### ")) {
                        addPendings(map, pendingHeader, pendingContent);
                        pendingHeader = line.substring(line.indexOf(' ')).trim();
                        continue;
                    }

                    pendingContent.append(line);
                    pendingContent.append('\n');

                }
                addPendings(map, pendingHeader, pendingContent);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + aFile.getAbsolutePath(), e);
        }
        return map;
    }

    private static void addPendings(Map<String, String> map, String pendingHeader, StringBuilder pendingContent) {
        if(pendingHeader == null) {
            return;
        }

        map.put(pendingHeader, pendingContent.toString());
        pendingContent.delete(0, pendingContent.length());
    }

    public Optional<String> getContent(String aName) {
        return ofNullable(map.get(aName));
    }
}
