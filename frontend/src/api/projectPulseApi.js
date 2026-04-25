const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  const contentType = response.headers.get('content-type') ?? ''
  const data = contentType.includes('application/json') ? await response.json() : null

  if (!response.ok) {
    const message = data?.message || data?.error || `Request failed with status ${response.status}`
    throw new Error(message)
  }

  return data
}

function withQuery(path, params = {}) {
  const query = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && `${value}`.trim() !== '') {
      query.set(key, value)
    }
  })

  const queryString = query.toString()
  return `${path}${queryString ? `?${queryString}` : ''}`
}

export function getSections(name = '') {
  return request(withQuery('/sections', { name }))
}

export function getSectionDetails(id) {
  return request(`/sections/${id}`)
}

export function getTeams(sectionName = '', teamName = '') {
  return request(withQuery('/teams', { sectionName, teamName }))
}

export function getStudentAccounts() {
  return request('/students/accounts')
}

export function setupStudentAccount(payload) {
  return request('/students/accounts/setup', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateStudentAccount(id, payload) {
  return request(`/students/accounts/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function getInstructors() {
  return request('/instructors')
}

export function setupInstructorAccount(payload) {
  return request('/instructors/setup', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function reactivateInstructor(id) {
  return request(`/instructors/${id}/reactivate`, {
    method: 'PATCH',
  })
}

export function createActivity(payload) {
  return request('/activities', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateActivity(id, payload) {
  return request(`/activities/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function deleteActivity(id) {
  return request(`/activities/${id}`, {
    method: 'DELETE',
  })
}

export function getStudentActivities(studentId, weekNumber = '') {
  return request(withQuery(`/students/${studentId}/activities`, { weekNumber }))
}

export function submitPeerEvaluation(payload) {
  return request('/peer-evaluations', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function getStudentPeerEvaluationReport(studentId, weekNumber = '') {
  return request(withQuery(`/students/${studentId}/peer-evaluation-report`, { weekNumber }))
}

export function getSectionPeerEvaluationReport(instructorId, sectionId, weekNumber = '') {
  return request(withQuery(`/instructors/${instructorId}/sections/${sectionId}/peer-evaluation-report`, { weekNumber }))
}

export function getTeamWarReport(teamId, viewerType, viewerId, weekNumber = '') {
  return request(withQuery(`/teams/${teamId}/war-report`, { viewerType, viewerId, weekNumber }))
}

export function getInstructorStudentPeerEvaluationReport(instructorId, studentId, weekNumber = '') {
  return request(withQuery(`/instructors/${instructorId}/students/${studentId}/peer-evaluation-report`, { weekNumber }))
}

export function getInstructorStudentWarReport(instructorId, studentId, weekNumber = '') {
  return request(withQuery(`/instructors/${instructorId}/students/${studentId}/war-report`, { weekNumber }))
}
