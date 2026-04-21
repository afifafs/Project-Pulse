# Use Case 33: The Instructor Generates a Peer Evaluation Report of a Student

## Goal
Allow an instructor to generate a peer evaluation report focused on a single student.

## Primary Actor
Instructor

## Supporting Actors
System

## Trigger
The instructor requests a peer evaluation report for a selected student.

## Preconditions
- The instructor is authenticated.
- The instructor has permission to view the selected student’s section data.
- Peer evaluation data exists for the student or the system supports empty report results.

## Postconditions
- The requested student peer evaluation report is displayed.
- No peer evaluation data is modified.

## Main Success Scenario
1. The instructor opens the peer evaluation reporting page.
2. The system displays the sections and students available to the instructor.
3. The instructor selects a student.
4. The system displays report options such as week, date range, or summary type.
5. The instructor submits the report request.
6. The system validates that the student belongs to an accessible section.
7. The system retrieves peer evaluation data for the selected student.
8. The system calculates averages, totals, and any trend information needed for the report.
9. The system generates the student peer evaluation report.
10. The system displays the report.

## Extensions
### 6a. Selected student is not accessible to the instructor
1. The system denies the request.
2. The use case ends.

### 7a. No peer evaluation data exists for the selected filters
1. The system displays an empty-state report or message.
2. The use case ends.

### 8a. Report generation fails
1. The system displays an error message.
2. The use case ends without producing the report.

## Business Rules
- Instructors may view student peer evaluation reports only for students in their assigned sections.
- Report visibility must respect public and private comment rules.
- Report generation is a read-only operation.
- The system should present enough detail for instructor review without exposing unauthorized data from unrelated teams or sections.
