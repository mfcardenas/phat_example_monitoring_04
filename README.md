# PHAT Example Monitoring Activity [04]
Monitoring by acceleration sensor to a user in their home trying to close the refrigerator door while suffering tremors in hands and neck.
<table>
<tr>
    <td>
        <img height="80" width="80" src="https://github.com/mfcardenas/phat_examples/blob/master/img/warning.png" title="Warning"/>
        <img height="80" width="80" src="https://github.com/mfcardenas/phat_examples/blob/master/img/in_progress.png" title="The example is under construction"/>
    </td>
    <td>  
To run the demo

```
mvn clean compile
mvn exec:java -Dexec.mainClass=phat.ActvityMonitoringDemo
```
In case of running into memory problems
```
export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"
```
And then run the previous command
    </td>
    <td>
        <img src="https://github.com/mfcardenas/phat_example_monitoring_04/blob/master/img/img_older_people_home.png" />
    </td>
</tr>
</table>
