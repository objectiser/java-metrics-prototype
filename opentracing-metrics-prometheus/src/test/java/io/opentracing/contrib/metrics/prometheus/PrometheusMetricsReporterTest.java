package io.opentracing.contrib.metrics.prometheus;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.Collector.MetricFamilySamples.Sample;
import io.prometheus.client.CollectorRegistry;

public class PrometheusMetricsReporterTest {

    private CollectorRegistry collectorRegistry;

    @Before
    public void init() {
        collectorRegistry = new CollectorRegistry();
    }

    @After
    public void close() {
        collectorRegistry.clear();
    }

    @Test
    public void testReportSpanWithDefaultValuesForMissingTags() {
        PrometheusMetricsReporter reporter = PrometheusMetricsReporter.newMetricsReporter()
                .withCollectorRegistry(collectorRegistry)
                .withConstLabel("span.kind", Tags.SPAN_KIND_CLIENT) // Override the default, to make sure span metrics reported
                .build();

        Span span = Mockito.mock(Span.class);

        Map<String,Object> spanTags = new HashMap<String,Object>();
        spanTags.put(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);

        reporter.reportSpan(span, "testop", spanTags, 100000L);

        // Check span count
        List<MetricFamilySamples> samples = reporter.getSpanCount().collect();
        assertEquals(1, samples.size());
        assertEquals(1, samples.get(0).samples.size());

        Sample sample=samples.get(0).samples.get(0);
        assertEquals(1, (int)sample.value); // Span count
        assertEquals(Arrays.asList(reporter.getLabelNames()), sample.labelNames);
        assertEquals("testop", sample.labelValues.get(0));

        // Check span duration
        samples = reporter.getSpanDuration().collect();
        assertEquals(1, samples.size());
        assertEquals(17, samples.get(0).samples.size());

        for (int i=0; i < samples.get(0).samples.size(); i++) {
            sample = samples.get(0).samples.get(i);
            // Verify operation name
            assertEquals("testop", sample.labelValues.get(0));
        }
    }

    @Test
    public void testConvertLabel() {
        assertEquals("Hello9", PrometheusMetricsReporter.convertLabel("Hello9"));
        assertEquals("Hello_there", PrometheusMetricsReporter.convertLabel("Hello there"));  // Space invalid
        assertEquals("_tag1", PrometheusMetricsReporter.convertLabel("1tag1"));  // Leading number invalid
        assertEquals("tag_:_", PrometheusMetricsReporter.convertLabel("tag£:%"));  // Some characters invalid
    }

}
