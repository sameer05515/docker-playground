import "dotenv/config";
import express from "express";
import cors from "cors";
import todoRoutes from "./routes/todoRoutes.js";

const app = express();

const PORT = process.env.PORT || 8080;

app.use(cors());
app.use(express.json());

app.get("/api/health", (req, res) => {
  res.json({
    status: "UP",
    service: "todo-node-backend",
    timestamp: new Date().toISOString()
  });
});

app.use("/api/todos", todoRoutes);

app.use((req, res) => {
  res.status(404).json({
    message: "Endpoint not found"
  });
});

app.use((err, req, res, next) => {
  console.error(err);

  res.status(500).json({
    message: "Internal server error"
  });
});

app.listen(PORT, () => {
  console.log(`Todo backend running at http://localhost:${PORT}`);
});
