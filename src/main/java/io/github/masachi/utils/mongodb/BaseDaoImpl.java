package io.github.masachi.utils.mongodb;

import com.mongodb.MongoNamespace;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.github.masachi.condition.MongoDBCondition;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;

@Log4j2
@Conditional(MongoDBCondition.class)
@Primary
@Repository
public class BaseDaoImpl<T> implements IBaseDao<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    private ExceptionCapture exceptionCapture = new MongoDbExceptionCapture();

    @Deprecated
    @Override
    public List<T> findAll(Class<T> entityClass) {
        try {
            final List<T> list = mongoTemplate.findAll(entityClass);
            onLoaded(entityClass, list);
            return list;
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Override
    public T findById(String id, Class<T> entityClass) {
        try {
            final T t = mongoTemplate.findById(id, entityClass);
            onLoaded(entityClass, t);
            return t;
        } catch (Exception e) {
            log.error("查询 MongoDB 失败, id: " + id);

            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Override
    public List<T> findByQuery(Query query, Class<T> entityClass) {
        try {
            final List<T> list = mongoTemplate.find(query, entityClass);
            onLoaded(entityClass, list);
            return list;
        } catch (Exception e) {
            log.error("查询 MongoDB 失败, sql: " + query.toString());

            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Override
    public AggregationResults<T> aggregation(Aggregation aggregation, String collectionName, Class<T> entityClass) {
        try {
            return mongoTemplate.aggregate(aggregation, collectionName, entityClass);
        } catch (Exception e) {
            log.error("查询 MongoDB 失败, aggregation sql: " + aggregation.toString());

            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Deprecated
    @Override
    public List<T> findAllByParentId(String id, Class<T> entityClass) {
        Query query = new Query();
        query.skip(0).limit(10);
        query.fields().include("name").include("sex").exclude("_id");

        try {
            final List<T> list = mongoTemplate.find(new Query(Criteria.where("parent").is(id)), entityClass);
            onLoaded(entityClass, list);
            return list;
        } catch (Exception e) {
            log.error("查询 MongoDB 失败, id: " + id);

            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Override
    public T findOne(Query query, Class<T> entityClass) {
        try {
            final T t = mongoTemplate.findOne(query, entityClass);
            onLoaded(entityClass, t);
            return t;
        } catch (Exception e) {
            log.error("查询 MongoDB 失败, query sql: " + query.toString());

            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Override
    public void save(T entity) {
        try {
            mongoTemplate.save(entity);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
    }

    @Override
    public void insert(Collection<?> batchToSave, String collectionName) {
        try {
            mongoTemplate.insert(batchToSave, collectionName);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
    }

    @Override
    public DeleteResult deleteById(String id, Class<T> entityClass) {
        try {
            T data = mongoTemplate.findById(id, entityClass);
            if (data != null) {
                return mongoTemplate.remove(data);
            }
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Override
    public DeleteResult deleteByQuery(Query query, Class<T> entityClass) {
        try {
            return mongoTemplate.remove(query, entityClass);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
        return null;
    }

    /**
     * 此方法需要注意，只会更新一条记录
     *
     * @param query
     * @param update
     * @param entityClass
     */
    @Override
    public UpdateResult update(Query query, Update update, Class<T> entityClass) {
        log.debug("please attention: this method only update first record for query collections !!!! ");

        try {
            return mongoTemplate.updateFirst(query, update, entityClass);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
        return null;
    }

    /**
     * update 的所有方法都不支持传入分页参数
     * 可行方案：查询出超出的记录，然后将ID储存，重新新增一个query，再次更新。精准更新
     *
     * @param query
     * @param update
     * @param entityClass
     */
    @Override
    public UpdateResult updateMulti(Query query, Update update, Class<T> entityClass) {
        update.set("last_update_time", new Date());

        try {
            return mongoTemplate.updateMulti(query, update, entityClass);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Override
    public void upsert(Query query, Update update, Class<T> entityClass) {
        update.set("last_update_time", new Date());

        try {
            mongoTemplate.upsert(query, update, entityClass);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
    }

    @Override
    public long count(Query query, Class<T> entityClass) {
        try {
            return mongoTemplate.count(addIsDelete(query), entityClass);
        } catch (Exception e) {
            log.error("查询 MongoDB 失败, sql: " + query.toString());
            exceptionCapture.catchException(e);
        }
        return 0L;
    }

    @Override
    public long count(String json, Class<T> entityClass) {
        long count = 0;
        try {
            count = mongoTemplate.execute(entityClass, (MongoCollection<Document> mongoCollection) -> mongoCollection.countDocuments(Document.parse(json)));
        } catch (Exception e) {
            log.error("查询 MongoDB 失败, sql: " + json);
            exceptionCapture.catchException(e);
        }
        return count;
    }

    /**
     * 只查询未删除  或者  没有删除字段的数据
     */
    private Query addIsDelete(Query query) {
        if (query.getQueryObject().get("is_delete") == null) {
            query.addCriteria(Criteria.where("is_delete").ne(true));
        }
        return query;
    }

    @Override
    public void renameTable(String oldName, String newName) {

        if (!mongoTemplate.collectionExists(oldName)) {
            return;
        }


        try {
            mongoTemplate.execute(oldName, (MongoCollection<Document> mongoCollection) -> {
                MongoNamespace namespace = mongoCollection.getNamespace();
                MongoNamespace newNamespace = new MongoNamespace(namespace.getDatabaseName(), newName);
                mongoCollection.renameCollection(newNamespace);
                return null;
            });
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
    }

    @Override
    public void dropTable(String table) {
        if (!mongoTemplate.collectionExists(table)) {
            return;
        }

        try {
            mongoTemplate.dropCollection(table);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
    }

    @Override
    public List<T> execute(String json, Class<T> entityClass) {
        return execute(json, null, 0, 0, entityClass);
    }

    public List<T> execute(String findJson, String orderJson, Integer skip, Integer limit, Class<T> entityClass) {
        List<T> list = new ArrayList<>();
        try {
            mongoTemplate.execute(entityClass, (MongoCollection<Document> mongoCollection) -> {
                MongoCursor<Document> cursor = mongoCollection.find(Document.parse(findJson)).sort(Document.parse(orderJson == null ? "{}" : orderJson)).skip(skip).limit(limit).iterator();
                while (cursor.hasNext()) {
                    T source = mongoTemplate.getConverter().read(entityClass, cursor.next());
                    list.add(source);
                }
                return list;
            });
        } catch (Exception e) {
            log.error("查询 MongoDB 失败, sql: " + findJson);

            exceptionCapture.catchException(e);
        }
        return list;
    }

    @Override
    public BulkOperations bulkOps(BulkOperations.BulkMode bulkMode, Class<T> entityClass) {
        try {
            return mongoTemplate.bulkOps(bulkMode, entityClass);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }
        return null;
    }

    @Override
    public List<T> distinct(String collectionName, Query query, String clounName, Class<T> entityClass) {
        List<T> myList = new ArrayList<>();
        DistinctIterable<T> distinct = null;
        try {
            distinct = mongoTemplate.getCollection(collectionName).distinct(clounName, query.getQueryObject(), entityClass);
        } catch (Exception e) {
            exceptionCapture.catchException(e);
        }

        if (distinct != null) {
            MongoCursor<T> iterator = distinct.iterator();
            while (iterator.hasNext()) {
                myList.add(iterator.next());
            }
        }

        return myList;
    }

    @Override
    public void registExceptionCapturer(ExceptionCapture exceptionCapture) {
        if (exceptionCapture == null) {
            return;
        }
        this.exceptionCapture = exceptionCapture;
    }

    private class MongoDbExceptionCapture implements ExceptionCapture {

        @Override
        public void catchException(Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    private void onLoaded(Class<T> clazz, List<T> list) {
        if (BaseUtil.isEmpty(list) || !checkPersistentSupport(clazz)) {
            return;
        }

        list.forEach(this::onLoaded);
    }

    private void onLoaded(Class<T> clazz, T entity) {
        if (BaseUtil.isEmpty(entity) || !checkPersistentSupport(clazz)) {
            return;
        }

        final IPersistentSupport calculateEntity = (IPersistentSupport) entity;
        try {
            calculateEntity.onLoaded();
        } catch (Exception e) {
            log.error("onLoaded error, This class may not be correct! ");
        }
    }


    /**
     * 不检查entityClass是否实现了CalculateInterface接口，直接开始执行计算
     * 慎用!
     *
     * @param entity
     */
    private void onLoaded(T entity) {
        if (BaseUtil.isEmpty(entity)) {
            return;
        }

        final IPersistentSupport calculateEntity = (IPersistentSupport) entity;
        try {
            calculateEntity.onLoaded();
        } catch (Exception e) {
            log.error("onLoaded error, This class may not be correct! ");
        }
    }

    private boolean checkPersistentSupport(Class<T> clazz) {
        if (BaseUtil.isEmpty(clazz)) {
            return false;
        }

        final Class<?>[] interfaces = clazz.getInterfaces();
        if (BaseUtil.isEmpty(interfaces)) {
            return false;
        }

        return Arrays.asList(interfaces).contains(IPersistentSupport.class);
    }

}
