package io.opentracing.contrib.metrics;

import io.opentracing.Tracer;
import io.opentracing.contrib.metrics.tracer.MetricsTracer;

public class Metrics {

    public static Tracer decorate(Tracer tracer, MetricsReporter reporter) {
        return new MetricsTracer(tracer, reporter);
    }
}
