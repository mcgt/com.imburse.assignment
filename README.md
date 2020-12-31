# com.imburse.assignment
Test cases for the Imburse assignment

A. Pre-requisites

On a Windows Environment (or on your computer's OS)

1. Download and install the Oracle JDK & JRE (use a recent version i.e. 1.8 or higher)
2. Download and install Maven 3 (use a recent version, i.e. 3.6 or higher)

Note: Make sure the JAVA_HOME environment variable is set to the path of the JDK. Run 'mvn -version' afterward, Maven should now point to the JDK. You can also add the location of the mvn binary to the PATH - again, this will vary by operating system. Below is a useful guide for the above process on a Windows box.

https://mkyong.com/maven/how-to-install-maven-in-windows/

B. Running the test suite

1. git clone https://github.com/mcgt/com.imburse.assignment.git
2. Navigate to the directory containing the pom.xml (i.e. com.imburse.assignment)
3. Run "mvn clean test site" to run the tests and generate the Surefire report
