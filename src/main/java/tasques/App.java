package tasques;

import java.util.Scanner;
import java.time.Instant;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.github.cdimascio.dotenv.Dotenv;

import org.bson.Document;
import org.bson.types.ObjectId;

public final class App {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String uri = dotenv.get("MONGO_URI");
        if (uri == null) {
            throw new IllegalStateException("MONGO_URI no está especificat en el .env!");
        }
        Scanner sc = new Scanner(System.in);
        MongoClient mongoClient = MongoClients.create(uri);

        MongoDatabase database = mongoClient.getDatabase("Tasques");
        MongoCollection<Document> collection = database.getCollection("Entrades");

        System.out.println("Conectado a MongoDB!");
        Boolean finish = false;
        while (!finish) {
            System.out.println("""
                        1. Crear entrada
                        2. Llegir tots el registres
                        3. Llegir registre per alumne
                        4. Actualitzar registre
                        5. Eliminar registre
                        6. Sortir
                    """);
            int option = askOp(sc, ">> ");
            switch (option) {
                case 1 -> {
                    System.out.println("Nom de l'alumne: ");
                    String entryName = sc.nextLine();
                    System.out.println("Primer cognom de l'alumne: ");
                    String EntryLastnameOne = sc.nextLine();
                    System.out.println("Segon cognom de l'alumne: ");
                    String EntryLastNameTwo = sc.nextLine();
                    System.out.println("Tasca completada? (true/false): ");
                    boolean EntryCompleted = sc.nextBoolean();
                    sc.nextLine();
                    System.out.println("Observacions: ");
                    String EntryObservations = sc.nextLine();

                    create(entryName, EntryLastnameOne, EntryLastNameTwo, EntryCompleted, EntryObservations,
                            collection);
                }
                case 2 -> readAll(collection);
                case 3 -> {
                    System.out.println("Nom de l'alumne a buscar: ");
                    String entryName = sc.nextLine();
                    readPerAlumne(entryName, collection);
                }
                case 4 -> {
                    readAll(collection);
                    System.out.println("ID de l'entrada a actualitzar: ");
                    String idToUpdate = sc.nextLine();
                    ObjectId entryId = new ObjectId(idToUpdate);
                    System.out
                            .println("Camp a actualitzar (nomAlumne, cognom1, cognom2, completa, observacions): ");
                    String fieldToUpdate = sc.nextLine();
                    System.out.println("Nou valor: ");
                    String newValue = sc.nextLine();

                    Object value;
                    if (fieldToUpdate.equals("completa")) {
                        value = Boolean.parseBoolean(newValue);
                    } else {
                        value = newValue;
                    }

                    update(entryId, fieldToUpdate, value, collection);
                }
                case 5 -> {
                    readAll(collection);
                    System.out.println("ID de l'entrada a eliminar: ");
                    String idToDelete = sc.nextLine();
                    ObjectId entryId = new ObjectId(idToDelete);
                    Delete(entryId, collection);
                }
                case 6 -> {
                    mongoClient.close();

                    sc.close();
                    finish = true;
                }
                default -> System.err.println("Opció no vàlida! (1 - 6)");
            }

        }
    }

    public static void create(String entryName, String EntryLastnameOne, String EntryLastNameTwo,
            boolean EntryCompleted, String EntryObservations, MongoCollection<Document> collection) {
        Document newEntry = new Document("nomAlumne", entryName)
                .append("cognom1", EntryLastnameOne)
                .append("cognom2", EntryLastNameTwo)
                .append("dataEntradaTasca", Instant.now().toString())
                .append("completa", EntryCompleted)
                .append("observacions", EntryObservations);
        try {
            collection.insertOne(newEntry);
            System.out.println("Entrada creada!");
        } catch (MongoWriteException mwe) {
            System.err.println("Error d'escriptura a MongoDB: " + mwe.getMessage());
        } catch (MongoException me) {
            System.err.println("Error de MongoDB: " + me.getMessage());
        }

    }

    public static void readAll(MongoCollection<Document> collection) {

        try (MongoCursor<Document> cursor = collection.find().iterator()) {

            while (cursor.hasNext()) {
                Document entry = cursor.next();
                String id = entry.getObjectId("_id").toHexString();
                String nom = entry.getString("nomAlumne");
                String cognom1 = entry.getString("cognom1");
                String cognom2 = entry.getString("cognom2");

                boolean completa = Boolean.TRUE.equals(entry.getBoolean("completa"));
                String data = entry.getString("dataEntradaTasca");

                String observacions = entry.getString("observacions");
                if (observacions == null || observacions.isBlank()) {
                    observacions = "(sense observacions)";
                }

                System.out.printf("""
                        ───── TASCA ─────
                        ID: %s
                        Alumne: %s
                        Cognoms: %s %s
                        Completada: %s
                        Data entrada: %s
                        Observacions: %s
                        ─────────────────
                        """,
                        id, nom, cognom1, cognom2,
                        completa,
                        data,
                        observacions);
            }
        } catch (MongoException e) {
            System.err.println("Error de MongoDB: " + e.getMessage());
        }
    }

    public static void readPerAlumne(String entryName, MongoCollection<Document> collection) {
        Document filter = new Document("nomAlumne", entryName);
        try {
            Document tasca = collection.find(filter).first();
            if (tasca == null) {
                System.err.println("No s'ha trobat cap entrada");
            } else {
                String id = tasca.getObjectId("_id").toHexString();
                String nom = tasca.getString("nomAlumne");
                String cognom1 = tasca.getString("cognom1");
                String cognom2 = tasca.getString("cognom2");
                boolean completa = Boolean.TRUE.equals(tasca.getBoolean("completa"));
                String data = tasca.getString("dataEntradaTasca");
                String observacions = tasca.getString("observacions");
                if (observacions == null || observacions.isBlank()) {
                    observacions = "(sense observacions)";
                }

                System.out.printf("""
                        ───── TASCA ─────
                        ID: %s
                        Alumne: %s
                        Cognoms: %s %s
                        Completada: %s
                        Data entrada: %s
                        Observacions: %s
                        ─────────────────
                        """,
                        id, nom, cognom1, cognom2,
                        completa,
                        data,
                        observacions);
            }
        } catch (MongoException me) {
            System.err.println("Error de MongoDB: " + me.getMessage());
        }
    }

    public static void update(ObjectId entryId, String fieldToUpdate, Object newValue,
            MongoCollection<Document> collection) {
        Document filter = new Document("_id", entryId);
        Document update = new Document("$set", new Document(fieldToUpdate, newValue));
        try {
            UpdateResult result = collection.updateOne(filter, update);
            if (result.getMatchedCount() == 0) {
                System.err.println("No existeix cap entrada amb aquest ID");
            } else if (result.getModifiedCount() == 0) {
                System.out.println("Aquest camp ja era així!");
            } else {
                System.out.println("Entrada actualitzada!");
            }
        } catch (MongoWriteException mwe) {
            System.err.println("Error d'escriptura a MongoDB: " + mwe.getMessage());
        } catch (MongoException me) {
            System.err.println("Error de MongoDB: " + me.getMessage());
        }
    }

    public static void Delete(ObjectId entryId, MongoCollection<Document> collection) {
        Document filter = new Document("_id", entryId);
        try {
            DeleteResult result = collection.deleteOne(filter);
            if (result.getDeletedCount() == 0) {
                System.err.println("No s'ha eliminat cap entrada");
            } else {
                System.out.println("Entrada eliminada!");
            }
        } catch (MongoException me) {
            System.err.println("Error de MongoDB: " + me.getMessage());
        }
    }

    public static int askOp(Scanner sc, String message) {
        int opValue = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(message);
            if (sc.hasNextInt()) {
                opValue = sc.nextInt();
                sc.nextLine();
                valid = true;
            } else {
                System.err.println("Has de posar un número del 1 - 6!");
                sc.next();
            }
        }
        return opValue;
    }

}
