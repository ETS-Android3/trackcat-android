package de.mobcom.group3.gotrack.Database.DAO;

import java.util.List;

interface IDAO<T> {
    T read(int id);

    List<T> readAll(int id);

    void create(T t);

    void update(int id, T t);

    void delete(T t);
}
