package uk.co.eelpieconsulting.monitoring.controllers;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.eelpieconsulting.monitoring.MetricsDAO;
import uk.co.eelpieconsulting.monitoring.model.Metric;

import java.util.List;
import java.util.regex.Pattern;

@Controller
public class MetricsController {

  private static final Pattern NO_ODD_CHARACTERS = Pattern.compile("[^a-zA-Z0-9]+");

  private final MetricsDAO metricsDAO;

  @Autowired
  public MetricsController(MetricsDAO metricsDAO) {
    this.metricsDAO = metricsDAO;
  }

  @RequestMapping(value = "/metrics", method = RequestMethod.GET)
  public ModelAndView metrics() {
    List<Metric> metrics = metricsDAO.getMetrics();

    List<Metric> cleaned = Lists.newArrayList();
    for (Metric m : metrics) {
      try {
        Double.parseDouble(m.getLastValue());
        cleaned.add(new Metric(makeSafeName(m), m.getLastValue(), m.getDate(), Lists.newArrayList()));
      } catch (Exception e) {
        // TODO
      }
    }

    return new ModelAndView("templates/export").addObject("availableMetrics", cleaned);
  }

  private String makeSafeName(Metric m) {
    return NO_ODD_CHARACTERS.matcher(m.getName()).replaceAll("");
  }

}
