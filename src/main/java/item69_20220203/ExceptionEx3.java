package item69_20220203;

public class ExceptionEx3 {
    public static void main(String[] args) {
        String[] words = new String[100000000];
        for (int i = 0; i < words.length; i++) {
            words[i] = "aaa";
        }

        long start = System.currentTimeMillis();
        int totalLength = 0;
        for (String word : words) {
            totalLength += word.length();
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
