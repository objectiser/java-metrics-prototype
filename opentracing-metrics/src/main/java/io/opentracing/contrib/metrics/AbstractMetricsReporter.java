package io.opentracing.contrib.metrics;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import io.opentracing.BaseSpan;
import io.opentracing.contrib.metrics.MetricLabel;
import io.opentracing.contrib.metrics.MetricsReporter;
import io.opentracing.contrib.metrics.label.OperationMetricLabel;
import io.opentracing.contrib.metrics.label.TagMetricLabel;
import io.opentracing.tag.Tags;

public abstract class AbstractMetricsReporter implements MetricsReporter {

    protected MetricLabel[] metricLabels;

    protected final static List<MetricLabel> STANDARD_SPAN_LABELS = Arrays.asList(
            new OperationMetricLabel(),
            new TagMetricLabel(Tags.SPAN_KIND.getKey(), null),
            new TagMetricLabel(Tags.ERROR.getKey(), Boolean.FALSE.toString()));

    protected AbstractMetricsReporter(List<MetricLabel> labels) {
        initLabels(labels);
    }

    /**
     * This method initializes the list of metric labels for use by the reporter. The list
     * needs to be initialized on instantiation as they must remain constant for all of the
     * metric types reported. The list of labels is constructed based on the supplied labels
     * as well as the standard labels.
     *
     * @param labels The list of additional and overridden label definitions
     */
    protected void initLabels(List<MetricLabel> labels) {
        Map<String,MetricLabel> labelsByName = new LinkedHashMap<String,MetricLabel>();
        for (MetricLabel label : labels) {
            labelsByName.put(label.name(), label);
        }
        for (MetricLabel label : STANDARD_SPAN_LABELS) {
            // Check if standard label has been overridden
            if (labelsByName.containsKey(label.name())) {
                // Remove and re-insert to maintain order of standard labels
                labelsByName.put(label.name(), labelsByName.remove(label.name()));
            } else {
                labelsByName.put(label.name(), label);
            }
        }
        metricLabels = new MetricLabel[labelsByName.size()];
        labelsByName.values().toArray(metricLabels);
    }

    /**
     * This method derives the values for the labels associated with the metrics reporter.
     *
     * @param span The span
     * @param operation The operation
     * @param tags The span tags
     * @param duration The duration
     * @return The array of values, or null if the span metrics should not be reported
     */
    protected String[] getLabelValues(BaseSpan<?> span, String operation, Map<String, Object> tags, long duration) {
        String[] values = new String[metricLabels.length];
        for (int i=0; i < values.length; i++) {
            Object value = metricLabels[i].value(span, operation, tags);
            if (value == null) {
                // Don't report span as not all labels are specified
                // TODO: May need to provide debug log to help if metrics unexpectedly not reported
                return null;
            }
            values[i] = value.toString();
        }
        return values;
    }

}
