package io.github.masachi.utils.mongodb;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.List;

public interface IBaseDao<T> {

    @Deprecated
    List<T> findAll(Class<T> entityClass);

    T findById(String id, Class<T> entityClass);

    List<T> findByQuery(Query query, Class<T> entityClass);

    AggregationResults<T> aggregation(Aggregation aggregation, String collectionName, Class<T> entityClass);

    @Deprecated
    List<T> findAllByParentId(String id, Class<T> entityClass);

    T findOne(Query query, Class<T> entityClass);

    void save(T entity);

    void insert(Collection<? extends Object> batchToSave, String collectionName);

    DeleteResult deleteById(String id, Class<T> entityClass);

    DeleteResult deleteByQuery(Query query, Class<T> entityClass);

    UpdateResult update(Query query, Update update, Class<T> entityClass);

    UpdateResult updateMulti(Query query, Update update, Class<T> entityClass);

    void upsert(Query query, Update update, Class<T> entityClass);

    long count(Query query, Class<T> entityClass);

    long count(String json, Class<T> entityClass);

    void renameTable(String oldName, String newName);

    void dropTable(String table);

    List<T> execute(String json, Class<T> entityClass);

    BulkOperations bulkOps(BulkOperations.BulkMode bulkMode, Class<T> entityClass);

    public List<T> distinct(String collectionName, Query query, String clounName, Class<T> entityClass);

    public void registExceptionCapturer(ExceptionCapture exceptionCapture);

}
