package tasques;

import java.util.Date;
import java.util.List;

import tasques.Model.Entrada;
import tasques.Model.EntradaModel;
import tasques.Model.Model;
import tasques.Util.ConnectionManager;
import tasques.View.View;

public class Main {
    private EntradaModel model;
    private View view;
    private boolean running;

    public Main(EntradaModel model) {
        this.model = model;
        this.view = new View();
        this.running = true;
    }

    public void run() {
        view.showMessage("Benvingut al sistema de gestió d'entrades de tasques!");

        while (running) {
            view.showMainMenu();
            int option = view.readOption();
            processOption(option);
        }

        ConnectionManager.closeConnection();
        view.close();
        view.showFarewell();
    }

    private void processOption(int option) {
        switch (option) {
            case 1 -> addEntry();
            case 2 -> deleteEntry();
            case 3 -> modifyEntry();
            case 4 -> listAllEntries();
            case 5 -> listEntriesByDate();
            case 6 -> searchEntryByName();
            case 7 -> listEntriesByStatus();
            case 0 -> exit();
            default -> view.showError("Opció no vàlida. Si us plau, tria una opció entre 0 i 7.");
        }
    }

    private void addEntry() {
        try {
            Entrada newEntry = view.readNewEntryData();

            if (newEntry.getStudentName().isEmpty()) {
                view.showError("El nom de l'alumne és obligatori.");
                return;
            }

            boolean success = model.insertEntry(newEntry);

            if (success) {
                view.showSuccess("Entrada afegida correctament!");
            } else {
                view.showError("No s'ha pogut afegir l'entrada.");
            }
        } catch (Exception e) {
            view.showError("Error en afegir l'entrada: " + e.getMessage());
        }

        view.pause();
    }

    private void deleteEntry() {
        try {
            List<Entrada> entries = model.getAllEntries();

            if (entries.isEmpty()) {
                view.showMessage("No hi ha entrades per eliminar.");
                view.pause();
                return;
            }

            Entrada selectedEntry = view.selectEntry(entries);

            if (selectedEntry == null) {
                view.showMessage("Operació cancel·lada.");
                view.pause();
                return;
            }

            boolean confirm = view.readBoolean("Estàs segur que vols eliminar aquesta entrada?");

            if (confirm) {
                boolean success = model.deleteEntry(selectedEntry);

                if (success) {
                    view.showSuccess("Entrada eliminada correctament!");
                } else {
                    view.showError("No s'ha pogut eliminar l'entrada.");
                }
            } else {
                view.showMessage("Eliminació cancel·lada.");
            }
        } catch (Exception e) {
            view.showError("Error en eliminar l'entrada: " + e.getMessage());
        }

        view.pause();
    }

    private void modifyEntry() {
        try {
            List<Entrada> entries = model.getAllEntries();

            if (entries.isEmpty()) {
                view.showMessage("No hi ha entrades per modificar.");
                view.pause();
                return;
            }

            Entrada selectedEntry = view.selectEntry(entries);

            if (selectedEntry == null) {
                view.showMessage("Operació cancel·lada.");
                view.pause();
                return;
            }

            view.readModifyEntryData(selectedEntry);

            boolean success = model.updateEntry(selectedEntry);

            if (success) {
                view.showSuccess("Entrada modificada correctament!");
            } else {
                view.showError("No s'ha pogut modificar l'entrada.");
            }
        } catch (Exception e) {
            view.showError("Error en modificar l'entrada: " + e.getMessage());
        }

        view.pause();
    }

    private void listAllEntries() {
        try {
            List<Entrada> entries = model.getAllEntries();
            view.showEntriesList(entries, "TOTES LES ENTRADES");
        } catch (Exception e) {
            view.showError("Error en llistar les entrades: " + e.getMessage());
        }

        view.pause();
    }

    private void listEntriesByDate() {
        try {
            Date[] dates = view.readDateRange();
            Date startDate = dates[0];
            Date endDate = dates[1];

            List<Entrada> entries = model.getEntriesByDate(startDate, endDate);
            view.showEntriesList(entries, "ENTRADES ENTRE DATES");
        } catch (Exception e) {
            view.showError("Error en filtrar per dates: " + e.getMessage());
        }

        view.pause();
    }

    private void searchEntryByName() {
        try {
            String name = view.readText("Introdueix el nom (o part del nom) de l'alumne: ");

            if (name.isEmpty()) {
                view.showError("Has d'introduir un nom per cercar.");
                view.pause();
                return;
            }

            List<Entrada> entries = model.getFilteredEntries(name);
            view.showEntriesList(entries, "RESULTATS DE LA CERCA: " + name);
        } catch (Exception e) {
            view.showError("Error en cercar entrades: " + e.getMessage());
        }

        view.pause();
    }

    private void listEntriesByStatus() {
        try {
            boolean complete = view.readBoolean("Vols veure les tasques completes?");

            List<Entrada> entries = model.getEntriesByCompletion(complete);
            String title = complete ? "ENTRADES COMPLETES" : "ENTRADES INCOMPLETES";
            view.showEntriesList(entries, title);
        } catch (Exception e) {
            view.showError("Error en filtrar per estat: " + e.getMessage());
        }

        view.pause();
    }

    private void exit() {
        view.showMessage("Tancant l'aplicació...");
        running = false;
    }

    public static void main(String[] args) {
        EntradaModel model = new Model();
        Main app = new Main(model);
        app.run();
    }
}
