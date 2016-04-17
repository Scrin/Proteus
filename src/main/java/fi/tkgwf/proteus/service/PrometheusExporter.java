package fi.tkgwf.proteus.service;

import fi.tkgwf.proteus.snmp.SnmpCollector;
import fi.tkgwf.proteus.snmp.SnmpTarget;
import io.prometheus.client.exporter.MetricsServlet;
import java.net.InetSocketAddress;
import java.util.List;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class PrometheusExporter {

    private final List<SnmpTarget> targets;
    private final int maxSnmpThreads;
    private final long timeoutLimit;

    public PrometheusExporter(List<SnmpTarget> targets, int maxSnmpThreads, long timeoutLimit) {
        this.targets = targets;
        this.maxSnmpThreads = maxSnmpThreads;
        this.timeoutLimit = timeoutLimit;
    }

    public void start(String hostname, int port) {
        Server server = new Server(new InetSocketAddress(hostname, port));
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

        SnmpCollector c = new SnmpCollector(targets, maxSnmpThreads, timeoutLimit);

        c.register();
        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
