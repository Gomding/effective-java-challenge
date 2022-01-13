package item50_20220113;

import java.util.Date;

public class PeriodEx2 {
    public static void main(String[] args) {
        Date start = new Date();
        Date end = new Date();
        Period2 period = new Period2(start, end);
        System.out.println("end 변경 전 " + period);
        end.setYear(78);
        System.out.println("end 변경 후 " + period);

        System.out.println("접근자 메서드로 end 변경 전 " + period);
        period.end().setYear(78);
        System.out.println("접근자 메서드로 end 변경 후 " + period);
    }
}
