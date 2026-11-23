# Schedule Service Build Fix Instructions

## Problem
The Maven build is failing during test compilation with errors indicating that model classes and service classes cannot be found, even though they exist in the source code.

## Root Cause
The issue appears to be related to Maven's test compilation classpath not properly including the compiled main classes. This can happen when:
1. The main classes haven't been compiled yet when tests are being compiled
2. There are dependency resolution issues in the Maven reactor
3. The common-security dependency is not properly resolved

## Solution Steps

### Step 1: Clean and Build Dependencies First
```bash
# From project root
mvn clean install -pl common-security -DskipTests
```

### Step 2: Build Schedule Service Main Classes
```bash
# From project root  
mvn compile -pl schedule-service
```

### Step 3: Run Schedule Service Tests
```bash
# From project root
mvn test -pl schedule-service
```

### Alternative: Full Reactor Build
```bash
# From project root - build everything in correct order
mvn clean install -DskipTests
mvn test -pl schedule-service
```

## Verification
After successful build, verify that these files exist:
- `schedule-service/target/classes/com/svm/ecom/schedule/model/VesselScheduleResponse.class`
- `schedule-service/target/classes/com/svm/ecom/schedule/model/VesselScheduleItem.class`
- `schedule-service/target/classes/com/svm/ecom/schedule/service/VesselService.class`
- `schedule-service/target/classes/com/svm/ecom/schedule/controller/ScheduleController.class`

## Additional Notes
- All source files are present and correct
- The issue is purely a Maven compilation order/classpath problem
- No source code changes are needed
