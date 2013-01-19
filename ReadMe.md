maven dependency:

        <dependency>
            <groupId>com.github.danny02</groupId>
            <artifactId>JOpenCTM</artifactId>
            <version>1.1</version>
        </dependency>
        
To use the latest snap-shot build use this in your pom:

        <dependency>
            <groupId>com.github.danny02</groupId>
            <artifactId>JOpenCTM</artifactId>
            <version>1.2-SNAPSHOT</version>
        </dependency>
        
and also this repository:
        
        <repository>
                <id>sonatype-nexus-snapshots</id>
                <name>Sonatype Nexus Snapshots</name>
                <url>https://oss.sonatype.org/content/repositories/snapshots</url>
                <snapshots>
                        <enabled>true</enabled>
                </snapshots>
        </repository>

