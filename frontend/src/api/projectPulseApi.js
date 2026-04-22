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

export function getSections(name = '') {
  const params = new URLSearchParams()

  if (name.trim()) {
    params.set('name', name.trim())
  }

  const query = params.toString()
  return request(`/sections${query ? `?${query}` : ''}`)
}

export function getSectionDetails(id) {
  return request(`/sections/${id}`)
}

export function getTeams(sectionId = '') {
  if (sectionId) {
    return getSectionDetails(sectionId).then((section) => section.teams ?? [])
  }

  return request('/teams')
}

export function getTeamDetails(id) {
  return request(`/teams/${id}`)
}

export function createSection(payload) {
  return request('/sections', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateSection(id, payload) {
  return request(`/sections/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function updateSectionActiveWeeks(id, activeWeeks) {
  return request(`/sections/${id}/active-weeks`, {
    method: 'PATCH',
    body: JSON.stringify({ activeWeeks }),
  })
}

export function createRubric(payload) {
  return request('/rubrics', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
