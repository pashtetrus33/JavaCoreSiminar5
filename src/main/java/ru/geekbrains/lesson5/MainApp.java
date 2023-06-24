package ru.geekbrains.lesson5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 1. Создать 2 текстовых файла;
 * 2. Написать метод, склеивающий эти файлы, то есть в начале идет текст из первого файла, потом текст из второго.
 * 3.* Написать метод, который проверяет, присутствует ли указанное пользователем слово в файле (работаем только с латиницей).
 * 4.* Написать метод, проверяющий, есть ли указанное слово в папке.
 */

public class MainApp {

    private static final Random random = new Random();

    private static final int CHAR_BOUND_L = 65; // Номер начального символа А
    private static final int CHAR_BOUND_H = 90; // Номер последнего символа Z
    private static final String TO_SEARCH = "GEEKBRAINS"; // для поиска


    public static void main(String[] args) {
        try {
            writeFileContext("file.txt", 300);
            writeFileContext("file2.txt", 300, 6);
            concatenate("file.txt", "file2.txt", "out.txt");
            System.out.println("Word " + TO_SEARCH + " is found: " + searchWordInFile("out.txt", TO_SEARCH));

            Tree.print(new File("."), "", true);

            String[] fileNames = new String[10];
            for (int i = 0; i < fileNames.length; i++) {
                fileNames[i] = "file_" + i + ".txt";
                writeFileContext(fileNames[i], 30, 4);
                System.out.println("Файл " + fileNames[i] + " создан.");
            }


            List<String> result = searchMatch(fileNames, TO_SEARCH);
            for (String s : result) {
                System.out.printf("Файл %s содержит слово '%s'\n", s, TO_SEARCH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод генерации некоторой последовательности символов
     *
     * @param amount количество символов
     * @return последовательность символов
     */
    private static String generateSymbols(int amount) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= amount; i++) {
            stringBuilder.append((char) random.nextInt(CHAR_BOUND_L, CHAR_BOUND_H + 1));
            if (i % 140 == 0) {
                stringBuilder.append("\n");
            }
            if (i % 10 == 0) {
                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Записать последовательность символов в файл
     *
     * @param fileName имя файла
     * @param length   длина последовательности символов
     * @throws IOException исключение ввода вывода
     */
    private static void writeFileContext(String fileName, int length) throws IOException {

//        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
//        fileOutputStream.write(generateSymbols(length).getBytes());
//        fileOutputStream.flush();
//        fileOutputStream.close();

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            fileOutputStream.write(generateSymbols(length).getBytes());
        }
    }

    /**
     * Записать последовательность символов в файл, при этом дописать осознанное слово
     *
     * @param fileName имя файла
     * @param length   длина последовательности символов
     * @param words    количество слов для поиска
     * @throws IOException исключение ввода вывода
     */
    private static void writeFileContext(String fileName, int length, int words) throws IOException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            for (int i = 0; i < words; i++) {
                if (random.nextInt(5) == 5 / 2) {
                    fileOutputStream.write(TO_SEARCH.getBytes());
                } else {
                    fileOutputStream.write(generateSymbols(length).getBytes());
                }
            }
        }
    }

    /**
     * @param fileIn1 имя файла 1
     * @param fileIn2 имя файла 2
     * @param fileOut имя выходного файла
     * @throws IOException исключение ввода-вывода
     */
    private static void concatenate(String fileIn1, String fileIn2, String fileOut) throws IOException {
        //На запись
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
            int c;
            //На чтение
            try (FileInputStream fileInputStream = new FileInputStream(fileIn1)) {
                while ((c = fileInputStream.read()) != -1) {
                    fileOutputStream.write(c);
                }
            }
            //На чтение
            try (FileInputStream fileInputStream = new FileInputStream(fileIn2)) {
                while ((c = fileInputStream.read()) != -1) {
                    fileOutputStream.write(c);
                }
            }
        }
    }

    /**
     * Определить, содержится ли в файле искомое слово
     *
     * @param filename имя файла
     * @param word     строка для поиска
     * @return результат поиска
     */

    private static boolean searchWordInFile(String filename, String word) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            //Cпособ с семинара
            int c;
            byte[] searchData = word.getBytes();
            int i = 0;
            while ((c = fileInputStream.read()) != -1) {
                if (c == searchData[i]) {
                    i++;
                } else {
                    i = 0;
                    if (c == searchData[i]) //GEEKBRAINGEEKBRAINS
                        i++;
                    continue;
                }
                if (i == searchData.length) {
                    return true;
                }
            }
            return false;

            //Мой способ
//            int c;
//            StringBuilder stringBuilder = new StringBuilder();
//            while ((c = fileInputStream.read()) != -1) {
//                if (stringBuilder.length() < word.length()) {
//                    stringBuilder.append((char) c);
//                } else {
//                    if (stringBuilder.toString().equals(word)) {
//                        return true;
//                    } else {
//                        stringBuilder.deleteCharAt(0);
//                        stringBuilder.append((char) c);
//                    }
//                }
//            }
//            return stringBuilder.toString().equals(word);
        }
    }

    private static List<String> searchMatch(String[] files, String search) throws IOException {
        List<String> list = new ArrayList<>();
        File path = new File(new File(".").getCanonicalPath());
        File[] dir = path.listFiles();
        for (int i = 0; i < dir.length; i++) {
            if (dir[i].isDirectory()) {
                continue;
            }
            for (int j = 0; j < files.length; j++) {
                if (dir[i].getName().equals(files[j])) {
                    if (searchWordInFile(dir[i].getName(), search)) {
                        list.add(dir[i].getName());
                        break;
                    }
                }
            }
        }
        return list;
    }
}
