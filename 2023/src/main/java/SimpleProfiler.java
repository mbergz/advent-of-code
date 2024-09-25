public class SimpleProfiler {
    private long startTime;

    public SimpleProfiler start() {
        startTime = System.currentTimeMillis();
        return this;
    }

    public void stop() {
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
        startTime = 0;
    }
}
