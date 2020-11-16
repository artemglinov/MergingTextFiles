package com.artemglinov;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public final class FilesList extends SimpleFileVisitor<Path> {

    private final List<Path> textFiles = new ArrayList<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        /*
            Добавление файла в список только при условии, что это обычный текстовый файл с расширением .txt.
         */

//        if (Files.isRegularFile(file) && (file.getFileName().toString().endsWith(".txt"))){
        if (Files.probeContentType(file).startsWith("text/")){
            textFiles.add(file);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("Error accessing file: " + file.toAbsolutePath() + " " + exc.getMessage());
        return FileVisitResult.CONTINUE;
    }

    public List<Path> getPaths() {
        return textFiles;
    }
}


