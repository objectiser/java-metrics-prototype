package io.opentracing.contrib.metrics.tracer;

import io.opentracing.ActiveSpan;
import io.opentracing.SpanContext;

public class MetricsActiveSpan extends MetricsBaseSpan<ActiveSpan> implements ActiveSpan  {

    public MetricsActiveSpan(MetricsTracer tracer, ActiveSpan span) {
        super(tracer, span);
    }

    public Continuation capture() {
        return new MetricsContinuation(metricsTracer(), span().context(), span().capture());
    }

    public void deactivate() {
        span().deactivate();
        metricsTracer().spanDeactivated(context());
    }

    public void close() {
        span().close();
    }

    private static class MetricsContinuation implements Continuation {

        private final MetricsTracer tracer;
        private final Continuation continuation;

        public MetricsContinuation(MetricsTracer tracer, SpanContext context, Continuation continuation) {
            this.tracer = tracer;
            this.continuation = continuation;
            tracer.spanDeactivated(context);
        }

        @Override
        public ActiveSpan activate() {
            return new MetricsActiveSpan(tracer, continuation.activate());
        }
        
    }
}
