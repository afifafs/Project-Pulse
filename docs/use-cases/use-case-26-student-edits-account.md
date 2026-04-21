# Use Case 26: The Student Edits an Account

## Goal
Allow a student to update personal account details so profile information, login data, and default context remain accurate.

## Primary Actor
Student

## Supporting Actors
System

## Trigger
A student decides to update account information after signing in.

## Preconditions
- The student is authenticated.
- The student account is active.

## Postconditions
- The account changes are saved successfully.
- The updated profile information is visible to the student.
- Future system actions use the updated account details.

## Main Success Scenario
1. The student opens the account profile page.
2. The system displays the current student account information.
3. The student selects the option to edit the account.
4. The system displays editable account fields.
5. The student updates one or more fields such as name, email, username, password, or default course and section values.
6. The student submits the changes.
7. The system validates the updated information.
8. The system saves the account changes.
9. The system confirms that the account was updated successfully.
10. The student views the updated account profile.

## Extensions
### 7a. Required data is missing or invalid
1. The system highlights the invalid fields.
2. The student corrects the errors and resubmits.

### 7b. New email or username already belongs to another user
1. The system rejects the duplicate value.
2. The student provides a different value and resubmits.

### 8a. Password update violates policy
1. The system displays the password requirements.
2. The student enters a valid password and resubmits.

### 8b. Save operation fails
1. The system displays an error message.
2. The use case ends without saving the changes.

## Business Rules
- Students may edit only their own accounts.
- The system must validate unique usernames and email addresses.
- Password changes must follow the system security policy.
- Changes to default course or section values must be limited to contexts already assigned to the student.
