import mysql from "mysql2/promise";

const config = {
  host: process.env.DB_HOST || "localhost",
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER || "todo_user",
  password: process.env.DB_PASSWORD || "todo_password",
  database: process.env.DB_NAME || "todo_db",
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
};

let pool;

export function getPool() {
  if (!pool) {
    pool = mysql.createPool(config);
  }

  return pool;
}

export async function waitForDatabase(retries = 30, delayMs = 2000) {
  for (let attempt = 1; attempt <= retries; attempt++) {
    try {
      const connection = await getPool().getConnection();
      await connection.ping();
      connection.release();

      console.log("MySQL connection established.");
      return;
    } catch (error) {
      console.log(
        `Waiting for MySQL... attempt ${attempt}/${retries}`
      );

      if (attempt === retries) {
        throw error;
      }

      await new Promise(resolve => setTimeout(resolve, delayMs));
    }
  }
}
