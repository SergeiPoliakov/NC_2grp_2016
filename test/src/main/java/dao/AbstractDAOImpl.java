package dao;

import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class AbstractDAOImpl<T> extends AbstractDAO<T> {

    private Class<T> type;

    public AbstractDAOImpl(Class<T> type) {
        this.type = type;
    }

    protected EntityManager em = HibernateUtil.getEm();

    public void setType(Class<T> type) {
        this.type = type;
    }
    public Class<T> getType() { return type; }

    @Override
    @Transactional
    public void save(T newInstance) {
        em.getTransaction().begin();
        em.persist(newInstance);
        em.getTransaction().commit();
        em.detach(newInstance);
    }

    @Override
    public T read(Integer id) {
        T result = (T) em.find(type, id);
        em.detach(result);
        return result;
    }

    @Override
    public List<T> readAll() {
        TypedQuery<T> query = em.createQuery("FROM " + type.getSimpleName(), type);
        List<T> resultList = query.getResultList();
        return resultList;
    }

    @Override
    @Transactional
    public T update(T transientObject) {
        em.getTransaction().begin();
        T entity = em.merge(transientObject);
        em.getTransaction().commit();
        em.detach(transientObject);
        return entity;
    }

    @Override
    @Transactional
    public void remove(T persistentObject) {
        em.getTransaction().begin();
        em.remove(persistentObject);
        em.getTransaction().commit();
        em.detach(persistentObject);
    }

    @Override
    @Transactional
    public void remove(Integer id) {
        T entity = (T) em.find(type, id);
        if (entity != null) {
            remove(entity);
        }
    }

}
