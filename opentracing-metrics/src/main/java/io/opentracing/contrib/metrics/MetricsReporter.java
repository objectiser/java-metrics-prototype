package io.opentracing.contrib.metrics;

import java.util.Map;

import io.opentracing.BaseSpan;

public interface MetricsReporter {

    public void reportSpan(BaseSpan<?> span, String operation, Map<String,Object> tags, long duration);

}
