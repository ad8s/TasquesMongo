package tasques.Util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionManager {
    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;
    private static final String DATABASE_NAME = "Tasques";

    public static MongoDatabase getConnection() {
        if (database == null) {
            try {
                Dotenv dotenv = Dotenv.load();
                String uri = dotenv.get("MONGO_URI");

                if (uri == null || uri.isEmpty()) {
                    throw new IllegalStateException("MONGO_URI no està especificat en el fitxer .env!");
                }

                mongoClient = MongoClients.create(uri);

                database = mongoClient.getDatabase(DATABASE_NAME);

                System.out.println("Connexió establerta amb MongoDB correctament!");

            } catch (Exception e) {
                System.err.println("Error en connectar amb MongoDB: " + e.getMessage());
                throw new RuntimeException("No s'ha pogut establir connexió amb la base de dades", e);
            }
        }
        return database;
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("Connexió amb MongoDB tancada.");
        }
    }

    public static boolean isConnected() {
        return database != null;
    }
}
