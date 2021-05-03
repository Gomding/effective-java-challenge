package item10_20210501;

public class TransitiveEx2 {
}


class Point2 {
    private final int x;
    private final int y;

    public Point2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        Point2 p = (Point2) o;
        return p.x == x && p.y == y;
    }
}

class ColorPoint2 extends Point2 {
    private final Color2 color;

    public ColorPoint2(int x, int y, Color2 color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  Point2)) {
            return false;
        }

        // o가 일반 Point 면 색상을 무시하고 비교
        if (!(o instanceof ColorPoint2)) {
            return o.equals(this);
        }

        // o 가 ColorPoint면 색상까지 비교합니다.
        return super.equals(o) && ((ColorPoint2) o).color == color;
    }
}

class Color2 {
    private String name;

    public Color2(String name) {
        this.name = name;
    }
}
