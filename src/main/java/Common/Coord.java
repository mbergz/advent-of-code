package Common;

public record Coord(int x, int y) {
    public static Coord of(int x, int y) {
        return new Coord(x, y);
    }

    public Coord oneUp() {
        return new Coord(this.x, this.y - 1);
    }

    public Coord oneRight() {
        return new Coord(this.x + 1, this.y);
    }

    public Coord oneDown() {
        return new Coord(this.x, this.y + 1);
    }

    public Coord oneLeft() {
        return new Coord(this.x - 1, this.y);
    }

}