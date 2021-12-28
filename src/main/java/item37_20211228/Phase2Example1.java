package item37_20211228;

public class Phase2Example1 {

    public static void main(String[] args) {
        Phase2.Transition2 transition = Phase2.Transition2.from(Phase2.LIQUID, Phase2.GAS);
        System.out.println(transition);
    }
}
