package io.opentracing.contrib.metrics.label;

import java.util.Map;

import io.opentracing.BaseSpan;
import io.opentracing.contrib.metrics.MetricLabel;

public class BaggageMetricLabel implements MetricLabel {

    private final String name;
    private final Object defaultValue;

    public BaggageMetricLabel(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

    @Override
    public Object value(BaseSpan<?> span, String operation, Map<String, Object> tags) {
        Object ret = span.getBaggageItem(name());
        return ret == null ? defaultValue : ret;
    }

}
