package com.swarauto.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FileUtil {
    public static String readTextFile(String path) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static boolean writeTextFile(String path, String content) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path);
            writer.print(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void fileCopy(String sourcePath, String destPath) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(sourcePath);
            os = new FileOutputStream(destPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    public static void fileCopy(InputStream sourceIs, String destPath) throws IOException {
        if (sourceIs == null) return;
        OutputStream os = null;
        try {
            os = new FileOutputStream(destPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = sourceIs.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
