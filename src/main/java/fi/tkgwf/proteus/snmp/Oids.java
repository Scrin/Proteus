package fi.tkgwf.proteus.snmp;

import java.util.HashMap;
import java.util.Map;

public abstract class Oids {

    final static String ifName = "1.3.6.1.2.1.31.1.1.1.1";
    final static String ifDescr = "1.3.6.1.2.1.2.2.1.2";

    final static String ifHCInOctets = "1.3.6.1.2.1.31.1.1.1.6";
    final static String ifHCOutOctets = "1.3.6.1.2.1.31.1.1.1.10";
    final static String ifInOctets = "1.3.6.1.2.1.2.2.1.10";
    final static String ifOutOctets = "1.3.6.1.2.1.2.2.1.16";

    final static String ifHCInUcastPkts = "1.3.6.1.2.1.31.1.1.1.7";
    final static String ifHCOutUcastPkts = "1.3.6.1.2.1.31.1.1.1.11";
    final static String ifInUcastPkts = "1.3.6.1.2.1.2.2.1.11";
    final static String ifOutUcastPkts = "1.3.6.1.2.1.2.2.1.17";

    final static String ifInDiscards = "1.3.6.1.2.1.2.2.1.13";
    final static String ifOutDiscards = "1.3.6.1.2.1.2.2.1.19";
    final static String ifInErrors = "1.3.6.1.2.1.2.2.1.14";
    final static String ifOutErrors = "1.3.6.1.2.1.2.2.1.20";
    final static String ifInUnknownProtos = "1.3.6.1.2.1.2.2.1.15";

    final static String udpInDatagrams = "1.3.6.1.2.1.7.1.0";
    final static String udpNoPorts = "1.3.6.1.2.1.7.2.0";
    final static String udpInErrors = "1.3.6.1.2.1.7.3.0";
    final static String udpOutDatagrams = "1.3.6.1.2.1.7.4.0";

    final static String tcpActiveOpens = "1.3.6.1.2.1.6.5.0";
    final static String tcpPassiveOpens = "1.3.6.1.2.1.6.6.0";
    final static String tcpAttemptFails = "1.3.6.1.2.1.6.7.0";
    final static String tcpEstabResetss = "1.3.6.1.2.1.6.8.0";
    final static String tcpCurrEstab = "1.3.6.1.2.1.6.9.0";
    final static String tcpInSegs = "1.3.6.1.2.1.6.10.0";
    final static String tcpOutSegs = "1.3.6.1.2.1.6.11.0";
    final static String tcpRetransSegs = "1.3.6.1.2.1.6.12.0";
    final static String tcpInErrs = "1.3.6.1.2.1.6.14.0";
    final static String tcpOutRsts = "1.3.6.1.2.1.6.15.0";

    final static String icmpInMsgs = "1.3.6.1.2.1.5.1.0";
    final static String icmpInErrors = "1.3.6.1.2.1.5.2.0";
    final static String icmpInDestUnreachs = "1.3.6.1.2.1.5.3.0";
    final static String icmpInTimeExcds = "1.3.6.1.2.1.5.4.0";
    final static String icmpInParmProbs = "1.3.6.1.2.1.5.5.0";
    final static String icmpInSrcQuenchs = "1.3.6.1.2.1.5.6.0";
    final static String icmpInRedirects = "1.3.6.1.2.1.5.7.0";
    final static String icmpInEchos = "1.3.6.1.2.1.5.8.0";
    final static String icmpInEchoReps = "1.3.6.1.2.1.5.9.0";
    final static String icmpInTimestamps = "1.3.6.1.2.1.5.10.0";
    final static String icmpInTimestampReps = "1.3.6.1.2.1.5.11.0";
    final static String icmpInAddrMasks = "1.3.6.1.2.1.5.12.0";
    final static String icmpInAddrMaskReps = "1.3.6.1.2.1.5.13.0";

    final static String icmpOutMsgs = "1.3.6.1.2.1.5.14.0";
    final static String icmpOutErrors = "1.3.6.1.2.1.5.15.0";
    final static String icmpOutDestUnreachs = "1.3.6.1.2.1.5.16.0";
    final static String icmpOutTimeExcds = "1.3.6.1.2.1.5.17.0";
    final static String icmpOutParmProbs = "1.3.6.1.2.1.5.18.0";
    final static String icmpOutSrcQuenchs = "1.3.6.1.2.1.5.19.0";
    final static String icmpOutRedirects = "1.3.6.1.2.1.5.20.0";
    final static String icmpOutEchos = "1.3.6.1.2.1.5.21.0";
    final static String icmpOutEchoReps = "1.3.6.1.2.1.5.22.0";
    final static String icmpOutTimestamps = "1.3.6.1.2.1.5.23.0";
    final static String icmpOutTimestampReps = "1.3.6.1.2.1.5.24.0";
    final static String icmpOutAddrMasks = "1.3.6.1.2.1.5.25.0";
    final static String icmpOutAddrMaskReps = "1.3.6.1.2.1.5.26.0";

