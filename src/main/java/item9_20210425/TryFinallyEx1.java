package item9_20210425;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryFinallyEx1 {

    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
