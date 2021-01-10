package uk.co.eelpieconsulting.monitoring;

import org.apache.log4j.Logger;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class MqttConnectionFactory {

    private static final Logger log = Logger.getLogger(MqttConnectionFactory.class);

    private final String metricsHost;
    private final int metricsPort;
    private final String metricsTopic;

    @Autowired
    public MqttConnectionFactory(
            @Value("${mqtt.metrics.host}") String metricsHost,
            @Value("${mqtt.metrics.port}") int metricsPort,
            @Value("${mqtt.metrics.topic}") String metricsTopic) {
        this.metricsHost = metricsHost;
        this.metricsPort = metricsPort;
        this.metricsTopic = metricsTopic;
    }

    public BlockingConnection subscribeToMetricsTopic() throws URISyntaxException, Exception {
        BlockingConnection connection = connectToMetricsHost();
        log.info("Subscribing to topic '" + metricsTopic + "' on host '" + metricsHost + "'");
        connection.subscribe(new Topic[]{new Topic(metricsTopic, QoS.AT_MOST_ONCE)});
        return connection;
    }

    private BlockingConnection connectToMetricsHost() throws URISyntaxException, Exception {
        log.info("Connecting to metrics host: " + metricsHost);
        MQTT mqtt = new MQTT();
        mqtt.setHost(metricsHost, metricsPort);
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        return connection;
    }

}