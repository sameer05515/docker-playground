# Knowledge Recall System V2

V2 adds the **Recall Engine** to V1.

The goal is not just to store information, but to repeatedly recall it until it becomes interview-ready knowledge.

## Stack

- Java 17
- Spring Boot 3.4.5
- Spring Data MongoDB
- MongoDB 8 Docker
- React + Vite
- Axios
- react-markdown

## V2 Features

Everything from V1 plus:

- Start Recall session
- Due-for-review queue
- Hide answer until you attempt recall
- Self-rating: Again / Hard / Good / Easy
- Spaced review scheduling
- Review count
- Correct count
- Last reviewed timestamp
- Next review timestamp
- Confidence percentage
- Recall history
- Markdown rendering for Question, Answer and Notes
- Recall dashboard
- Weak-topic detection
- Recall session progress

## Simple scheduling algorithm

| Rating | Next review |
|---|---|
| Again | Today + 1 day |
| Hard | Today + 2 days |
| Good | Today + 5 days |
| Easy | Today + 10 days |

If you answer "Again", the item comes back sooner.

This is intentionally simple in V2. V3 can evolve this into a more sophisticated spaced-repetition algorithm.

## Architecture

React + Vite
      |
      | REST / JSON
      v
Spring Boot
      |
      +---- Knowledge collection
      |
      +---- Recall history
- Markdown rendering for Question, Answer and Notes
      |
      v
MongoDB
      ^
      |
   Docker

## Run

### 1. Start MongoDB

```powershell
docker compose up -d
```

### 2. Start backend

```powershell
cd backend
mvn spring-boot:run
```

### 3. Start frontend

Open another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open:

http://localhost:5173

Backend:

http://localhost:8080

Health:

http://localhost:8080/api/health

## Important V2 flow

```text
Due knowledge
      ↓
Recall question
      ↓
Think / type answer
      ↓
Reveal expected answer
      ↓
Rate yourself
      ↓
Again / Hard / Good / Easy
      ↓
Calculate next review
      ↓
Save recall history
```

## API

### Knowledge

GET `/api/knowledge`

GET `/api/knowledge/{id}`

POST `/api/knowledge`

PUT `/api/knowledge/{id}`

DELETE `/api/knowledge/{id}`

### Recall

GET `/api/recall/due`

POST `/api/recall/{id}/review`

Example:

```json
{
  "rating": "GOOD"
}
```

### Dashboard

GET `/api/recall/stats`

## V3 ideas

- Proper spaced repetition
- Interview mode
- Timed recall
- Mock interview
- Weak-area recommendations
- Daily target
- Progress charts
- Authentication
- User-specific knowledge


## Markdown support

Question, Answer and Notes are stored as normal strings in MongoDB, but the React UI renders them as Markdown using `react-markdown`.

Examples:

```markdown
## HashMap

HashMap stores **key-value pairs**.

### Internal flow

1. Calculate hash
2. Find bucket
3. Compare keys
4. Handle collision

> Remember: hash → bucket → node
```

You can use:

- headings
- bold / italic
- ordered and unordered lists
- links
- blockquotes
- inline code
- fenced code blocks
- tables

The V2 frontend uses `react-markdown` for rendering.


## Export and import

Knowledge can be exported to JSON with MongoDB `_id` values. Import accepts the exported object or a plain array. For each imported `_id` that already exists, the UI asks whether to overwrite it. **OK overwrites; Cancel skips.** Recall fields are preserved.
