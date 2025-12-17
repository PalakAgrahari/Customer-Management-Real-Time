package com.api.customer.dao;

import com.api.customer.model.CustomerModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CustomerDaoImplementation implements CustomerDao {

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomerDaoImplementation.class);

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<CustomerModel> getCustomers(int pageNo) {
        Session session = sessionFactory.openSession();
        int pageSize = 5;
        try {
            int offset = (pageNo - 1) * pageSize;
            String hql = "FROM CustomerModel";
            Query query = session.createQuery(hql).setFirstResult(offset).setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public CustomerModel getCustomerById(int id) {
        EntityManager entityManager = null;
        try {
            entityManager = sessionFactory.createEntityManager();
            String hql = "FROM CustomerModel WHERE id = :ID";
            Query query = entityManager.createQuery(hql);
            query.setParameter("ID", id);
            return (CustomerModel) query.getSingleResult();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    @Override
    @Transactional
    public CustomerModel addCustomer(CustomerModel customer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            session.saveOrUpdate(customer);
            transaction.commit();
            return customer;
        } catch (Exception e) {
            logger.info(e.getMessage());
            if (transaction.isActive()) transaction.rollback();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public CustomerModel getCustomerByCode(String customerCode) {
        EntityManager entityManager = null;
        try {
            entityManager = sessionFactory.createEntityManager();
            String hql = "FROM CustomerModel WHERE customerCode = :CODE";
            Query query = entityManager.createQuery(hql);
            query.setParameter("CODE", customerCode);
            return (CustomerModel) query.getSingleResult();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public void deleteCustomer(int id) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        CustomerModel customer = session.get(CustomerModel.class, id);
        if (customer != null) {
            session.delete(customer);
        }
        tx.commit();
        session.close();
    }


    @Override
    public List<CustomerModel> getCustomersByClientId(int client) {
        EntityManager entityManager = null;
        try {
            entityManager = sessionFactory.createEntityManager();
            String hql = "FROM CustomerModel WHERE client = :CLIENT";
            Query query = entityManager.createQuery(hql);
            query.setParameter("CLIENT", client);
            return query.getResultList();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }
}
