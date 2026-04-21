# Use Case 30: The Instructor Sets Up an Instructor Account

## Goal
Allow an instructor to activate an instructor account and establish the credentials needed to manage sections, teams, and reports.

## Primary Actor
Instructor

## Supporting Actors
System

## Trigger
An instructor receives an invitation or onboarding link and begins account setup.

## Preconditions
- An instructor invitation or pre-created instructor record exists.
- The invitation or setup token is valid.
- The instructor account has not already been activated.

## Postconditions
- The instructor account is created or activated.
- The instructor can sign in and access assigned courses and sections.
- The instructor profile is linked to the correct course and section context.

## Main Success Scenario
1. The instructor opens the account setup page from the invitation.
2. The system validates the invitation token or onboarding request.
3. The system displays the instructor account setup form.
4. The instructor enters required profile information.
5. The instructor chooses login credentials.
6. The instructor submits the form.
7. The system validates the submitted data.
8. The system creates or activates the instructor account.
9. The system associates the instructor with the assigned course and section data.
10. The system confirms successful account setup.
11. The system directs the instructor to sign in or continue to the instructor dashboard.

## Extensions
### 2a. Invitation token is invalid or expired
1. The system informs the instructor that the account setup request is invalid.
2. The use case ends without creating the account.

### 7a. Required data is missing or invalid
1. The system highlights the invalid fields.
2. The instructor corrects the form and resubmits.

### 7b. Username or email is already in use
1. The system rejects the duplicate value.
2. The instructor supplies a different value and resubmits.

### 8a. Account activation fails
1. The system displays an error message.
2. The use case ends without activating the account.

## Business Rules
- Instructor accounts may be created only through an authorized invitation or setup process.
- Usernames and email addresses must be unique across all users.
- Passwords must satisfy the system security policy.
- Instructor accounts must inherit the course and section assignments defined by the invitation or administrator setup.
