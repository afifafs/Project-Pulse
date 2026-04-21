# Use Case 31: The Instructor Generates a Peer Evaluation Report of the Entire Senior Design Section

## Goal
Allow an instructor to generate a peer evaluation report that summarizes evaluation results for an entire senior design section.

## Primary Actor
Instructor

## Supporting Actors
System

## Trigger
The instructor requests a section-wide peer evaluation report.

## Preconditions
- The instructor is authenticated.
- The instructor is assigned to the selected senior design section.
- Peer evaluation data exists for at least one reporting period or the system supports empty report results.

## Postconditions
- The system generates and displays the requested section-wide peer evaluation report.
- No peer evaluation data is modified.

## Main Success Scenario
1. The instructor opens the peer evaluation reporting page.
2. The system displays the sections available to the instructor.
3. The instructor selects a senior design section.
4. The system displays report options such as reporting week, date range, or summary level.
5. The instructor chooses the desired report filters and submits the request.
6. The system validates the instructor’s access to the section.
7. The system gathers peer evaluation data for the selected section and reporting scope.
8. The system calculates totals, averages, and any section-level summaries.
9. The system generates the section peer evaluation report.
10. The system displays the report to the instructor.

## Extensions
### 6a. Instructor is not assigned to the selected section
1. The system denies access to the report.
2. The use case ends.

### 7a. No peer evaluation data exists for the selected filters
1. The system generates an empty-state report or message.
2. The use case ends.

### 8a. A report calculation or retrieval error occurs
1. The system displays an error message.
2. The use case ends without producing the report.

## Business Rules
- Instructors may generate reports only for sections they are authorized to access.
- Peer evaluation reports may include only data permitted by role-based visibility rules.
- Private comments must be handled according to reporting permissions and privacy policy.
- Report generation is a read-only action.
