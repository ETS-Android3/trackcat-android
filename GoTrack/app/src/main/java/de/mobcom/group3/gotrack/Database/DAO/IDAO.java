package de.mobcom.group3.gotrack.Database.DAO;

import java.util.List;

interface IDAO<T> {
    T read(long id);

    List<T> readAll(long id);

    void create(T t);

    void update(T t);

    void delete(T t);
}
