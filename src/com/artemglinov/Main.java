package com.artemglinov;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;

public class Main {

    /*
    Клиент, который передаёт необходимый URI в качестве аргумента метода getPath. В нашем случае это
    относительный путь к директории FileTree.
    */
    public static void main(String[] args) {
        glueContents(getSortedList(FileSystems.getDefault().getPath("FileTree")));
    }

    private static List<Path> getSortedList(Path rootPath) {

        if (!rootPath.toFile().exists()) {
            throw new IllegalArgumentException("Directory specified by the path " + rootPath + " does not exist");
        }

        FilesList filesList = new FilesList();
        try {
            Files.walkFileTree(rootPath, filesList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Path> files = filesList.getPaths();
        files.sort(Comparator.comparing(Path::getFileName));

        return files;
    }

    private static void glueContents(List<Path> textFiles) {

        /*
            Проверка списка на null.
         */
        if (textFiles == null) {
            throw new IllegalArgumentException("The list of files is null");
        }

        /*
            Проверка на наличие элементов в списке.
         */

        if (textFiles.isEmpty()) {
            System.out.println("The list of files is empty");
        }

        /*
            Проверка списка на наличие файлов нужного расширения (формата) (.txt в нашем случае как пример).
         */


        try {
            for (Path path : textFiles) {
                System.out.println(path.getFileName());
//            if (!path.getFileName().toString().endsWith(".txt")) {
                if (!Files.probeContentType(path).startsWith("text/")) {
                    throw new IllegalArgumentException("One of the files has an illegal extension");
                }
            }

            /*
                Возможна склейка не в один из имеющихся, а в иной текстовый файл,
                чей URI можно передать как второй аргумент метода.
                Тогда следующяя строка кода позволяет получить необходимый Path.

            Path glued = Paths.get(uri);

                 Дальнейший код меняется незначительно.

                 Если же нужно склеить файлы в один из имеющихся (например, в первый),
                 то в качестве glued выберем его и прочитаем все остальные файлы.
             */

            Path glued = textFiles.get(0);

            try (BufferedWriter bw = Files.newBufferedWriter(glued, StandardOpenOption.APPEND);
                 PrintWriter pw = new PrintWriter(bw)) {

                for (int i = 1; i < textFiles.size(); i++) {
                    try (BufferedReader br = Files.newBufferedReader(textFiles.get(i))) {
                        String line = br.readLine();

                        while (line != null) {
                            pw.println(line);
                            line = br.readLine();
                        }

                        pw.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
