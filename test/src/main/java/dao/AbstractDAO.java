package dao;

import java.util.List;

/*
 * Обобщённый интерфейс DAO
 *
 */
public abstract class AbstractDAO<T> {

    // сохранить объект newInstance в БД
    abstract public void save(T newInstance);

    // извлечь объект из БД
    abstract public T read(Integer id);

    abstract public List<T> readAll();

    // обновить объект, находящийся в Persistence Context
    abstract public T update(T transientObject);

    // удалить объект из БД
    abstract public void remove(T persistentObject);

    abstract public void remove(Integer id);
}
