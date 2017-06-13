package io.opentracing.contrib.metrics.label;

import java.util.Map;

import io.opentracing.BaseSpan;
import io.opentracing.contrib.metrics.MetricLabel;

public class ConstMetricLabel implements MetricLabel {

    private final String name;
    private final Object value;

    public ConstMetricLabel(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object defaultValue() {
        return value;
    }

    @Override
    public Object value(BaseSpan<?> span, String operation, Map<String, Object> tags) {
        return value;
    }

    
}
