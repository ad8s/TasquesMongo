package tasques.Model;

// He fet servir un altre model es a dir, no he modificat el model, o esborrat, perque el vull fer servir per poder estudiar!
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelApi implements EntradaModel {

        private static final String API_URL = "https://m6uf3api-two.vercel.app";
        private final HttpClient client = HttpClient.newHttpClient();

        private JSONObject entradaToJson(Entrada entry) {

                JSONObject json = new JSONObject();

                json.put("nomAlumne", entry.getStudentName());
                json.put("cognom1", entry.getLastName1());
                json.put("cognom2", entry.getLastName2());

                String date = new SimpleDateFormat("yyyy-MM-dd")
                                .format(entry.getEntryDate());

                json.put("dataEntradaTasca", date);
                json.put("completa", entry.isComplete());
                json.put("observacions", entry.getObservations());

                return json;
        }

        private Entrada jsonToEntrada(JSONObject obj) {

                Entrada e = new Entrada();

                if (obj.has("_id")) {
                        e.setId(new ObjectId(obj.getString("_id")));
                }

                e.setStudentName(obj.optString("nomAlumne", ""));
                e.setLastName1(obj.optString("cognom1", ""));
                e.setLastName2(obj.optString("cognom2", ""));
                e.setObservations(obj.optString("observacions", ""));
                e.setComplete(obj.optBoolean("completa", false));

                try {
                        String dateStr = obj.optString("dataEntradaTasca", null);
                        if (dateStr != null) {
                                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                                e.setEntryDate(date);
                        }
                } catch (Exception ex) {
                        e.setEntryDate(new Date());
                }

                return e;
        }

        private List<Entrada> parseEntrades(String body) {

                List<Entrada> list = new ArrayList<>();

                if (body == null || body.isEmpty())
                        return list;

                body = body.trim();

                if (!body.startsWith("[")) {
                        System.err.println("Respuesta no válida: " + body);
                        return list;
                }

                JSONArray array = new JSONArray(body);

                for (int i = 0; i < array.length(); i++) {
                        list.add(jsonToEntrada(array.getJSONObject(i)));
                }

                return list;
        }

        @Override
        public boolean insertEntry(Entrada entry) {

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/add"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                entradaToJson(entry).toString()))
                                .build();

                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(res -> res.statusCode() == 200 || res.statusCode() == 201)
                                .join();
        }

        @Override
        public boolean updateEntry(Entrada entry) {

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/update/" + entry.getId().toHexString()))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(
                                                entradaToJson(entry).toString()))
                                .build();

                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(res -> res.statusCode() == 200)
                                .join();
        }

        @Override
        public boolean deleteEntry(Entrada entry) {

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/delete/" + entry.getId().toHexString()))
                                .DELETE()
                                .build();

                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(res -> res.statusCode() == 200)
                                .join();
        }

        @Override
        public List<Entrada> getAllEntries() {

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/list"))
                                .GET()
                                .build();

                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(res -> parseEntrades(res.body()))
                                .join();
        }

        @Override
        public List<Entrada> getEntriesByDate(Date start, Date end) {

                String s = new SimpleDateFormat("yyyy-MM-dd").format(start);
                String e = new SimpleDateFormat("yyyy-MM-dd").format(end);

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/list/" + s + "/" + e))
                                .GET()
                                .build();

                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(res -> parseEntrades(res.body()))
                                .join();
        }

        @Override
        public List<Entrada> getFilteredEntries(String name) {

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/list/name/" + name))
                                .GET()
                                .build();

                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(res -> parseEntrades(res.body()))
                                .join();
        }

        @Override
        public List<Entrada> getEntriesByCompletion(boolean complete) {

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/list/completed/" + complete))
                                .GET()
                                .build();

                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(res -> parseEntrades(res.body()))
                                .join();
        }
}
