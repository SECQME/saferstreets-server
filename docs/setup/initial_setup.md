
## Sections
* [Setup Environment](#setupEnvironment)
* [Setup Project](#setupProject)
* [Run Locally](#runLocally)
* [Deploy Production](#deployProduction)

## Setup Environment <a name="setupEnvironment"></a>

### Install Java
1. Download Java SE Development from here <http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>
2. After install, type the following in terminal to confirm that Java Environment is running perfectly
```
    java -version
```
Expected outcome
```
    java version "1.8.0_60"
```

### Install Homebrew
1. Simply past the following command in terminal
```
    /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```
2. Then run the following code to update the library
```
    brew update
    brew doctor
```

### Install Apache - Ant
1. Install Ant using homebrew
```
    brew install ant
```
2. Then in terminal type
```
    ant -version
```
Expected outcome
```
    Apache Ant(TM) version <VERSION NUMBER> compiled on <MONTH> <DATE> <YEAR>
```
If you get this error
```
    -bash: ant: command not found
```
Run the following command
```
    brew link ant
```

### Install Postgres
1. Install Postgres with homebrew
```
    brew install postgres
```
2. For further install on postgis please refer <EDWARD POSTGIS>

### Install Apache - Tomcat
1. Install Tomcat with homebrew
```
    brew install tomcat
```
2. Start Apache Tomcat
```
    /usr/local/Cellar/tomcat/7.X.X/bin/catalina start
```
3. If you're using `NetBean` / `Eclipse` IDEs, in terminal type
```
    export CATALINA_HOME=/usr/local/Cellar/tomcat/7.X.X/libexec
```
---
## Setup Project <a name="setupProject"></a>

### Github
1. Download the source code from github <https://github.com/SECQME/WOMCrimeDataEngine/archive/master.zip>
2. Clone the project to local. In terminal change to the prefer directory then type:
```
    git clone https://github.com/SECQME/WOMCrimeDataEngine.git
```
## Run Locally <a name="runLocally"></a>
1. After the download is finished,type the following command in terminal,

Change Directory to `WOMCrimeDataEngine`
```
    cd WOMCrimeDataEngine
```
Create Directory
```
    mkdir build
    mkdir dist
```
Initiate and prepare the project
```
    sudo ant init
    sudo ant clean war
```
Copy builded war file to tomcat
```
    cp /dist/crimereport.war /usr/local/Cellar/tomcat/7.X.X/libexec/webapps/
```
Start Tomcat
```
    /usr/local/Cellar/tomcat/7.X.X/bin/catalina start
```
Stop Tomcat
```
    /usr/local/Cellar/tomcat/7.X.X/bin/catalina stop
```

## Deploy to Production <a name="deployProduction"></a>

Run the following code the deploy to Production
```
    sudo ant clean aws-eb-deploy rollbar-deploy -Dbuild.env=production
```