    final static String hrStorageIndex = "1.3.6.1.2.1.25.2.3.1.1";
    final static String hrStorageDescr = "1.3.6.1.2.1.25.2.3.1.3";
    final static String hrStorageAllocationUnits = "1.3.6.1.2.1.25.2.3.1.4";
    final static String hrStorageSize = "1.3.6.1.2.1.25.2.3.1.5";
    final static String hrStorageUsed = "1.3.6.1.2.1.25.2.3.1.6";
    
    final static String hrSystemUptime = "1.3.6.1.2.1.25.1.1.0";
    final static String hrSystemNumUsers = "1.3.6.1.2.1.25.1.5.0";
    final static String hrSystemProcesses = "1.3.6.1.2.1.25.1.6.0";
    
    final static String hrProcessorLoad = "1.3.6.1.2.1.25.3.3.1.2";

    final static Map<String, String> udpOids;
    final static Map<String, String> tcpOids;
    final static Map<String, String> icmpOids;
    final static Map<String, String> hrStorageOids;
    final static Map<String, String> hrSystemOids;

    static {
        udpOids = new HashMap<>();
        udpOids.put(Oids.udpInDatagrams, "udpInDatagrams");
        udpOids.put(Oids.udpNoPorts, "udpNoPorts");
        udpOids.put(Oids.udpInErrors, "udpInErrors");
        udpOids.put(Oids.udpOutDatagrams, "udpOutDatagrams");
        tcpOids = new HashMap<>();
        tcpOids.put(Oids.tcpActiveOpens, "tcpActiveOpens");
        tcpOids.put(Oids.tcpPassiveOpens, "tcpPassiveOpens");
        tcpOids.put(Oids.tcpAttemptFails, "tcpAttemptFails");
        tcpOids.put(Oids.tcpEstabResetss, "tcpEstabResetss");
        tcpOids.put(Oids.tcpCurrEstab, "tcpCurrEstab");
        tcpOids.put(Oids.tcpInSegs, "tcpInSegs");
        tcpOids.put(Oids.tcpOutSegs, "tcpOutSegs");
        tcpOids.put(Oids.tcpRetransSegs, "tcpRetransSegs");
        tcpOids.put(Oids.tcpInErrs, "tcpInErrs");
        tcpOids.put(Oids.tcpOutRsts, "tcpOutRsts");
        icmpOids = new HashMap<>();
        icmpOids.put(Oids.icmpInMsgs, "icmpInMsgs");
        icmpOids.put(Oids.icmpInErrors, "icmpInErrors");
        icmpOids.put(Oids.icmpInDestUnreachs, "icmpInDestUnreachs");
        icmpOids.put(Oids.icmpInTimeExcds, "icmpInTimeExcds");
        icmpOids.put(Oids.icmpInParmProbs, "icmpInParmProbs");
        icmpOids.put(Oids.icmpInSrcQuenchs, "icmpInSrcQuenchs");
        icmpOids.put(Oids.icmpInRedirects, "icmpInRedirects");
        icmpOids.put(Oids.icmpInEchos, "icmpInEchos");
        icmpOids.put(Oids.icmpInEchoReps, "icmpInEchoReps");
        icmpOids.put(Oids.icmpInTimestamps, "icmpInTimestamps");
        icmpOids.put(Oids.icmpInTimestampReps, "icmpInTimestampReps");
        icmpOids.put(Oids.icmpInAddrMasks, "icmpInAddrMasks");
        icmpOids.put(Oids.icmpInAddrMaskReps, "icmpInAddrMaskReps");
        icmpOids.put(Oids.icmpOutMsgs, "icmpOutMsgs");
        icmpOids.put(Oids.icmpOutErrors, "icmpOutErrors");
        icmpOids.put(Oids.icmpOutDestUnreachs, "icmpOutDestUnreachs");
        icmpOids.put(Oids.icmpOutTimeExcds, "icmpOutTimeExcds");
        icmpOids.put(Oids.icmpOutParmProbs, "icmpOutParmProbs");
        icmpOids.put(Oids.icmpOutSrcQuenchs, "icmpOutSrcQuenchs");
        icmpOids.put(Oids.icmpOutRedirects, "icmpOutRedirects");
        icmpOids.put(Oids.icmpOutEchos, "icmpOutEchos");
        icmpOids.put(Oids.icmpOutEchoReps, "icmpOutEchoReps");
        icmpOids.put(Oids.icmpOutTimestamps, "icmpOutTimestamps");
        icmpOids.put(Oids.icmpOutTimestampReps, "icmpOutTimestampReps");
        icmpOids.put(Oids.icmpOutAddrMasks, "icmpOutAddrMasks");
        icmpOids.put(Oids.icmpOutAddrMaskReps, "icmpOutAddrMaskReps");
        hrStorageOids = new HashMap<>();
        hrStorageOids.put(Oids.hrStorageSize, "hrStorageSize");
        hrStorageOids.put(Oids.hrStorageUsed, "hrStorageUsed");
        hrSystemOids = new HashMap<>();
        hrSystemOids.put(Oids.hrSystemUptime, "hrSystemUptime");
        hrSystemOids.put(Oids.hrSystemNumUsers, "hrSystemNumUsers");
        hrSystemOids.put(Oids.hrSystemProcesses, "hrSystemProcesses");
    }
}
