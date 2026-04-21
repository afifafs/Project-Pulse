# Use Case 24: The Admin Reactivates an Instructor

## Goal
Allow an administrator to restore an inactive instructor account so the instructor can regain access to assigned courses, sections, teams, and reporting features.

## Primary Actor
Admin

## Supporting Actors
Instructor
System

## Trigger
An admin decides that a previously inactive instructor should be able to use the system again.

## Preconditions
- The admin is authenticated and has permission to manage instructor accounts.
- The instructor account already exists in the system.
- The instructor account is currently inactive or disabled.

## Postconditions
- The instructor account is marked as active.
- The instructor can sign in and access the sections and reports allowed by the instructor role.
- The system records the account update for auditing purposes.

## Main Success Scenario
1. The admin opens the instructor management page.
2. The system displays a list of instructors and their current account status.
3. The admin selects an inactive instructor account.
4. The system displays the instructor profile, assignment data, and current status.
5. The admin chooses the option to reactivate the instructor.
6. The system validates that the instructor record still exists and is eligible for reactivation.
7. The system changes the instructor account status from inactive to active.
8. The system confirms that the instructor has been reactivated.
9. The system refreshes the instructor record and shows the updated active status.

## Extensions
### 6a. Instructor record cannot be found
1. The system informs the admin that the instructor no longer exists.
2. The use case ends without changes.

### 6b. Instructor account is already active
1. The system informs the admin that no reactivation is required.
2. The use case ends without changes.

### 6c. Admin lacks permission
1. The system denies the action.
2. The system displays an authorization error.
3. The use case ends.

### 7a. Reactivation fails during save
1. The system rolls back the update.
2. The system shows an error message to the admin.
3. The use case ends without changing the instructor status.

## Business Rules
- Only users with administrative privileges may reactivate instructor accounts.
- Reactivation restores access without creating a new instructor record.
- Existing course, section, and team relationships must remain associated with the instructor account.
- The system should preserve the history of when the account was disabled and re-enabled.
