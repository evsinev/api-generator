package com.payneteasy.apigen.maven.typescript;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static File createDirectories(File aFile) {
        if(aFile.exists()) {
            if(aFile.isDirectory()) {
                return aFile;
            } else {
                throw new IllegalStateException(aFile.getAbsolutePath()  + " already exists but not a directory");
            }
        }

        if(!aFile.mkdirs()) {
            throw new IllegalStateException("Cannot create dir " + aFile.getAbsolutePath());
        }

        return aFile;
    }

    public static void writeTextToFile(String aText, File aFile) {
        try {
            try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(aFile), StandardCharsets.UTF_8)) {
                out.write(aText);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write to " + aFile.getAbsolutePath(), e);
        }
    }
}
