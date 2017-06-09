package io.opentracing.contrib.metrics.label;

import java.util.Map;

import io.opentracing.BaseSpan;
import io.opentracing.contrib.metrics.MetricLabel;

public class OperationMetricLabel implements MetricLabel {

    public OperationMetricLabel() {
    }

    @Override
    public String name() {
        return "operation";
    }

    @Override
    public Object defaultValue() {
        return null;
    }

    @Override
    public Object value(BaseSpan<?> span, String operation, Map<String, Object> tags) {
        return operation;
    }

}
