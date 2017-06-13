package io.opentracing.contrib.metrics.tracer;

import java.util.Map;

import io.opentracing.BaseSpan;
import io.opentracing.SpanContext;

public class MetricsBaseSpan<T extends BaseSpan<?>> implements BaseSpan<T> {

    private final T span;
    private final MetricsTracer tracer;

    public MetricsBaseSpan(MetricsTracer tracer, T span) {
        this.tracer = tracer;
        this.span = span;
    }

    protected T span() {
        return span;
    }

    protected MetricsTracer metricsTracer() {
        return tracer;
    }

    @Override
    public SpanContext context() {
        return span.context();
    }

    @Override
    public String getBaggageItem(String name) {
        return span.getBaggageItem(name);
    }

    @Override
    public T setBaggageItem(String name, String value) {
        return this.setBaggageItem(name, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T log(Map<String, ?> fields) {
        span.log(fields);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T log(String event) {
        span.log(event);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T log(long timestampMicroseconds, Map<String, ?> fields) {
        span.log(timestampMicroseconds, fields);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T log(long timestampMicroseconds, String event) {
        span.log(timestampMicroseconds, event);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setOperationName(String operationName) {
        span.setOperationName(operationName);
        tracer.spanUpdateOperation(span.context(), operationName);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setTag(String key, String value) {
        span.setTag(key, value);
        tracer.spanUpdateTag(span.context(), key, value);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setTag(String key, boolean value) {
        span.setTag(key, value);
        tracer.spanUpdateTag(span.context(), key, value);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setTag(String key, Number value) {
        span.setTag(key, value);
        tracer.spanUpdateTag(span.context(), key, value);
        return (T)this;
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public T log(String eventName, Object payload) {
        return (T)span.log(eventName, payload);
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public T log(long timestampMicroseconds, String eventName, Object payload) {
        return (T)span.log(timestampMicroseconds, eventName, payload);
    }

}
