# Proteus

Proteus is a data exporter for Prometheus, mainly for collecting SNMP data from a group of specified hosts.

Dependencies:

* Maven (For building from sources)
* JDK8 (For building from sources, JRE8 enough for just running the built JAR)

### Building

Execute 

```sh
mvn clean package
```

### "Installation"

* Create directory /opt/proteus/ and place the built JAR file as /opt/proteus/Proteus.jar
* Create a configuration file as /opt/proteus/proteus.properties (see proteus.properties.example for reference)
* Create a (system)user for Proteus:

```sh
useradd -s /bin/false -r -M -d /opt/proteus proteus
```

* The initscript contains a sysvinit initscript, copy it as /etc/init.d/proteus and execute 

```sh
update-rc.d proteus defaults
```

Note: Proteus will look for proteus.properties configuration file in the directory where the JAR file itself is, and one directory above that. This means, during development, you can have your local proteus.properties file in the "root" of the project and execute the JAR directly from the Maven target directory.

### Running

For "installed" version:

```sh
service proteus start
```

For built version (while in the "root" of the project):

```sh
java -jar target/Proteus.jar
```

### Configuring Prometheus

The following should be sufficient for prometheus.yml:

```yaml
  - job_name: 'proteus'
    scrape_interval: 60s
    scrape_timeout: 55s
    target_groups:
      - targets: ['localhost:62222']

```