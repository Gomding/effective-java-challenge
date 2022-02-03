package item69_20220203;

public class ExceptionEx1 {
    public static void main(String[] args) {
        String[] words = {"aaa", "bbb", "ccc", "ddd"};
        int totalLength = 0;
        try {
            int i = 0;
            while (true) {
                totalLength += words[i++].length();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        System.out.println(totalLength);
    }
}