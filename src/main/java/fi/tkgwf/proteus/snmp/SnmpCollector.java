package fi.tkgwf.proteus.snmp;

import fi.tkgwf.proteus.service.SnmpService;
import io.prometheus.client.Collector;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.snmp4j.smi.Variable;

public class SnmpCollector extends Collector {

    private final List<SnmpTarget> targets;

    public SnmpCollector(List<SnmpTarget> targets) {
        this.targets = targets;
    }

    @Override
    public List<Collector.MetricFamilySamples> collect() {
        long start = System.currentTimeMillis();
        List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> list = new LinkedList<>(); // TODO: requires rewriting after finding out Prometheus is really picky about the data format
        Map<String, Long> durations = new HashMap<>();
        for (SnmpTarget target : targets) {
            long roundStart = System.currentTimeMillis();
            list.add(measureLatency(target));
            Map<Integer, String> interfaces = walkNames(target, Oids.ifName);
            if (interfaces.isEmpty()) {
                interfaces = walkNames(target, Oids.ifDescr); // fallback to description if the names are not available
            }
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifHCInOctets, Oids.ifInOctets), "ifInOctets"));
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifHCOutOctets, Oids.ifOutOctets), "ifOutOctets"));
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifHCInUcastPkts, Oids.ifInUcastPkts), "ifInUnicastPkts"));
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifHCOutUcastPkts, Oids.ifOutUcastPkts), "ifOutUnicastPkts"));
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifInDiscards), "ifInDiscards"));
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifOutDiscards), "ifOutDiscards"));
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifInErrors), "ifInErrors"));
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifOutErrors), "ifOutErrors"));
            list.add(parseInterfaceValues(target, interfaces, getValuesForWalkedOids(target, interfaces.keySet(), Oids.ifInUnknownProtos), "ifInUnknownProtos"));
            list.addAll(parseSingleValues(target, Oids.udpOids));
            list.addAll(parseSingleValues(target, Oids.tcpOids));
            list.addAll(parseSingleValues(target, Oids.icmpOids));
            list.addAll(parseSingleValues(target, Oids.hrSystemOids));
            list.addAll(parseDiskValues(target));
            list.addAll(parseCpuUsage(target));
            durations.put(target.getHost(), System.currentTimeMillis() - roundStart);
        }
        list.addAll(parseDurationValues(durations));
