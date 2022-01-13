package item50_20220113;

import java.util.Date;

public class Period3 {
    private final Date start;
    private final Date end;

    public Period3(Date start, Date end) {
        // 불변식 검사보다 방어적 복사를 먼저 진행한다.
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0)
            throw new IllegalArgumentException("시작 시각:{ " + start + " }가 종료 시각:{ " + end + " }보다 늦다.");
    }

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }

    @Override
    public String toString() {
        return "Period3{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
