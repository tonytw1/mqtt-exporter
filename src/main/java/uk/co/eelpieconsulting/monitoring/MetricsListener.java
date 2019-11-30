package uk.co.eelpieconsulting.monitoring;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.Message;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.eelpieconsulting.monitoring.model.Metric;

import java.util.List;

@Component
public class MetricsListener {
	
	private final static Logger log = Logger.getLogger(MetricsListener.class);
	
	private final MqttConnectionFactory mqttConnectionFactory;
	
	@Autowired
	public MetricsListener(MqttConnectionFactory mqttConnectionFactory, MetricsDAO metricsDAO) throws Exception {
		
		this.mqttConnectionFactory = mqttConnectionFactory;
		new Thread(new Listener(metricsDAO)).start();
	}

	private class Listener implements Runnable {
		
		private final MetricsDAO metricsDAO;

		public Listener(MetricsDAO metricsDAO) {
			this.metricsDAO = metricsDAO;
		}

		@Override
		public void run() {
			try {
				log.info("Starting metrics listener");
		        final BlockingConnection connection = mqttConnectionFactory.subscribeToMetricsTopic();
		        while (true) {
		        	processNextMessageFrom(connection);
		        }
		        
			} catch (Exception e) {
				log.error(e);
			}
		}

		private void processNextMessageFrom(BlockingConnection connection) {
			try {
				Message message = connection.receive();
	        	byte[] payload = message.getPayload();
	        			        	
	        	String metricMessage = new String(payload, "UTF-8");
	        	log.debug("Got metric message: " + metricMessage);
	        	String[] fields = metricMessage.replaceAll("\r", "").replaceAll("\n", "").split(":");
	        	String newValue = fields[1];

				String metricName = fields[0];
				Metric existing = metricsDAO.getByName(metricName);

				String existingValue = existing != null ? existing.getLastValue(): null;

				boolean hasChanged = !(newValue.equals(existingValue));

				List<DateTime> changes = existing != null ? existing.getChanges() : Lists.newArrayList();
				if (hasChanged) {
					changes.add(DateTime.now());
				}
				if (changes.size() > 2) {
					changes.remove(0);
				}

				Metric metric = new Metric(metricName, newValue, DateTime.now(), changes);
	        	metricsDAO.registerMetric(metric);

				message.ack();
				
			} catch (Exception e) {
				log.error(e);
			}
		}
		
	}
	
}
