package org.secuso.privacyfriendlysudoku.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileInputOutput {
    FileInputStream inputstream;
    FileOutputStream outputstream;

    public FileInputOutput(){
        inputstream = null;
        outputstream = null;
    }

    public void writeFile(File file,String level) throws IOException {
        outputstream = new FileOutputStream(file);
        try {
            outputstream.write(level.getBytes());
        } finally {
            outputstream.close();
        }
    }

    public void readFile(File file, byte[] bytes) throws IOException {
        inputstream = new FileInputStream(file);
        try {
            inputstream.read(bytes);
        } finally {
            inputstream.close();
        }
    }
}
