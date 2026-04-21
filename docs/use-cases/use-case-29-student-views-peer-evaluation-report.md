# Use Case 29: The Student Views Her Own Peer Evaluation Report

## Goal
Allow a student to review the peer evaluation results that apply to the student for a selected reporting period.

## Primary Actor
Student

## Supporting Actors
System

## Trigger
The student opens the peer evaluation report page.

## Preconditions
- The student is authenticated.
- At least one peer evaluation report exists for the student or the system supports viewing empty states.

## Postconditions
- The student views the requested peer evaluation report data.
- No report data is modified.

## Main Success Scenario
1. The student opens the peer evaluation report page.
2. The system identifies the authenticated student.
3. The system retrieves the peer evaluation results associated with that student.
4. The system displays the available reporting periods or weeks.
5. The student selects a reporting period.
6. The system displays the student’s peer evaluation report, including scores, averages, and visible comments.
7. The student reviews the report.

## Extensions
### 3a. No peer evaluation report exists for the selected period
1. The system informs the student that no report is available.
2. The use case ends.

### 5a. Student chooses a different reporting period
1. The system refreshes the report view for the selected period.
2. The main success scenario resumes at Step 6.

### 6a. Some comments are restricted
1. The system hides private comments that are not visible to the student.
2. The system displays only permitted data in the report.

## Business Rules
- Students may view only their own peer evaluation reports.
- Report visibility must follow comment privacy rules and role permissions.
- Reports may be organized by week, section, or another course-defined reporting unit.
- Viewing a report must not alter peer evaluation records.
