const BASE_URL = 'http://localhost:8080/api'

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  })
  if (!res.ok) {
    const err = new Error(`API ${res.status}: ${res.statusText}`)
    err.status = res.status
    throw err
  }
  return res.status === 204 ? null : res.json()
}

export function registerUser(email, password, displayName) {
  return request('/users/register', {
    method: 'POST',
    body: JSON.stringify({ email, password, displayName }),
  })
}

export function loginUser(email, password) {
  return request('/users/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export function getUserByEmail(email) {
  return request(`/users/by-email/${encodeURIComponent(email)}`)
}

export function getUser(id) {
  return request(`/users/${id}`)
}

export function getLevels() {
  return request('/levels')
}

export function getLevel(id) {
  return request(`/levels/${id}`)
}

export function submitQuizResult(data) {
  return request('/quiz-results', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getUserResults(userId) {
  return request(`/quiz-results/user/${userId}`)
}

export function getQuizResult(id) {
  return request(`/quiz-results/${id}`)
}
