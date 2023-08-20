# Reimbursement Calculation Application

## How to set up the environment on Windows System
### Backend application
1. Install java 11. In this tutorial I will show results for java 20.
2. Check if JAVA_HOME is present in your environment variables. Add if it is not.
```text
JAVA_HOME   D:\Program Files\Java\jdk-20
```
3. Install maven.
4. Check if maven path is present in your PATH environment variable. Add if it is not.
```text
PATH    D:\Program Files\apache-maven-3.9.3\bin
```
5. Restart terminal.
6. Check your java version.
```text
PS C:\Users\jbart> java --version
java 20.0.1 2023-04-18
Java(TM) SE Runtime Environment (build 20.0.1+9-29)
Java HotSpot(TM) 64-Bit Server VM (build 20.0.1+9-29, mixed mode, sharing)
```
7. Check your maven version
```text
PS C:\Users\jbart> mvn -v
Apache Maven 3.9.3 (21122926829f1ead511c958d89bd2f672198ae9f)
Maven home: D:\Program Files\apache-maven-3.9.3
Java version: 20.0.1, vendor: Oracle Corporation, runtime: D:\Program Files\Java\jdk-20
Default locale: pl_PL, platform encoding: UTF-8
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```

### Frontend application
1. Install node.js.
2. Check if nodejs path is present in your PATH environment variable. Add if it is not.
```text
C:\Program Files\nodejs\
```
3. Also there should be present npm in your PATH variable. Add if it is not.
```text
C:\Users\jbart\AppData\Roaming\npm
```
4. Restart terminal. 
5. Check if node is working.
```text
PS C:\Users\jbart> node --version
v19.7.0
```
6. Check if npm is working.
```text
PS C:\Users\jbart> npm --version
9.5.0
```
## How to build the application
### Backend application 
1. Change directory into the root directory.
2. Run *mvn install*.
3. Run *java -jar .\application\target\application-1.0-SNAPSHOT-jar-with-dependencies.jar*.

### Frontend application
1. Change directory into the root directory.
2. Run *npm run build*.
3. Run *npm install -g serve*
4. Run serve -s build

## How to run the tests
### Backend application
1. Change directory into the root directory.
2. Run *mvn test*.

### Frontend application
Not required in the task.

## Endpoints
### Backend application
- GET, POST, OPTIONS http://localhost:8500/admin
- GET, POST, OPTIONS http://localhost:8500/user

### Frontend application
- http://localhost:3000/
- http://localhost:3000/user
- http://localhost:3000/admin

## Bonus - theoretical task (not mandatory):
Propose a solution on how to integrate into your Java application the possibility to add some calculation rules (e.g. limits, total amount of reimbursement based on different conditions) using JavaScript.

I decided to use React on the front end, so I can easily tract current state of the form.
The mechanism of state is used to ensure that proper HtmlElements are inserted into the browser window.
The current total value in the user's reimbursement form is recalculated on each action:
- Distance changed
- Receipt added
- Allowance days changed

Similarly, a rule for each field should be run when form field(depending on its type):

- Changes value
- Looses focus
- Gets deleted

Depending on the outcome of the field validation, state should either be changed or remain the same.
In an event of a failure, an error message should be displayed.



