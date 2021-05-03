package item10_20210501;

public class TransitiveEx1 {
}

class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
}

class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  Point)) {
            return false;
        }

        // o가 일반 Point 면 색상을 무시하고 비교
        if (!(o instanceof ColorPoint)) {
            return o.equals(this);
        }

        // o 가 ColorPoint면 색상까지 비교합니다.
        return super.equals(o) && ((ColorPoint) o).color == color;
    }
}

class Color {
    private String name;

    public Color(String name) {
        this.name = name;
    }
}
