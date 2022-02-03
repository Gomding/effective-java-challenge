package item69_20220203;

public class ExceptionEx2 {
    public static void main(String[] args) {
        String[] words = new String[100000000];
        for (int i = 0; i < words.length; i++) {
            words[i] = "aaa";
        }

        long start = System.currentTimeMillis();
        int totalLength = 0;
        try {
            int i = 0;
            while (true) {
                totalLength += words[i++].length();
            }
        } catch (IndexOutOfBoundsException e) {
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
