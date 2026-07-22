---
name: frontend
description: >
  Vue 3 (Vite) frontend for smooth-academy. Single-page quiz app for elementary
  math (Jarimatika finger method). Connects to Quarkus backend at localhost:8080.
---

## Stack

- Vue 3 (Composition API, `<script setup>`)
- Vite 8 + `@vitejs/plugin-vue`
- No router — screen switching via `currentScreen` ref
- ESLint + oxlint + oxfmt
- CSS: custom variables + component styles (no framework)

## Project Map

```
src/
├── main.js
├── App.vue                     # Main SPA with all screens
├── assets/css/
│   ├── variables.css           # CSS custom properties
│   ├── base.css                # Reset, body, #app
│   ├── components.css          # All component styles
│   ├── animations.css          # Keyframes
│   └── responsive.css          # Mobile
├── components/
│   └── MascotDisplay.vue       # Animated mascot + speech bubble
└── utils/
    ├── api.js                  # Fetch wrapper → localhost:8080/api
    ├── constants.js            # LEVELS, TOTAL_QUESTIONS, CONFETTI_COLORS
    ├── questions.js            # generateQuestions() + generateDistractors()
    ├── helpers.js              # shuffleArray, getRandomEncouragement, getRandomComfort
    ├── hands.js                # SVG hand rendering (Jarimatika finger counting)
    └── effects.js              # Confetti canvas effects
```

## Conventions

### Components
- `<script setup>` Composition API only
- Props with `defineProps`, emits with `defineEmits`
- No external UI libraries — all custom CSS
- Scoped animations via CSS classes, not JS animation libraries

### State Management
- No Vuex/Pinia — single `App.vue` manages all state via `ref`/`computed`
- Screen routing: `currentScreen` ref, `<Transition>` component
- User session: `currentUser` ref, persisted to `localStorage('jarimatika_user')`
- Stars: fetched from API, cached in `localStorage('jarimatika_stars')`

### API Layer (`utils/api.js`)
- Base URL: `http://localhost:8080/api`
- All functions use `request()` helper with JSON Content-Type
- Non-OK responses throw `Error` with `.status` property
- 204 No Content returns `null`

### Styling
- 2-space indent (`.editorconfig`)
- CSS variables in `variables.css` for colors, spacing, fonts
- Component classes use BEM-like naming prefixed with context
- No Tailwind, no utility CSS

## Screen Flow

```
screen-menu → screen-login → screen-mode → screen-level → screen-quiz → screen-result
                                  ↑                                           |
                                  └───────────────────────────────────────────┘
```

| Screen | Key | Purpose |
|--------|-----|---------|
| menu | `screen-menu` | Title, mascot, "Mulai Bermain" button |
| login | `screen-login` | Email + name inputs, register/login via API |
| mode | `screen-mode` | Practice (no timer) vs Challenge (30s per question) |
| level | `screen-level` | Pick level, shows stars per level, locked levels |
| quiz | `screen-quiz` | Active quiz: hand display, options, timer, progress dots |
| result | `screen-result` | Score, stars earned, retry/next level buttons |

## Domain Model (Frontend)

### Levels (from `constants.js`)
```
Level 1 — Ones Star      (1-9)
Level 2 — Tens Star      (10-99)
Level 3 — Hundreds Star  (100-999)
Level 4 — Thousands Star (1000-9999)
```

Future expansion to 5 operations × 4 sub-levels = 20 level entries.

### Quiz Generation (`questions.js`)
- `generateQuestions(levelId, count)` — generates random addition problems
- `generateDistractors(correct, levelId)` — 3 plausible wrong answers
- Flow: random `(a, b)` within level range, compute answer, generate distractors, shuffle all options

## How to Add a Screen

1. Add `screen-{name}` to `currentScreen` switch
2. Create `<Transition>` block with `v-else-if` condition
3. Add navigation function in `<script setup>`
4. Add mascot speech + button actions

## How to Connect to Backend

### Already wired:
- `GET /api/levels` — future: replace hardcoded LEVELS constant
- `GET /api/quiz-results/user/{userId}` — stars per level calculation
- `POST /api/quiz-results` — submit quiz on endQuiz()
- `POST /api/users/register` — register new user
- `GET /api/users/by-email/{email}` — existing user login

### Not yet wired:
- Question bank CRUD (future)
- Report generation (future)
- Premium subscription (future)

## Quiz Scoring

Stars calculation (in `endQuiz()`):
- ≥90% → 3 stars
- ≥70% → 2 stars
- ≥50% → 1 star
- <50% → 0 stars

Level unlock: level N requires 3 stars on level N-1.

## Dev

```bash
npm run dev      # Vite on :5173
npm run build    # Prod build to dist/
npm run lint     # oxlint + eslint
npm run format   # oxfmt
```

## Premium Feature Integration

`currentUser.isPremium` flag from API user object. Gate features:
- `v-if="currentUser?.isPremium"` on premium-only UI elements
- Backend validates premium status on protected endpoints
- Future: premium plan selection screen beforing screen-mode
