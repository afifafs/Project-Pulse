# Use Case 25: The Student Sets Up a Student Account

## Goal
Allow a student to activate a student account and establish the credentials and profile details needed to participate in weekly activity reports and peer evaluations.

## Primary Actor
Student

## Supporting Actors
System

## Trigger
A student receives an invitation or first-time access link and begins account setup.

## Preconditions
- A student invitation or pre-created student record exists.
- The student has access to the invitation email, token, or onboarding link.
- The account has not already been fully activated.

## Postconditions
- The student account is created or activated successfully.
- The student can sign in with the new credentials.
- The student profile is associated with the proper course, section, and team when applicable.

## Main Success Scenario
1. The student opens the account setup page from the invitation link.
2. The system validates the invitation token or setup request.
3. The system displays the student account setup form.
4. The student enters the required personal information.
5. The student chooses a username and password.
6. The student confirms the password and submits the form.
7. The system validates the submitted information.
8. The system creates or activates the student account.
9. The system associates the student with the appropriate course, section, and team data from the invitation.
10. The system confirms successful account setup.
11. The system directs the student to sign in or enters the student dashboard.

## Extensions
### 2a. Invitation token is invalid or expired
1. The system informs the student that the setup request is no longer valid.
2. The use case ends without creating the account.

### 7a. Required information is missing
1. The system highlights the missing fields.
2. The student corrects the form and resubmits.

### 7b. Username or email is already in use
1. The system informs the student that the credentials are unavailable.
2. The student enters different information and resubmits.

### 8a. Account creation fails
1. The system shows an error message.
2. The use case ends without activating the account.

## Business Rules
- Student accounts may be created only from a valid invitation or approved onboarding path.
- Usernames and email addresses must be unique in the system.
- Passwords must satisfy the system security policy.
- Student accounts must inherit the course, section, and team context defined by the invitation or enrollment record.
