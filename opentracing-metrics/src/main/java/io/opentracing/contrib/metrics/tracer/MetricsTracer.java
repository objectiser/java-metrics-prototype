package io.opentracing.contrib.metrics.tracer;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.opentracing.ActiveSpan;
import io.opentracing.BaseSpan;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.metrics.MetricsReporter;
import io.opentracing.propagation.Format;

public class MetricsTracer implements Tracer {

    private final Tracer tracer;
    private final MetricsReporter reporter;

    private final Map<SpanContext,MetricData> metricData = new WeakHashMap<SpanContext,MetricData>();

    public MetricsTracer(Tracer tracer, MetricsReporter reporter) {
        this.tracer = tracer;
        this.reporter = reporter;
    }

    MetricsReporter getReporter() {
        return reporter;
    }

    @Override
    public ActiveSpan activeSpan() {
        return new MetricsActiveSpan(this, tracer.activeSpan());
    }

    @Override
    public ActiveSpan makeActive(Span span) {
        return new MetricsActiveSpan(this, tracer.makeActive(span));
    }

    @Override
    public SpanBuilder buildSpan(String operation) {
        return new MetricsSpanBuilder(operation, tracer.buildSpan(operation));
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C carrier) {
        return tracer.extract(format, carrier);
    }

    @Override
    public <C> void inject(SpanContext context, Format<C> format, C carrier) {
        tracer.inject(context, format, carrier);
    }

    void spanStarted(BaseSpan<?> span, String operationName, long startNanoTime, Map<String,Object> tags) {
        if (reporter != null) {
            synchronized (metricData) {
                metricData.put(span.context(), new MetricData(span, operationName, startNanoTime, tags));
            }
        }
    }

    void spanUpdateTag(SpanContext context, String key, Object value) {
        if (reporter != null) {
            synchronized (metricData) {
                MetricData data = metricData.get(context);
                if (data != null) {
                    data.getTags().put(key, value);
                }
            }
        }
    }

    void spanUpdateOperation(SpanContext context, String operationName) {
        if (reporter != null) {
            synchronized (metricData) {
                MetricData data = metricData.get(context);
                if (data != null) {
                    data.setOperationName(operationName);
                }
            }
        }        
    }

    void spanFinished(SpanContext context, long finishNanoTime) {
        if (reporter != null) {
            MetricData data = null;
            synchronized (metricData) {
                data = metricData.remove(context);
            }
            if (data != null) {
                reporter.reportSpan(data.getSpan(), data.getOperationName(), data.getTags(),
                        TimeUnit.NANOSECONDS.toMicros(finishNanoTime - data.getStartNanoTime()));
            }
        }
    }

    void spanCreatedContinuation(SpanContext context) {
        if (reporter != null) {
            synchronized (metricData) {
                MetricData data = metricData.get(context);
                if (data != null) {
                    data.incrementRefCount();
                }
            }
        }
    }

    void spanDeactivated(SpanContext context) {
        if (reporter != null) {
            synchronized (metricData) {
                MetricData data = metricData.get(context);
                if (data != null && data.decrementRefCount() == 0) {
                    spanFinished(context, System.nanoTime());
                }
            }
        }
    }

    static class MetricData {
        private final AtomicInteger refCount;
        private BaseSpan<?> span;
        private String operationName;
        private final long startNanoTime;
        private Map<String,Object> tags;
        
        public MetricData(BaseSpan<?> span, String operationName, long startNanoTime, Map<String,Object> tags) {
            this.span = span;
            this.operationName = operationName;
            this.startNanoTime = startNanoTime;
            this.tags = tags;
            this.refCount = new AtomicInteger(1);
        }

        public BaseSpan<?> getSpan() {
            return span;
        }

        public String getOperationName() {
            return operationName;
        }

        public void setOperationName(String operationName) {
            this.operationName = operationName;
        }

        public long getStartNanoTime() {
            return startNanoTime;
        }

        public Map<String,Object> getTags() {
            return tags;
        }

        public void incrementRefCount() {
            refCount.incrementAndGet();
        }

        public int decrementRefCount() {
            return refCount.decrementAndGet();
        }
    }

    public class MetricsSpanBuilder implements SpanBuilder {
        
        private final String operationName;
        private final SpanBuilder builder;
        private final long startNanoTime = System.nanoTime();
        private final Map<String,Object> tags = new HashMap<String,Object>();

        public MetricsSpanBuilder(String operationName, SpanBuilder builder) {
            this.operationName = operationName;
            this.builder = builder;
        }

        @Override
        public SpanBuilder asChildOf(SpanContext parent) {
            builder.asChildOf(parent);
            return this;
        }

        @Override
        public SpanBuilder asChildOf(BaseSpan<?> parent) {
            builder.asChildOf(parent);
            return this;
        }

        @Override
        public SpanBuilder addReference(String referenceType, SpanContext referencedContext) {
            builder.addReference(referenceType, referencedContext);
            return this;
        }

        @Override
        public SpanBuilder ignoreActiveSpan() {
            builder.ignoreActiveSpan();
            return this;
        }

        @Override
        public SpanBuilder withTag(String key, String value) {
            tags.put(key, value);
            builder.withTag(key, value);
            return this;
        }

        @Override
        public SpanBuilder withTag(String key, boolean value) {
            tags.put(key, value);
            builder.withTag(key, value);
            return this;
        }

        @Override
        public SpanBuilder withTag(String key, Number value) {
            tags.put(key, value);
            builder.withTag(key, value);
            return this;
        }

        @Override
        public SpanBuilder withStartTimestamp(long microseconds) {
            builder.withStartTimestamp(microseconds);
            return this;
        }

        @Override
        public ActiveSpan startActive() {
            ActiveSpan activeSpan = new MetricsActiveSpan(MetricsTracer.this, builder.startActive());
            spanStarted(activeSpan, operationName, startNanoTime, tags);
            return activeSpan;
        }

        @Override
        public Span startManual() {
            Span span = new MetricsSpan(MetricsTracer.this, builder.startManual());
            spanStarted(span, operationName, startNanoTime, tags);
            return span;
        }

        @Override
        public Span start() {
            return startManual();
        }

    }
}
