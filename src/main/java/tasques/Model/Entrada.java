package tasques.Model;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Entrada {
    private ObjectId id;
    private String studentName;
    private String lastName1;
    private String lastName2;
    private Date entryDate;
    private boolean complete;
    private String observations;

    public Entrada() {
    }

    public Entrada(String studentName, String lastName1, String lastName2, Date entryDate, boolean complete,
            String observations) {
        this.studentName = studentName;
        this.lastName1 = lastName1;
        this.lastName2 = lastName2;
        this.entryDate = entryDate;
        this.complete = complete;
        this.observations = observations;
    }

    public Entrada(Document doc) {
        this.id = doc.getObjectId("_id");
        this.studentName = doc.getString("nomAlumne");
        this.lastName1 = doc.getString("cognom1");
        this.lastName2 = doc.getString("cognom2");

        Object dateObj = doc.get("dataEntradaTasca");
        if (dateObj instanceof Date) {
            this.entryDate = (Date) dateObj;
        } else if (dateObj instanceof String) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                this.entryDate = isoFormat.parse((String) dateObj);
            } catch (ParseException e) {
                System.err.println("Error en parsejar data: " + dateObj);
                this.entryDate = new Date();
            }
        } else {
            this.entryDate = new Date();
        }

        this.complete = doc.getBoolean("completa", false);
        this.observations = doc.getString("observacions");
    }

    public Document toDocument() {
        Document doc = new Document();
        if (id != null) {
            doc.append("_id", id);
        }
        doc.append("nomAlumne", studentName)
                .append("cognom1", lastName1)
                .append("cognom2", lastName2)
                .append("dataEntradaTasca", entryDate)
                .append("completa", complete)
                .append("observacions", observations);
        return doc;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getLastName1() {
        return lastName1;
    }

    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }

    public String getLastName2() {
        return lastName2;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getFullName() {
        return studentName + " " + lastName1 + " " + lastName2;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Alumne: %s | Data: %s | Completa: %s | Observacions: %s",
                id != null ? id.toHexString() : "nou",
                getFullName(),
                entryDate,
                complete ? "Sí" : "No",
                observations != null ? observations : "Sense observacions");
    }
}
