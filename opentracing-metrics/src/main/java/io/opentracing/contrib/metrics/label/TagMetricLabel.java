package io.opentracing.contrib.metrics.label;

import java.util.Map;

import io.opentracing.BaseSpan;
import io.opentracing.contrib.metrics.MetricLabel;

public class TagMetricLabel implements MetricLabel {

    private final String name;
    private final Object defaultValue;

    public TagMetricLabel(String name, Object defaultValue) {
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
        Object ret = tags.get(name());
        return ret == null ? defaultValue : ret;
    }

}
