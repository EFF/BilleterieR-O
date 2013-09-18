package ca.ulaval.glo4003.dataaccessobjects;

import java.util.List;

public interface DataAccessObject <T> {
	public List<T> list();
	public void create(T event);
	public T read(long id);
	public void update(T event);
	public void delete(long id);
}
