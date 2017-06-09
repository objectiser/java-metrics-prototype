package io.opentracing.contrib.metrics;

import java.util.Map;

import io.opentracing.BaseSpan;

public interface MetricLabel {

    /**
     * This method returns the name of the metric tag.
     *
     * @return The name
     */
    String name();

    /**
     * This method returns a default value for the specified
     * label, if one is defined, otherwise null.
     *
     * @return The default value, or null
     */
    Object defaultValue();

    /**
     * This method returns a metric tag value.
     *
     * @param span The span
     * @param operation The operation
     * @param tags The tags
     * @return The value, if null will suppress the metrics for the span being reported
     */
    Object value(BaseSpan<?> span, String operation, Map<String,Object> tags);

}
