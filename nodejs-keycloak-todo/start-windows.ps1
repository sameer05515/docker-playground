docker compose up -d
Set-Location backend
if (!(Test-Path .env)) {
    Copy-Item .env.example .env
}
npm install
npm start
