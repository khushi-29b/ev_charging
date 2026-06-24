⚡ EV Charging Network — Setup Guide
Stack: HTML/CSS/JS + Java Servlets + MySQL
---
STEP 1 — Prerequisites install karo
Tool	Download
JDK 17+	https://adoptium.net
Apache Tomcat 10	https://tomcat.apache.org/download-10.cgi
MySQL 8	https://dev.mysql.com/downloads/
VS Code	https://code.visualstudio.com
VS Code Extensions:
Extension Pack for Java (Microsoft)
Tomcat for Java (by Wei Shen)
---
STEP 2 — MySQL Database setup
MySQL Workbench ya terminal mein:
```sql
-- Pehle database banao
CREATE DATABASE ev_charging_network;
USE ev_charging_network;

-- Ab ev_charging_network.sql run karo (already given)
SOURCE /path/to/ev_charging_network.sql;
```
---
STEP 3 — Password set karo
`src/com/ev/util/DBConnection.java` mein apna MySQL password daalo:
```java
private static final String PASS = "yourpassword";  // <-- yahan
```
---
STEP 4 — JAR files download karo
Ye 2 JARs chahiye. `WebContent/WEB-INF/lib/` folder mein rakhna:
JAR	Download Link
mysql-connector-j-8.x.jar	https://dev.mysql.com/downloads/connector/j/
gson-2.10.1.jar	https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
---
STEP 5 — Project compile karo
VS Code terminal mein (project root se):
```bash
# Windows
javac -cp "WebContent/WEB-INF/lib/*;path/to/tomcat/lib/servlet-api.jar" ^
  -d WebContent/WEB-INF/classes ^
  src/com/ev/util/DBConnection.java ^
  src/com/ev/util/CORSFilter.java ^
  src/com/ev/model/*.java ^
  src/com/ev/dao/*.java ^
  src/com/ev/servlet/*.java

# Mac/Linux
javac -cp "WebContent/WEB-INF/lib/*:path/to/tomcat/lib/servlet-api.jar" \
  -d WebContent/WEB-INF/classes \
  src/com/ev/util/DBConnection.java \
  src/com/ev/util/CORSFilter.java \
  src/com/ev/model/*.java \
  src/com/ev/dao/*.java \
  src/com/ev/servlet/*.java
```
`path/to/tomcat` = jahan Tomcat install kiya hai woh path
Example Windows: `C:/apache-tomcat-10.1.18`
---
STEP 6 — WAR banao aur Tomcat mein deploy karo
```bash
# ev-charging folder ke andar se:
cd WebContent
jar -cvf ../ev-charging.war .
```
Ab `ev-charging.war` file ko:
`apache-tomcat-10.x/webapps/` folder mein copy karo
---
STEP 7 — Tomcat start karo
```bash
# Windows
apache-tomcat-10.x/bin/startup.bat

# Mac/Linux
apache-tomcat-10.x/bin/startup.sh
```
---
STEP 8 — Browser mein open karo
```
http://localhost:8080/ev-charging/
```
---
Project Structure (Final)
```
ev-charging/
├── src/com/ev/
│   ├── util/
│   │   ├── DBConnection.java      ← MySQL connection
│   │   └── CORSFilter.java        ← CORS headers
│   ├── model/
│   │   ├── ChargingStation.java
│   │   ├── User.java
│   │   ├── Vehicle.java
│   │   ├── ChargingSession.java
│   │   ├── MaintenanceTicket.java
│   │   └── Payment.java
│   ├── dao/
│   │   ├── StationDAO.java        ← DB queries
│   │   ├── UserDAO.java
│   │   ├── VehicleDAO.java
│   │   ├── SessionDAO.java
│   │   ├── TicketDAO.java
│   │   └── PaymentDAO.java
│   └── servlet/
│       ├── StationServlet.java    ← REST APIs
│       ├── UserServlet.java
│       ├── VehicleServlet.java
│       ├── SessionServlet.java
│       ├── TicketServlet.java
│       └── PaymentServlet.java
│
└── WebContent/
    ├── WEB-INF/
    │   ├── web.xml                ← Servlet config
    │   ├── classes/               ← Compiled .class files (auto)
    │   └── lib/
    │       ├── mysql-connector-j.jar
    │       └── gson-2.10.1.jar
    ├── css/
    │   └── style.css
    ├── js/
    │   └── app.js
    └── index.html
```
---
API Endpoints Summary
Method	URL	Description
GET	/api/stations	All stations
POST	/api/stations	Add station
DELETE	/api/stations/{id}	Delete station
GET	/api/users	All users
POST	/api/users	Register user
PUT	/api/users/{id}/kyc?status=Verified	Update KYC
GET	/api/vehicles	All vehicles
GET	/api/vehicles?user=1	Vehicles by user
POST	/api/vehicles	Add vehicle
GET	/api/sessions	All sessions
POST	/api/sessions	Start session
POST	/api/sessions/end	End session
GET	/api/payments	All payments
POST	/api/payments	Record payment
GET	/api/tickets	All tickets
POST	/api/tickets	Open ticket
POST	/api/tickets/close	Close ticket
---
Common Errors & Fixes
Error	Fix
`ClassNotFoundException: com.mysql.cj.jdbc.Driver`	mysql-connector JAR WEB-INF/lib mein nahi hai
`404 Not Found`	WAR deploy nahi hua, webapps folder check karo
`500 Internal Server Error`	DB password galat hai DBConnection.java mein
`Access-Control-Allow-Origin` error	CORSFilter.java compile hua ya nahi check karo
Port 8080 busy	`server.xml` mein port 8081 karo ya dusra process band karo
