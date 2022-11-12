package com.order.dao;

import com.order.model.IModel;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.partitioningBy;

/**
 * Class used to share properties and methods among all existing Daos
 */
public abstract class ParentDao<R extends UpdatableRecord<R>, P extends IModel, T> extends DAOImpl<R, P, T> {

    // Used to execute SQL queries in database
    protected DSLContext dsl;


    protected ParentDao(Table<R> table, Class<P> type, DSLContext dslContext) {
        super(
                table,
                type,
                dslContext.configuration()
        );
        this.dsl = dslContext;
    }

    /**
     * {@inheritDoc}
     */
    public abstract T getId(P var1);


    /**
     *    Return a {@link JdbcMapper} used to transform raw information given by a database query into a
     * desired Java object.
     *
     * @param classT
     *    {@link Class} of the returned results.
     * @param columns
     *    {@link String}s used to match information of the query into returned object
     * @param <T>
     *    Type of the returned results
     *
     * @return a Java object of type T
     */
    protected <T> JdbcMapper<T> getJdbcMapper(final Class<T> classT,
                                              final String... columns) {
        return JdbcMapperFactory.newInstance()
                .addKeys(columns)
                .newMapper(classT);
    }


    /**
     *    Store in database the given object, choosing between {@code insert} or {@code update} if
     * this one is a new instance or not.
     *
     * @param model
     *    Information to save in database
     *
     * @return {@link Optional} with the "final version" of the given object
     */
    public Optional<P> save(P model) {
        return ofNullable(model)
                .map(m -> {
                    if (m.isNew()) {
                        insert(m);
                    } else {
                        update(m);
                    }
                    return m;
                });
    }


    /**
     * Store in database the given {@link Collection}.
     *
     * @param models
     *    {@link Collection} of objects to store in database
     *
     * @return {@link List} with the "final version" of the given models
     */
    public List<P> saveAll(Collection<P> models) {
        return ofNullable(models)
                       .map(m -> {
                           Map<Boolean, List<P>> insertAndUpdate =
                                   models.stream()
                                           .collect(
                                                   partitioningBy(IModel::isNew)
                                           );

                           List<P> toInsert = insertAndUpdate.get(true);
                           if (null != toInsert) {
                               insert(toInsert);
                           } else {
                               toInsert = new ArrayList<>();
                           }
                           List<P> toUpdate = insertAndUpdate.get(false);
                           if (null != toUpdate) {
                               update(toUpdate);
                           } else {
                               toUpdate = new ArrayList<>();
                           }
                           toInsert.addAll(toUpdate);
                           return toInsert;
                       })
                       .orElseGet(ArrayList::new);
    }

}