//        System.out.println("Metrics collected in: " + ((System.currentTimeMillis() - start) / 1000.0d) + " s");
        return combineEntries(list);
    }

    private Entry<String, List<Collector.MetricFamilySamples.Sample>> measureLatency(SnmpTarget target) {
        long start = System.currentTimeMillis();
        try {
            SnmpService.getOid(target, Oids.sysDescr);
        } catch (IOException ex) {
            return new SimpleEntry("latency", new LinkedList());
        }
        long duration = System.currentTimeMillis() - start;
        List<Collector.MetricFamilySamples.Sample> samples = new LinkedList<>();
        List<String> labels = new LinkedList<>();
        List<String> labelValues = new LinkedList<>();
        labels.add("host");
        labelValues.add(target.getHost());
        samples.add(new MetricFamilySamples.Sample("latency", labels, labelValues, duration));
        return new SimpleEntry("latency", samples);
    }

    private Entry<String, List<Collector.MetricFamilySamples.Sample>> parseInterfaceValues(SnmpTarget target, Map<Integer, String> interfaces, Map<Integer, Long> values, String name) {
        List<Collector.MetricFamilySamples.Sample> samples = new LinkedList<>();
        for (Entry<Integer, Long> e : values.entrySet()) {
            List<String> labels = new LinkedList<>();
            List<String> labelValues = new LinkedList<>();
            labels.add("host");
            labelValues.add(target.getHost());
            labels.add("iface");
            labelValues.add(interfaces.get(e.getKey()));
            samples.add(new Collector.MetricFamilySamples.Sample(name, labels, labelValues, e.getValue()));
        }
        return new SimpleEntry(name, samples);
    }

    private List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> parseDiskValues(SnmpTarget target) {
        List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> result = new LinkedList<>();
        Map<Integer, String> diskIndexes = walkNames(target, Oids.hrStorageIndex);
        Map<Integer, String> diskNames = getStringValuesForWalkedOids(target, diskIndexes.keySet(), Oids.hrStorageDescr);
        Map<Integer, Long> diskAllocSizes = getValuesForWalkedOids(target, diskIndexes.keySet(), Oids.hrStorageAllocationUnits);

        for (Entry<String, String> oidGroup : Oids.hrStorageOids.entrySet()) {
            List<Collector.MetricFamilySamples.Sample> samples = new LinkedList<>();
            Map<Integer, Long> walkedValues = getValuesForWalkedOids(target, diskIndexes.keySet(), oidGroup.getKey());
            for (Entry<Integer, String> e : diskIndexes.entrySet()) {
                String diskName = diskNames.get(e.getKey());
                if (diskName.startsWith("MALLOC: ") || diskName.startsWith("UMA: ")) { // Some weird shits we're not interested in
                    continue;
                }
                List<String> labels = new LinkedList<>();
                List<String> labelValues = new LinkedList<>();
                labels.add("host");
                labelValues.add(target.getHost());
                labels.add("disk");
                labelValues.add(diskName);
                Long value = walkedValues.get(e.getKey()) * diskAllocSizes.get(e.getKey());
                samples.add(new Collector.MetricFamilySamples.Sample(oidGroup.getValue(), labels, labelValues, value));
            }
            result.add(new SimpleEntry(oidGroup.getValue(), samples));
        }
        return result;
    }

    private List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> parseSingleValues(SnmpTarget target, Map<String, String> oidsNames) {
        List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> result = new LinkedList<>();
        try {
            Map<String, Variable> oids = SnmpService.getOids(target, oidsNames.keySet());

            for (Entry<String, Variable> entry : oids.entrySet()) {
                int syntax = entry.getValue().getSyntax();
                if (syntax == 65 || syntax == 66 || syntax == 67) {
                    List<Collector.MetricFamilySamples.Sample> samples = new LinkedList<>();
                    List<String> labels = new LinkedList<>();
                    List<String> labelValues = new LinkedList<>();
                    labels.add("host");
                    labelValues.add(target.getHost());
                    String name = oidsNames.get(entry.getKey());
                    samples.add(new Collector.MetricFamilySamples.Sample(name, labels, labelValues,
                            // super ghettohack to get uptime as normal milliseconds instead of retarded centiseconds
                            name.equals("hrSystemUptime") ? entry.getValue().toLong() * 10 : entry.getValue().toLong()));
                    result.add(new SimpleEntry(name, samples));
                }
            }
        } catch (IOException ex) {
        }
        return result;
    }

    private List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> parseCpuUsage(SnmpTarget target) {
        List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> result = new LinkedList<>();
        List<Collector.MetricFamilySamples.Sample> samples = new LinkedList<>();
        Map<Integer, String> loadCores = walkNames(target, Oids.hrProcessorLoad);

        Map<Integer, Long> walkedValues = getValuesForWalkedOids(target, loadCores.keySet(), Oids.hrProcessorLoad);
        int coreNumber = 0;
        int loadTotal = 0;
        for (Integer i : loadCores.keySet()) {
            List<String> labels = new LinkedList<>();
            List<String> labelValues = new LinkedList<>();
            labels.add("host");
            labelValues.add(target.getHost());
            labels.add("type");
            labelValues.add("core");
            labels.add("core");
            labelValues.add(String.valueOf(coreNumber));
            samples.add(new Collector.MetricFamilySamples.Sample("hrProcessorLoad", labels, labelValues, walkedValues.get(i)));
            coreNumber++;
            loadTotal += walkedValues.get(i);
        }
        if (coreNumber > 0) {
            List<String> labels = new LinkedList<>();
            List<String> labelValues = new LinkedList<>();
            labels.add("host");
            labelValues.add(target.getHost());
            labels.add("type");
            labelValues.add("average");
            samples.add(new Collector.MetricFamilySamples.Sample("hrProcessorLoad", labels, labelValues, ((double) loadTotal / (double) coreNumber)));
            result.add(new SimpleEntry("hrProcessorLoad", samples));
        }
        return result;
    }

    private List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> parseDurationValues(Map<String, Long> hosts) {
        List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> result = new LinkedList<>();
        List<Collector.MetricFamilySamples.Sample> samples = new LinkedList<>();
        for (Entry<String, Long> entry : hosts.entrySet()) {
            List<String> labels = new LinkedList<>();
            List<String> labelValues = new LinkedList<>();
            labels.add("host");
            labelValues.add(entry.getKey());
            samples.add(new Collector.MetricFamilySamples.Sample("pollerExecutionDuration", labels, labelValues, entry.getValue()));
        }
        result.add(new SimpleEntry("pollerExecutionDuration", samples));
        return result;
    }

    /**
     * Walks the given OID, returning the numbers and names of walked targets
     *
     * @param target Target to walk on
     * @param oid OID to walk
     * @return Map containing the walked numbers as keys, and names as values
     */
    private Map<Integer, String> walkNames(SnmpTarget target, String oid) {
        Map<String, Variable> names;
        try {
            names = SnmpService.walk(target, oid);
            Map<Integer, String> result = new HashMap<>();
            for (Entry<String, Variable> e : names.entrySet()) {
                result.put(Integer.valueOf(e.getKey().substring(oid.length() + 1)), e.getValue().toString());
            }
            return result;
        } catch (IOException ex) {
            return null;
        }
    }

    private Map<Integer, Long> getValuesForWalkedOids(SnmpTarget target, Set<Integer> nums, String primaryOid, String secondaryOid) {
        if (nums == null || nums.isEmpty()) {
            return new HashMap<>();
        }
        try {
            Set<String> hcOids = new HashSet<>();
            for (Integer i : nums) {
                hcOids.add(primaryOid + "." + i);
            }
            Map<String, Variable> oids = SnmpService.getOids(target, hcOids);
            Map<Integer, Long> result = new HashMap<>();
            Set<String> failed = new HashSet<>();
            for (Entry<String, Variable> entry : oids.entrySet()) {
                Integer num = Integer.valueOf(entry.getKey().substring(primaryOid.length() + 1));
                if (entry.getValue().getSyntax() == 70) { // Counter64
                    result.put(num, entry.getValue().toLong());
                } else {
                    failed.add(secondaryOid + "." + num);
                }
            }
            if (oids.isEmpty()) {
                for (Integer i : nums) {
                    failed.add(secondaryOid + "." + i);
                }
            }
            if (!failed.isEmpty()) {
                oids = SnmpService.getOids(target, failed);
                for (Entry<String, Variable> entry : oids.entrySet()) {
                    Integer num = Integer.valueOf(entry.getKey().substring(secondaryOid.length() + 1));
                    if (entry.getValue().getSyntax() == 65) { // Counter
                        result.put(num, entry.getValue().toLong());
                    }
                }
            }
            return result;
        } catch (IOException ex) {
            return new HashMap<>();
        }
    }

    private Map<Integer, Long> getValuesForWalkedOids(SnmpTarget target, Set<Integer> nums, String oid) {
        if (nums == null || nums.isEmpty()) {
            return new HashMap<>();
        }
        try {
            Set<String> oids = new HashSet<>();
            for (Integer i : nums) {
                oids.add(oid + "." + i);
            }
            Map<String, Variable> oidsResult = SnmpService.getOids(target, oids);
            Map<Integer, Long> result = new HashMap<>();
            for (Entry<String, Variable> entry : oidsResult.entrySet()) {
                Integer num = Integer.valueOf(entry.getKey().substring(oid.length() + 1));
                if (entry.getValue().getSyntax() == 2 || entry.getValue().getSyntax() == 65) { // 2 = Integer32, 65 = Counter
                    result.put(num, entry.getValue().toLong());
                }
            }
            return result;
        } catch (IOException ex) {
            return new HashMap<>();
        }
    }

    private Map<Integer, String> getStringValuesForWalkedOids(SnmpTarget target, Set<Integer> nums, String oid) {
        if (nums == null || nums.isEmpty()) {
            return new HashMap<>();
        }
        try {
            Set<String> oids = new HashSet<>();
            for (Integer i : nums) {
                oids.add(oid + "." + i);
            }
            Map<String, Variable> in = SnmpService.getOids(target, oids);
            Map<Integer, String> result = new HashMap<>();
            for (Entry<String, Variable> entry : in.entrySet()) {
                Integer num = Integer.valueOf(entry.getKey().substring(oid.length() + 1));
                result.put(num, entry.getValue().toString());
            }
            return result;
        } catch (IOException ex) {
            return new HashMap<>();
        }
    }

    private List<Collector.MetricFamilySamples> combineEntries(List<Entry<String, List<Collector.MetricFamilySamples.Sample>>> list) {
        Map<String, List<Collector.MetricFamilySamples.Sample>> map = new HashMap<>();
        for (Entry<String, List<MetricFamilySamples.Sample>> e : list) {
            List<MetricFamilySamples.Sample> existing = map.get(e.getKey());
            if (existing == null) {
                map.put(e.getKey(), e.getValue());
            } else {
                existing.addAll(e.getValue());
            }
        }
        List<Collector.MetricFamilySamples> result = new LinkedList<>();
        for (Entry<String, List<Collector.MetricFamilySamples.Sample>> e : map.entrySet()) {
            result.add(new MetricFamilySamples(e.getKey(), Type.GAUGE, e.getKey(), e.getValue()));
        }
        return result;
    }
}
