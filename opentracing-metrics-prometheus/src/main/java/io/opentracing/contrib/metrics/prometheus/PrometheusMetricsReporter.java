package io.opentracing.contrib.metrics.prometheus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.opentracing.BaseSpan;
import io.opentracing.contrib.metrics.AbstractMetricsReporter;
import io.opentracing.contrib.metrics.MetricLabel;
import io.opentracing.contrib.metrics.MetricsReporter;
import io.opentracing.contrib.metrics.label.BaggageMetricLabel;
import io.opentracing.contrib.metrics.label.ConstMetricLabel;
import io.opentracing.contrib.metrics.label.TagMetricLabel;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public class PrometheusMetricsReporter extends AbstractMetricsReporter implements MetricsReporter {

    private final Counter count;
    private final Histogram duration;

    private PrometheusMetricsReporter(CollectorRegistry registry, List<MetricLabel> labels) {
        super(labels);

        String[] labelNames = getLabelNames();
        this.count = Counter.build().name("span_count").help("The span count")
                .labelNames(labelNames).register(registry);
        this.duration = Histogram.build().name("span_duration").help("The span duration")
                .labelNames(labelNames).register(registry);
    }

    @Override
    public void reportSpan(BaseSpan<?> span, String operation, Map<String, Object> tags, long duration) {
        String[] labelValues = getLabelValues(span, operation, tags, duration);
        if (labelValues != null) {
            this.count.labels(labelValues).inc();
            // Convert microseconds to seconds
            this.duration.labels(labelValues).observe(duration / (double)1000000);
        }
    }

    Counter getSpanCount() {
        return count;
    }

    Histogram getSpanDuration() {
        return duration;
    }

    /**
     * This method transforms the supplied label name to ensure it conforms to the required
     * Prometheus label format as defined by the regex "[a-zA-Z_:][a-zA-Z0-9_:]*".
     *
     * @param name The name
     * @return The label
     */
    protected static String convertLabel(String label) {
        StringBuilder builder = new StringBuilder(label);
        for (int i=0; i < builder.length(); i++) {
            char ch = builder.charAt(i);
            if (!(ch == '_' || ch == ':' || Character.isLetter(ch) || (i > 0 && Character.isDigit(ch)))) {
                builder.setCharAt(i, '_');
            }
        }
        return builder.toString();
    }

    protected String[] getLabelNames() {
        String[] labelNames = new String[metricLabels.length];
        for (int i=0; i < metricLabels.length; i++) {
            labelNames[i] = convertLabel(metricLabels[i].name());
        }
        return labelNames;
    }

    public static Builder newMetricsReporter() {
        return new Builder();
    }

    public static class Builder {
        private CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
        private List<MetricLabel> metricLabels = new ArrayList<MetricLabel>();

        public Builder withCollectorRegistry(CollectorRegistry collectorRegistry) {
            this.collectorRegistry = collectorRegistry;
            return this;
        }

        public Builder withCustomLabel(MetricLabel label) {
            metricLabels.add(label);
            return this;
        }

        public Builder withConstLabel(String name, Object value) {
            metricLabels.add(new ConstMetricLabel(name, value));
            return this;
        }

        public Builder withTagLabel(String name, Object defaultValue) {
            metricLabels.add(new TagMetricLabel(name, defaultValue));
            return this;
        }

        public Builder withBaggageLabel(String name, Object defaultValue) {
            metricLabels.add(new BaggageMetricLabel(name, defaultValue));
            return this;
        }

        public PrometheusMetricsReporter build() {
            return new PrometheusMetricsReporter(collectorRegistry, metricLabels);
        }
    }
}
