package io.opentracing.contrib.metrics.tracer;

import io.opentracing.Span;

public class MetricsSpan extends MetricsBaseSpan<Span> implements Span  {

    public MetricsSpan(MetricsTracer tracer, Span span) {
        super(tracer, span);
    }

    @Override
    public void finish() {
        span().finish();
        metricsTracer().spanFinished(span().context(), System.nanoTime());
    }

    @Override
    public void finish(long finishMicros) {
        span().finish(finishMicros);
        metricsTracer().spanFinished(span().context(), System.nanoTime());
    }

}
