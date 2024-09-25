package Common;

public record FromCoord(Coord coord, Direction from) {
    public static FromCoord of(Coord coord, Direction from) {
        return new FromCoord(coord, from);
    }
}
