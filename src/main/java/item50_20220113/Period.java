package item50_20220113;

import java.util.Date;

public class Period {
    private final Date start;
    private final Date end;

    /**
     * @param start 시작 시각
     * @param end 종료 시각
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
     */
    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0) // 시작되는 날이 끝나는 날 이후의 시간일 수 없다.
            throw new IllegalArgumentException("시작 시각:{ " + start + " }가 종료 시각:{ " + end + " }보다 늦다.");

        this.start = start;
        this.end = end;
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }

    @Override
    public String toString() {
        return "Period{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
