package org.yah.tools.opencl.sandbox;

public class BenchmarkResult {
    public final double min;
    public final double max;
    public final double avg;

    public BenchmarkResult(double min, double max, double avg) {
        this.min = min;
        this.max = max;
        this.avg = avg;
    }

    @Override
    public String toString() {
        return String.format("%.3f [%.3f, %.3f] ms", avg, min, max);
    }
}
