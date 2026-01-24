package tasques.Model;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import tasques.Util.ConnectionManager;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Model implements EntradaModel {
    private MongoCollection<Document> collection;
    private static final String COLLECTION_NAME = "Entrades";

    public Model() {
        MongoDatabase database = ConnectionManager.getConnection();
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public boolean insertEntry(Entrada entry) {
        try {
            Document doc = entry.toDocument();
            InsertOneResult result = collection.insertOne(doc);

            if (result.getInsertedId() != null) {
                entry.setId(result.getInsertedId().asObjectId().getValue());
                System.out.println("Entrada inserida correctament amb ID: " + entry.getId().toHexString());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en inserir entrada: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEntry(Entrada entry) {
        try {
            if (entry.getId() == null) {
                System.err.println("Error: L'entrada no té ID assignat");
                return false;
            }

            Bson filter = Filters.eq("_id", entry.getId());
            Bson updates = Updates.combine(
                    Updates.set("nomAlumne", entry.getStudentName()),
                    Updates.set("cognom1", entry.getLastName1()),
                    Updates.set("cognom2", entry.getLastName2()),
                    Updates.set("dataEntradaTasca", entry.getEntryDate()),
                    Updates.set("completa", entry.isComplete()),
                    Updates.set("observacions", entry.getObservations()));

            UpdateResult result = collection.updateOne(filter, updates);

            if (result.getModifiedCount() > 0) {
                System.out.println("Entrada actualitzada correctament");
                return true;
            } else {
                System.out.println("No s'ha modificat cap entrada (potser ja tenia els mateixos valors)");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error en actualitzar entrada: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEntry(Entrada entry) {
        try {
            if (entry.getId() == null) {
                System.err.println("Error: L'entrada no té ID assignat");
                return false;
            }

            Bson filter = Filters.eq("_id", entry.getId());
            DeleteResult result = collection.deleteOne(filter);

            if (result.getDeletedCount() > 0) {
                System.out.println("Entrada eliminada correctament");
                return true;
            } else {
                System.out.println("No s'ha trobat l'entrada a eliminar");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error en eliminar entrada: " + e.getMessage());
            return false;
        }
    }

    public List<Entrada> getAllEntries() {
        List<Entrada> entries = new ArrayList<>();
        try {
            for (Document doc : collection.find()) {
                entries.add(new Entrada(doc));
            }
        } catch (Exception e) {
            System.err.println("Error en obtenir totes les entrades: " + e.getMessage());
        }
        return entries;
    }

    public List<Entrada> getEntriesByDate(Date startDate, Date endDate) {
        List<Entrada> entries = new ArrayList<>();
        try {
            Bson filter = Filters.and(
                    Filters.gte("dataEntradaTasca", startDate),
                    Filters.lte("dataEntradaTasca", endDate));

            for (Document doc : collection.find(filter)) {
                entries.add(new Entrada(doc));
            }
        } catch (Exception e) {
            System.err.println("Error en obtenir entrades per data: " + e.getMessage());
        }
        return entries;
    }

    public List<Entrada> getFilteredEntries(String studentName) {
        List<Entrada> entries = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(studentName, Pattern.CASE_INSENSITIVE);
            Bson filter = Filters.regex("nomAlumne", pattern);

            for (Document doc : collection.find(filter)) {
                entries.add(new Entrada(doc));
            }
        } catch (Exception e) {
            System.err.println("Error en filtrar entrades per nom: " + e.getMessage());
        }
        return entries;
    }

    public Entrada getEntryById(ObjectId id) {
        try {
            Document doc = collection.find(Filters.eq("_id", id)).first();
            if (doc != null) {
                return new Entrada(doc);
            }
        } catch (Exception e) {
            System.err.println("Error en obtenir entrada per ID: " + e.getMessage());
        }
        return null;
    }

    public List<Entrada> getEntriesByCompletion(boolean complete) {
        List<Entrada> entries = new ArrayList<>();
        try {
            Bson filter = Filters.eq("completa", complete);
            for (Document doc : collection.find(filter)) {
                entries.add(new Entrada(doc));
            }
        } catch (Exception e) {
            System.err.println("Error en obtenir entrades per estat: " + e.getMessage());
        }
        return entries;
    }
}
