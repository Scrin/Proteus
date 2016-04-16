package fi.tkgwf.proteus.service;

import fi.tkgwf.proteus.snmp.SnmpTarget;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SnmpService {

    /**
     * Gets values for given OIDs
     *
     * @param target Target to get the values from
     * @param oids The OIDs to retrieve values for
     * @return Map containing the OIDs and their values
     * @throws IOException Thrown if a connection error occurs
     */
    public static Map<String, Variable> getOids(SnmpTarget target, Set<String> oids) throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        Map<String, Variable> result = new HashMap<>();
        try {
            Snmp snmp = new Snmp(transport);
            transport.listen();

            CommunityTarget communityTarget = createCommunityTarget(target);
            Iterator<String> it = oids.iterator();
            List<String> oidsList = new LinkedList<>();
            while (it.hasNext()) {
                oidsList.clear();
                for (int i = 0; i < 50 && it.hasNext(); i++) {
                    oidsList.add(it.next());
                }
                PDU pdu = new PDU();
                for (String s : oidsList) {
                    pdu.add(new VariableBinding(new OID(s)));
                }
                pdu.setType(PDU.GET);
                ResponseEvent responseEvent = snmp.send(pdu, communityTarget);
                if (responseEvent == null || responseEvent.getResponse() == null) {
                    return result;
                }
                for (Object o : responseEvent.getResponse().getVariableBindings()) {
                    if (o instanceof VariableBinding) {
                        VariableBinding vb = (VariableBinding) o;
                        result.put(vb.getOid().toString(), vb.getVariable());
//                        System.out.println("        OIDS    " + target.address + " " + vb.getOid().toString() + " = " + vb.getVariable().getSyntaxString() + " = " + vb.getVariable().getSyntax() + " = " + vb.getVariable().toString());
                    }
                }
            }
        } finally {
            transport.close();
        }
        return result;
    }

    /**
     * Walks the specified OID
     *
     * @param target Target to get the values from
     * @param oid The OID to walk
     * @return Map containing the OID's and their values from the walk
     * @throws IOException Thrown if a connection error occurs
     */
    public static Map<String, Variable> walk(SnmpTarget target, String oid) throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        Map<String, Variable> result = new HashMap<>();
        try {
            Snmp snmp = new Snmp(transport);
            transport.listen();

            CommunityTarget communityTarget = createCommunityTarget(target);

            TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> events = treeUtils.getSubtree(communityTarget, new OID(oid));

            if (events == null) {
                System.out.println("helvetti");
                return result;
            }
            for (TreeEvent event : events) {
                if (event != null) {
                    if (event.isError()) {
                        System.err.println("Error with oid " + oid + " " + event.getErrorMessage());
                    }

                    VariableBinding[] varBindings = event.getVariableBindings();
                    if (varBindings == null) {
                        continue;
                    }
                    for (VariableBinding vb : varBindings) {
//                        System.out.println("        WALK    " + target.address + " " + vb.getOid() + " = " + vb.getVariable());
                        result.put(vb.getOid().toString(), vb.getVariable());
                    }
                }
            }
        } finally {
            transport.close();
        }
        return result;
    }

    private static CommunityTarget createCommunityTarget(SnmpTarget target) {
        CommunityTarget communityTarget = new CommunityTarget();
        switch (target.version) {
            case "1":
                communityTarget.setVersion(SnmpConstants.version1);
                break;
            case "2c":
                communityTarget.setVersion(SnmpConstants.version2c);
                break;
            default:
                throw new IllegalArgumentException("Unsupported SNMP version \"" + target.version + "\". Supported versions are \"1\" and \"2c\".");
        }
        communityTarget.setAddress(GenericAddress.parse(target.address));
        communityTarget.setCommunity(new OctetString(target.community));
        communityTarget.setRetries(5);
        communityTarget.setTimeout(5000);
        return communityTarget;
    }
}
