package item50_20220113;

import java.util.Date;

public class PeriodEx {
    public static void main(String[] args) {
        Date start = new Date();
        Date end = new Date();
        Period period = new Period(start, end);
        System.out.println("end 변경 전 " + period);
        end.setYear(78);
        System.out.println("end 변경 후" + period);
    }
}
