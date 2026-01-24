package tasques.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import tasques.Model.Entrada;

public class View {
    private Scanner scanner;
    private SimpleDateFormat dateFormat;

    public View() {
        this.scanner = new Scanner(System.in);
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    public void showMainMenu() {
        System.out.println("\n──────────────────────────────────────────────────");
        System.out.println("   GESTIÓ D'ENTRADES DE TASQUES - MENÚ PRINCIPAL  ");
        System.out.println("──────────────────────────────────────────────────");
        System.out.println("  1. Afegir entrada de tasca");
        System.out.println("  2. Eliminar entrada de tasca");
        System.out.println("  3. Modificar entrada de tasca");
        System.out.println("  4. Llistar totes les entrades");
        System.out.println("  5. Llistar entrades entre dates");
        System.out.println("  6. Cercar entrada per nom d'alumne");
        System.out.println("  7. Llistar entrades per estat (completes/incompletes)");
        System.out.println("  0. Sortir");
        System.out.println("──────────────────────────────────────────────────");
    }

    public int readOption() {
        System.out.print("Selecciona una opció: ");
        try {
            int option = Integer.parseInt(scanner.nextLine().trim());
            return option;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public boolean readBoolean(String message) {
        System.out.print(message + " (s/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("s") || answer.equals("si") || answer.equals("sí");
    }

    public Date readDate(String message) {
        System.out.print(message + " (format: dd/MM/yyyy HH:mm): ");
        String dateStr = scanner.nextLine().trim();

        if (dateStr.isEmpty()) {
            return new Date();
        }

        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            showError("Format de data incorrecte. S'utilitzarà la data actual.");
            return new Date();
        }
    }

    public Entrada readNewEntryData() {
        System.out.println("\n--- AFEGIR NOVA ENTRADA ---");

        String name = readText("Nom de l'alumne: ");
        String lastName1 = readText("Primer cognom: ");
        String lastName2 = readText("Segon cognom: ");
        Date date = readDate("Data d'entrada de la tasca");
        boolean complete = readBoolean("La tasca està completa?");
        String observations = readText("Observacions (opcional): ");

        if (observations.isEmpty()) {
            observations = "Sense observacions";
        }

        return new Entrada(name, lastName1, lastName2, date, complete, observations);
    }

    public Entrada selectEntry(List<Entrada> entries) {
        if (entries == null || entries.isEmpty()) {
            showMessage("No hi ha entrades disponibles.");
            return null;
        }

        System.out.println("\n--- SELECCIONA UNA ENTRADA ---");
        for (int i = 0; i < entries.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, entries.get(i).toString());
        }
        System.out.println("0. Cancel·lar");

        int option = readOption();

        if (option > 0 && option <= entries.size()) {
            return entries.get(option - 1);
        }

        return null;
    }

    public void readModifyEntryData(Entrada entry) {
        System.out.println("\n--- MODIFICAR ENTRADA ---");
        System.out.println("Deixa en blanc per mantenir el valor actual");
        System.out.println("Dades actuals: " + entry.toString());
        System.out.println();

        String name = readText("Nom de l'alumne [" + entry.getStudentName() + "]: ");
        if (!name.isEmpty()) {
            entry.setStudentName(name);
        }

        String lastName1 = readText("Primer cognom [" + entry.getLastName1() + "]: ");
        if (!lastName1.isEmpty()) {
            entry.setLastName1(lastName1);
        }

        String lastName2 = readText("Segon cognom [" + entry.getLastName2() + "]: ");
        if (!lastName2.isEmpty()) {
            entry.setLastName2(lastName2);
        }

        System.out.print("Vols canviar la data d'entrada? (s/n): ");
        if (readBoolean("")) {
            Date newDate = readDate("Nova data d'entrada");
            entry.setEntryDate(newDate);
        }

        System.out.print("Vols canviar l'estat de compleció? (s/n): ");
        if (readBoolean("")) {
            boolean complete = readBoolean("La tasca està completa?");
            entry.setComplete(complete);
        }

        String observations = readText("Observacions [" + entry.getObservations() + "]: ");
        if (!observations.isEmpty()) {
            entry.setObservations(observations);
        }
    }

    public Date[] readDateRange() {
        System.out.println("\n--- FILTRAR PER DATES ---");
        Date startDate = readDate("Data d'inici");
        Date endDate = readDate("Data de fi");
        return new Date[] { startDate, endDate };
    }

    public void showEntriesList(List<Entrada> entries, String title) {
        System.out.println("\n──────────────────────────────────────────────────");
        System.out.println("  " + centerText(title, 46) + " ");
        System.out.println("──────────────────────────────────────────────────");

        if (entries == null || entries.isEmpty()) {
            System.out.println("No s'han trobat entrades.");
        } else {
            System.out.println("Total d'entrades: " + entries.size());
            System.out.println("──────────────────────────────────────────────────");
            for (int i = 0; i < entries.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, entries.get(i).toString());
            }
        }
        System.out.println("──────────────────────────────────────────────────");
    }

    public void showMessage(String message) {
        System.out.println("\n" + message);
    }

    public void showError(String message) {
        System.err.println("\nERROR: " + message);
    }

    public void showSuccess(String message) {
        System.out.println("\nÈXIT: " + message);
    }

    public void showFarewell() {
        System.out.println("\n──────────────────────────────────────────────────");
        System.out.println("      Gràcies per utilitzar l'aplicació!          ");
        System.out.println("──────────────────────────────────────────────────");
    }

    public void pause() {
        System.out.print("\nPrem Enter per continuar...");
        scanner.nextLine();
    }

    private String centerText(String text, int width) {
        int spaces = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, spaces)) + text + " ".repeat(Math.max(0, width - text.length() - spaces));
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
