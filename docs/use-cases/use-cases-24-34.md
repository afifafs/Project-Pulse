# Use Cases 24-34

This branch implements the student account, instructor account, weekly activity report, and peer evaluation flows for Use Cases 24 through 34 in a combined full-stack workspace.

## Use Case 24: The Admin Reactivates an Instructor
- Actor: Admin
- Goal: Restore an inactive instructor account without recreating it.
- Backend: `PATCH /instructors/{id}/reactivate`
- Frontend: Reactivation panel in the Use Cases 24-34 workspace.

## Use Case 25: The Student Sets Up a Student Account
- Actor: Student
- Goal: Create or complete a student account linked to a section and optional team.
- Backend: `POST /students/accounts/setup`
- Frontend: Student account setup form with section and team assignment.

## Use Case 26: The Student Edits an Account
- Actor: Student
- Goal: Update profile, login, section, and team information.
- Backend: `PUT /students/accounts/{id}`
- Frontend: Student account edit form driven by existing account data.

## Use Case 27: The Student Manages Activities in a Weekly Activity Report
- Actor: Student
- Goal: Create, edit, and delete weekly activity entries.
- Backend:
  - `POST /activities`
  - `PUT /activities/{id}`
  - `DELETE /activities/{id}`
  - `GET /students/{id}/activities`
- Frontend: WAR activity editor with list and inline edit/delete actions.

## Use Case 28: The Student Submits a Peer Evaluation for the Previous Week
- Actor: Student
- Goal: Submit rubric-based scores and comments for a teammate.
- Backend: `POST /peer-evaluations`
- Frontend: Peer evaluation form that loads rubric criteria from the selected student's section.

## Use Case 29: The Student Views Her Own Peer Evaluation Report
- Actor: Student
- Goal: Review received peer evaluation scores and public comments.
- Backend: `GET /students/{id}/peer-evaluation-report`
- Frontend: Student peer report panel with summary and submitted evaluations.

## Use Case 30: The Instructor Sets Up an Instructor Account
- Actor: Instructor
- Goal: Create or complete an instructor account for a section.
- Backend: `POST /instructors/setup`
- Frontend: Instructor account setup form.

## Use Case 31: The Instructor Generates a Peer Evaluation Report of the Entire Senior Design Section
- Actor: Instructor
- Goal: Review section-wide averages and student-level peer evaluation summaries.
- Backend: `GET /instructors/{instructorId}/sections/{sectionId}/peer-evaluation-report`
- Frontend: Section peer report panel with instructor, section, and optional week filters.

## Use Case 32: The Instructor/Student Generates a WAR Report of a Senior Design Team
- Actor: Instructor or Student
- Goal: Review a team-wide WAR summary with student breakdowns.
- Backend: `GET /teams/{teamId}/war-report`
- Frontend: Team WAR report panel with viewer type, viewer, team, and optional week filters.

## Use Case 33: The Instructor Generates a Peer Evaluation Report of a Student
- Actor: Instructor
- Goal: Review a student's received peer evaluations including private comments.
- Backend: `GET /instructors/{instructorId}/students/{studentId}/peer-evaluation-report`
- Frontend: Instructor student peer report panel.

## Use Case 34: The Instructor Generates a WAR Report of the Student
- Actor: Instructor
- Goal: Review a student's weekly activity report entries and hour totals.
- Backend: `GET /instructors/{instructorId}/students/{studentId}/war-report`
- Frontend: Instructor student WAR report panel.

## Implementation Notes
- Demo data is seeded automatically when the database is empty so the new workflows are immediately testable.
- The combined frontend workspace lives in `frontend/src/views/UseCases2434Workspace.vue`.
- The backend additions live under `src/main/java/team/projectpulse/ram` and extend the existing student, team, section, and rubric model instead of introducing a separate application structure.
