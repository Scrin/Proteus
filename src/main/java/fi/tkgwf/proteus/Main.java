package fi.tkgwf.proteus;

import fi.tkgwf.proteus.service.PrometheusExporter;
import fi.tkgwf.proteus.snmp.SnmpTarget;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Main {

    private static final List<SnmpTarget> targets = new LinkedList<>();

    public static void main(String[] args) throws IOException, URISyntaxException {
        String hostname = "localhost";
        int port = 62222;
        // Load properties
        File jarLocation = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        File[] configFiles = jarLocation.listFiles((File f) -> f.isFile() && f.getName().equals("proteus.properties"));
        if (configFiles == null || configFiles.length == 0) {
            configFiles = jarLocation.getParentFile().listFiles((File f) -> f.isFile() && f.getName().equals("proteus.properties"));
        }
        if (configFiles == null || configFiles.length == 0) {
            System.err.println("No config file found!");
            System.exit(1);
        }
        System.out.println("Config: " + configFiles[0]);
        Properties props = new Properties();
        props.load(new FileInputStream(configFiles[0]));
        Enumeration<?> e = props.propertyNames();
        // Parse properties
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = props.getProperty(key);

            // Host configs
            if (key.startsWith("snmp.target.") && value.split("/").length >= 4) {
                String[] split = value.split("/", 4);
                targets.add(new SnmpTarget(split[0] + ":" + key.substring(12) + "/" + split[1], split[2], split[3]));
                System.out.println("Snmp host: " + key.substring(12));
            } else if (key.equals("listen.host")) {
                hostname = value;
            } else if (key.equals("listen.port")) {
                try {
                    port = Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    System.err.println("WARNING: Invalid number format for port: '" + value + '\'');
                }
            }
        }
        // Start the exporter
        PrometheusExporter pe = new PrometheusExporter(targets);
        pe.start(hostname, port);
    }
}
