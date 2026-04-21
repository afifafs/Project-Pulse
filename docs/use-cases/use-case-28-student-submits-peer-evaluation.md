# Use Case 28: The Student Submits a Peer Evaluation for the Previous Week

## Goal
Allow a student to evaluate teammates for the previous reporting week by entering ratings and comments tied to the course rubric.

## Primary Actor
Student

## Supporting Actors
System
Teammates

## Trigger
The student opens the peer evaluation page for the previous week before the submission deadline.

## Preconditions
- The student is authenticated.
- The student belongs to a team with at least one teammate to evaluate.
- A rubric exists for the section or course.
- The previous week is open for peer evaluation submission.

## Postconditions
- The peer evaluation is stored for the selected week.
- Ratings and comments are linked to the evaluator, evaluatee, and rubric criteria.
- The student can no longer lose submitted data for that evaluation unless the system permits edits.

## Main Success Scenario
1. The student opens the peer evaluation page.
2. The system determines the previous reporting week and the teammates to be evaluated.
3. The system displays the peer evaluation form for one teammate at a time or in a grouped view.
4. The system displays the rubric criteria and score ranges.
5. The student enters ratings for each criterion.
6. The student enters public and optional private comments.
7. The student submits the peer evaluation.
8. The system validates that all required ratings are complete and within allowed ranges.
9. The system calculates or stores the total score.
10. The system saves the peer evaluation for the selected teammate and week.
11. The system confirms successful submission.

## Extensions
### 2a. No teammate is available to evaluate
1. The system informs the student that no peer evaluation is required.
2. The use case ends.

### 8a. One or more ratings are missing or invalid
1. The system highlights the incomplete or invalid criteria.
2. The student corrects the evaluation and resubmits.

### 8b. Submission window is closed
1. The system blocks submission.
2. The system informs the student that the deadline has passed.
3. The use case ends without saving the evaluation.

### 10a. Evaluation for the same teammate and week already exists
1. The system applies the configured duplicate-submission rule.
2. The system either updates the existing evaluation or rejects the new submission with a message.

## Business Rules
- Peer evaluations apply to the previous reporting week.
- Students may evaluate only teammates in their assigned team.
- Scores must align with the rubric criteria and maximum score values.
- Private comments are visible only to authorized roles, while public comments may be included in reports shared with the evaluatee.
