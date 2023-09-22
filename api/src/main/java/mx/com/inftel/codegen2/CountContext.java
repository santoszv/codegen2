package mx.com.inftel.codegen2;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CountContext<X> extends PathCache<X> {

    protected final CriteriaBuilder criteriaBuilder;
    protected final List<Predicate> predicates;
    protected boolean distinct;

    public CountContext(Root<X> root, CriteriaBuilder criteriaBuilder) {
        super(root);
        this.criteriaBuilder = Objects.requireNonNull(criteriaBuilder);
        this.predicates = new ArrayList<>();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return Objects.requireNonNull(criteriaBuilder);
    }

    public List<Predicate> getPredicates() {
        return Objects.requireNonNull(predicates);
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
}
