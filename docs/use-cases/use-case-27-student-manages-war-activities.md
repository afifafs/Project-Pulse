# Use Case 27: The Student Manages Activities in a Weekly Activity Report (WAR)

## Goal
Allow a student to create, edit, review, and remove activity entries for a weekly activity report before final submission or deadline.

## Primary Actor
Student

## Supporting Actors
System

## Trigger
The student opens the weekly activity report for the current reporting period.

## Preconditions
- The student is authenticated.
- The student belongs to a team and section that uses WAR reporting.
- The reporting week is active or still editable.

## Postconditions
- The student’s WAR activities are stored for the selected week.
- The current WAR reflects the latest activity data entered by the student.

## Main Success Scenario
1. The student opens the weekly activity report page.
2. The system displays the WAR entries already associated with the selected week.
3. The student chooses to add a new activity or edit an existing one.
4. The system displays the activity form with fields for the activity name, description, category, status, planned hours, actual hours, comments, and week.
5. The student enters or updates the activity details.
6. The student submits the activity entry.
7. The system validates the activity information.
8. The system saves the activity entry to the student’s WAR.
9. The system refreshes the WAR and displays the updated list of activities.
10. The student repeats the process until the WAR is complete.

## Extensions
### 3a. Student deletes an activity entry
1. The student selects an activity to remove.
2. The system asks for confirmation.
3. The student confirms the deletion.
4. The system removes the activity from the WAR and refreshes the report.

### 7a. Required fields are missing or invalid
1. The system highlights the invalid fields.
2. The student corrects the information and resubmits.

### 7b. Reporting week is no longer editable
1. The system denies the change.
2. The system informs the student that the WAR is locked or past due.
3. The use case ends without saving changes.

### 8a. Save fails
1. The system displays an error message.
2. The use case ends without saving the attempted changes.

## Business Rules
- Activities must be tied to a specific reporting week.
- Activity categories and statuses must match the values supported by the system.
- Students may manage only their own WAR activity entries.
- A locked or expired reporting period must prevent further modification unless reopened by an authorized user.
