package fi.tkgwf.proteus.snmp;

public class SnmpTarget {

    /**
     * Address in format protocol:host/port
     */
    public final String address;
    /**
     * SNMP version
     */
    public final String version;
    /**
     * Community string
     */
    public final String community;

    public SnmpTarget(String address, String version, String community) {
        this.address = address;
        this.version = version;
        this.community = community;
    }

    /**
     * Gets the host
     *
     * @return hostname or IP of the target
     */
    public String getHost() {
        return address.split(":")[1].split("/")[0];
    }
}
