# 🧠 AI Travel Caption Generator (CAPPA)

An end-to-end **full-stack AI application** that generates creative travel captions from images — including short, funny, poetic captions and travel tips — using modern backend and frontend technologies.

🚀 **Live Demo**

* **Frontend:** [https://your-vercel-url.vercel.app](https://your-vercel-url.vercel.app)
* **Backend API:** [https://cappa-backend-sfy0.onrender.com](https://cappa-backend-sfy0.onrender.com)

---

## ✨ Features

* 📸 Upload any travel image
* 🧠 AI-generated captions:

  * Short caption
  * Funny caption
  * Poetic caption
  * Travel tip
* ⚡ Fast, responsive UI
* ☁️ Fully deployed (Frontend + Backend)
* 🔐 Secure API key handling via environment variables

---

## 🏗️ Architecture

```
CAPPA/
│
├── frontend/        # React + Vite frontend (Vercel)
│
└── cappy/           # Spring Boot backend (Render)
```

### Flow:

1. User uploads image (Frontend)
2. Image sent as multipart request to backend
3. Backend calls AI API
4. Captions returned as JSON
5. Frontend renders results

---

## 🛠️ Tech Stack

### Frontend

* React (TypeScript)
* Vite
* Tailwind CSS
* Deployed on **Vercel**

### Backend

* Java 17
* Spring Boot
* REST API
* Docker
* Deployed on **Render**

### AI

* OpenAI / Gemini / Groq (pluggable)
* Secure API key via environment variables

---

## ⚙️ Environment Variables

### Backend (Render)

```env
AI_API_KEY=your_api_key_here
PORT=10000
```

### Frontend (Vercel)

```env
VITE_BACKEND_URL=https://cappa-backend-sfy0.onrender.com
```

---

## 🚀 Local Setup

### Backend (Spring Boot)

```bash
cd cappy
mvn spring-boot:run
```

Backend runs at:

```
http://localhost:4000
```

---

### Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

```
http://localhost:5173
```

---

## 📡 API Endpoint

### POST `/api/caption`

**Request**

* `multipart/form-data`
* Field: `image` (file)

**Response**

```json
{
  "captions": {
    "short": "...",
    "funny": "...",
    "poetic": "...",
    "travel_tip": "..."
  }
}
```

---

## 🔐 Security Notes

* ❌ No API keys committed to GitHub
* ✅ Secrets managed via Render & Vercel
* ❌ `node_modules` excluded from repo
* ✅ Dockerized backend for portability

---

## 📌 Future Improvements

* Drag & drop image upload
* Caption download/share buttons
* User accounts
* Rate limiting
* Dark mode
* Custom domain

---

## 👤 Author

**Soumyajeet Satapathy**

* GitHub: [https://github.com/metadroix35](https://github.com/metadroix35)
* Full-stack | AI | Cloud | Backend-focused

---

## ⭐ If you like this project

Give it a ⭐ on GitHub — it helps a lot!

---

