package tasques.Model;

import java.util.Date;
import java.util.List;

public interface EntradaModel {

    boolean insertEntry(Entrada entry);

    boolean updateEntry(Entrada entry);

    boolean deleteEntry(Entrada entry);

    List<Entrada> getAllEntries();

    List<Entrada> getEntriesByDate(Date startDate, Date endDate);

    List<Entrada> getFilteredEntries(String name);

    List<Entrada> getEntriesByCompletion(boolean complete);
}
