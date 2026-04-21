# Use Case 34: The Instructor Generates a WAR Report of the Student

## Goal
Allow an instructor to generate a weekly activity report focused on a single student’s activity entries.

## Primary Actor
Instructor

## Supporting Actors
System

## Trigger
The instructor requests a WAR report for a selected student.

## Preconditions
- The instructor is authenticated.
- The instructor is authorized to view the selected student’s team or section data.
- WAR activity entries exist for the student or the system supports empty report results.

## Postconditions
- The requested student WAR report is generated and displayed.
- No activity data is modified.

## Main Success Scenario
1. The instructor opens the WAR reporting page.
2. The system displays the students available to the instructor.
3. The instructor selects a student.
4. The system displays report options such as week, date range, or detail level.
5. The instructor submits the report request.
6. The system validates that the student belongs to an accessible team or section.
7. The system retrieves the student’s WAR activity entries for the selected reporting scope.
8. The system summarizes the student’s activities, hours, categories, and statuses as needed.
9. The system generates the student WAR report.
10. The system displays the report.

## Extensions
### 6a. Selected student is outside the instructor’s access scope
1. The system denies the request.
2. The use case ends.

### 7a. No WAR data exists for the selected filters
1. The system displays an empty-state report or message.
2. The use case ends.

### 8a. Report generation fails
1. The system displays an error message.
2. The use case ends without producing the report.

## Business Rules
- Instructors may generate student WAR reports only for students in their assigned sections or teams.
- WAR report generation is read-only.
- Report output may include week, activity description, planned hours, actual hours, category, status, and comments.
- The report must exclude data for students outside the instructor’s authorized scope.
