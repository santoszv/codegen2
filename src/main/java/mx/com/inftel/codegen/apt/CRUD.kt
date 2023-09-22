/*
 * Copyright 2022 Santos Zatarain Vera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.com.inftel.codegen.apt

import mx.com.inftel.codegen.apt.model.ClassModel
import java.io.BufferedWriter

fun writeCRUD(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    bufferedWriter.appendLine("// Origin: ${classModel.qualifiedName}")
    if (classModel.packageName.isNotBlank()) {
        bufferedWriter.appendLine()
        bufferedWriter.appendLine("package ${classModel.packageName};")
    }
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("public interface ${classModel.crudName} {")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("    jakarta.persistence.EntityManager getEntityManager();")
    writeCount(bufferedWriter, classModel)
    writeList(bufferedWriter, classModel)
    writeFind(bufferedWriter, classModel)
    writeCreate(bufferedWriter, classModel)
    writeUpdate(bufferedWriter, classModel)
    writeDelete(bufferedWriter, classModel)
    writeCountContext(bufferedWriter, classModel)
    writeListContext(bufferedWriter, classModel)
    bufferedWriter.appendLine("}")
}

private fun writeCount(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default java.lang.Long count(CountContextConsumer consumer) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(consumer);""")
    bufferedWriter.appendLine("""        jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();""")
    bufferedWriter.appendLine("""        jakarta.persistence.criteria.CriteriaQuery<java.lang.Long> criteriaQuery = criteriaBuilder.createQuery(java.lang.Long.class);""")
    bufferedWriter.appendLine("""        jakarta.persistence.criteria.Root<${classModel.qualifiedName}> root = criteriaQuery.from(${classModel.qualifiedName}.class);""")
    bufferedWriter.appendLine("""        CountContext context = new CountContext(criteriaBuilder, root);""")
    bufferedWriter.appendLine("""        consumer.accept(context);""")
    bufferedWriter.appendLine("""        criteriaQuery.select(criteriaBuilder.count(root));""")
    bufferedWriter.appendLine("""        criteriaQuery.distinct(context.isDistinct());""")
    bufferedWriter.appendLine("""        if (!context.getPredicates().isEmpty()) {""")
    bufferedWriter.appendLine("""            criteriaQuery.where(context.getPredicates().toArray(new jakarta.persistence.criteria.Predicate[0]));""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        jakarta.persistence.TypedQuery<java.lang.Long> typedQuery = this.getEntityManager().createQuery(criteriaQuery);""")
    bufferedWriter.appendLine("""        return typedQuery.getSingleResult();""")
    bufferedWriter.appendLine("""    }""")
}

private fun writeList(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default java.util.List<${classModel.dtiName}> list(ListContextConsumer consumer) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(consumer);""")
    bufferedWriter.appendLine("""        jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();""")
    bufferedWriter.appendLine("""        jakarta.persistence.criteria.CriteriaQuery<${classModel.qualifiedName}> criteriaQuery = criteriaBuilder.createQuery(${classModel.qualifiedName}.class);""")
    bufferedWriter.appendLine("""        jakarta.persistence.criteria.Root<${classModel.qualifiedName}> root = criteriaQuery.from(${classModel.qualifiedName}.class);""")
    bufferedWriter.appendLine("""        ListContext context = new ListContext(criteriaBuilder, root);""")
    bufferedWriter.appendLine("""        consumer.accept(context);""")
    bufferedWriter.appendLine("""        criteriaQuery.select(root);""")
    bufferedWriter.appendLine("""        criteriaQuery.distinct(context.isDistinct());""")
    bufferedWriter.appendLine("""        if (!context.getPredicates().isEmpty()) {""")
    bufferedWriter.appendLine("""            criteriaQuery.where(context.getPredicates().toArray(new jakarta.persistence.criteria.Predicate[0]));""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        if (!context.getOrders().isEmpty()) {""")
    bufferedWriter.appendLine("""            criteriaQuery.orderBy(context.getOrders().toArray(new jakarta.persistence.criteria.Order[0]));""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        jakarta.persistence.TypedQuery<${classModel.qualifiedName}> typedQuery = this.getEntityManager().createQuery(criteriaQuery);""")
    bufferedWriter.appendLine("""        if (context.getFirstResult() >= 0) {""")
    bufferedWriter.appendLine("""            typedQuery.setFirstResult(context.getFirstResult());""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        if (context.getMaxResults() >= 0) {""")
    bufferedWriter.appendLine("""            typedQuery.setMaxResults(context.getMaxResults());""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        if (context.getLockMode() != null) {""")
    bufferedWriter.appendLine("""            typedQuery.setLockMode(context.getLockMode());""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        return typedQuery.getResultList().stream().map(entity -> {""")
    bufferedWriter.appendLine("""            ${classModel.dtiName} data = new ${classModel.dtoName}();""")
    bufferedWriter.appendLine("""            ${classModel.dtiName}.copyAllProperties(data, entity);""")
    bufferedWriter.appendLine("""            return data;""")
    bufferedWriter.appendLine("""        }).collect(java.util.stream.Collectors.toList());""")
    bufferedWriter.appendLine("""    }""")
}

private fun writeFind(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    val idPropertyModel = classModel.idProperty ?: return
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default void find(${classModel.dtiName} data) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        find(data, jakarta.persistence.LockModeType.OPTIMISTIC);""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default void find(${classModel.dtiName} data, jakarta.persistence.LockModeType lockMode) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(lockMode);""")
    bufferedWriter.appendLine("""        if (!tryFind(data, lockMode)) {""")
    bufferedWriter.appendLine("""            throw new IllegalArgumentException("Entity Not Found");""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default boolean tryFind(${classModel.dtiName} data) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        return tryFind(data, jakarta.persistence.LockModeType.OPTIMISTIC);""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default boolean tryFind(${classModel.dtiName} data, jakarta.persistence.LockModeType lockMode) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(lockMode);""")
    bufferedWriter.appendLine("""        ${classModel.qualifiedName} entity = this.getEntityManager().find(${classModel.qualifiedName}.class, data.${idPropertyModel.getterName}(), lockMode);""")
    bufferedWriter.appendLine("""        if (entity == null) {""")
    bufferedWriter.appendLine("""            return false;""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        ${classModel.dtiName}.copyAllProperties(data, entity);""")
    bufferedWriter.appendLine("""        return true;""")
    bufferedWriter.appendLine("""    }""")
}

private fun writeCreate(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default void create(${classModel.dtiName} data) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        ${classModel.qualifiedName} entity = new ${classModel.qualifiedName}();""")
    bufferedWriter.appendLine("""        ${classModel.dtiName}.copyInsertProperties(this.getEntityManager(), entity, data);""")
    bufferedWriter.appendLine("""        this.getEntityManager().persist(entity);""")
    bufferedWriter.appendLine("""        this.getEntityManager().flush();""")
    bufferedWriter.appendLine("""        ${classModel.dtiName}.copyAllProperties(data, entity);""")
    bufferedWriter.appendLine("""    }""")
}

private fun writeUpdate(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    val idPropertyModel = classModel.idProperty ?: return
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default void update(${classModel.dtiName} data) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        update(data, jakarta.persistence.LockModeType.OPTIMISTIC);""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default void update(${classModel.dtiName} data, jakarta.persistence.LockModeType lockMode) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(lockMode);""")
    bufferedWriter.appendLine("""        if (!tryUpdate(data, lockMode)) {""")
    bufferedWriter.appendLine("""            throw new IllegalArgumentException("Entity Not Found");""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default boolean tryUpdate(${classModel.dtiName} data) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        return tryUpdate(data, jakarta.persistence.LockModeType.OPTIMISTIC);""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default boolean tryUpdate(${classModel.dtiName} data, jakarta.persistence.LockModeType lockMode) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(lockMode);""")
    bufferedWriter.appendLine("""        ${classModel.qualifiedName} entity = this.getEntityManager().find(${classModel.qualifiedName}.class, data.${idPropertyModel.getterName}(), lockMode);""")
    bufferedWriter.appendLine("""        if (entity == null) {""")
    bufferedWriter.appendLine("""            return false;""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        ${classModel.dtiName}.copyUpdateProperties(this.getEntityManager(), entity, data);""")
    bufferedWriter.appendLine("""        this.getEntityManager().flush();""")
    bufferedWriter.appendLine("""        ${classModel.dtiName}.copyAllProperties(data, entity);""")
    bufferedWriter.appendLine("""        return true;""")
    bufferedWriter.appendLine("""    }""")
}

private fun writeDelete(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    val idPropertyModel = classModel.idProperty ?: return
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default void delete(${classModel.dtiName} data) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        delete(data, jakarta.persistence.LockModeType.OPTIMISTIC);""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default void delete(${classModel.dtiName} data, jakarta.persistence.LockModeType lockMode) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(lockMode);""")
    bufferedWriter.appendLine("""        if (!tryDelete(data, lockMode)) {""")
    bufferedWriter.appendLine("""            throw new IllegalArgumentException("Entity Not Found");""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default boolean tryDelete(${classModel.dtiName} data) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        return tryDelete(data, jakarta.persistence.LockModeType.OPTIMISTIC);""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    default boolean tryDelete(${classModel.dtiName} data, jakarta.persistence.LockModeType lockMode) {""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(data);""")
    bufferedWriter.appendLine("""        java.util.Objects.requireNonNull(lockMode);""")
    bufferedWriter.appendLine("""        ${classModel.qualifiedName} entity = this.getEntityManager().find(${classModel.qualifiedName}.class, data.${idPropertyModel.getterName}(), lockMode);""")
    bufferedWriter.appendLine("""        if (entity == null) {""")
    bufferedWriter.appendLine("""            return false;""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""        this.getEntityManager().remove(entity);""")
    bufferedWriter.appendLine("""        this.getEntityManager().flush();""")
    bufferedWriter.appendLine("""        return true;""")
    bufferedWriter.appendLine("""    }""")
}

private fun writeCountContext(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    class CountContext extends mx.com.inftel.codegen.CountContext<${classModel.qualifiedName}> {""")
    bufferedWriter.appendLine("""""")
    bufferedWriter.appendLine("""        CountContext(jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder, jakarta.persistence.criteria.Root<${classModel.qualifiedName}> root) {""")
    bufferedWriter.appendLine("""            super(root, criteriaBuilder);""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    @FunctionalInterface""")
    bufferedWriter.appendLine("""    interface CountContextConsumer extends java.util.function.Consumer<CountContext> {}""")
}

private fun writeListContext(bufferedWriter: BufferedWriter, classModel: ClassModel) {
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    class ListContext extends mx.com.inftel.codegen.ListContext<${classModel.qualifiedName}> {""")
    bufferedWriter.appendLine("""""")
    bufferedWriter.appendLine("""        ListContext(jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder, jakarta.persistence.criteria.Root<${classModel.qualifiedName}> root) {""")
    bufferedWriter.appendLine("""            super(root, criteriaBuilder);""")
    bufferedWriter.appendLine("""        }""")
    bufferedWriter.appendLine("""    }""")
    bufferedWriter.appendLine()
    bufferedWriter.appendLine("""    @FunctionalInterface""")
    bufferedWriter.appendLine("""    interface ListContextConsumer extends java.util.function.Consumer<ListContext> {}""")
}