package com.example.service;

import java.util.function.Supplier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class TransactionUtil {
    public static <R> R runInTransaction(EntityManager em, Supplier<R> action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = action.get();
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        }
    }
}
