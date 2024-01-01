package Common;

public record Range(int from, int to) {
    public boolean isInRange( Range b) {
        for (int i = this.from; i <= this.to; i++) {
            if (b.from() <= i && i <= b.to()) {
                return true;
            }
        }
        return false;
    }
}