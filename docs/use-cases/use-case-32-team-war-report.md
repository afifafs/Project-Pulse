# Use Case 32: The Instructor or Student Generates a WAR Report of a Senior Design Team

## Goal
Allow an authorized instructor or student to generate a weekly activity report for a senior design team.

## Primary Actors
Instructor
Student

## Supporting Actors
System

## Trigger
The actor requests a WAR report for a senior design team.

## Preconditions
- The actor is authenticated.
- The selected team exists.
- The actor has permission to view the selected team’s WAR data.
- WAR entries exist for the selected team or the system supports empty report results.

## Postconditions
- The system generates and displays the requested team WAR report.
- No WAR data is modified.

## Main Success Scenario
1. The actor opens the WAR reporting page.
2. The system displays the teams available to that actor.
3. The actor selects a senior design team.
4. The system displays report options such as reporting week, date range, or summary type.
5. The actor chooses the desired report filters and submits the request.
6. The system validates access to the selected team.
7. The system retrieves WAR activity entries for the selected team and reporting scope.
8. The system organizes the activities by week, student, status, category, or hours as needed.
9. The system generates the team WAR report.
10. The system displays the report.

## Extensions
### 6a. Actor is not authorized to view the selected team
1. The system denies access.
2. The use case ends.

### 7a. No WAR entries exist for the selected filters
1. The system displays an empty-state report or message.
2. The use case ends.

### 8a. Report generation fails
1. The system displays an error message.
2. The use case ends without producing the report.

## Business Rules
- Students may generate WAR reports only for teams they belong to or are explicitly authorized to view.
- Instructors may generate WAR reports only for teams within their assigned sections.
- WAR reports are read-only views of underlying activity data.
- Report output may include planned hours, actual hours, activity status, category, and comments according to the viewer’s permissions.
