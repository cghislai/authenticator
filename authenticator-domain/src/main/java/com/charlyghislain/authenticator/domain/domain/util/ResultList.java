package com.charlyghislain.authenticator.domain.domain.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResultList<T extends Object> {

    private List<T> results;
    private Long totalCount;

    public ResultList(@NotNull List<T> results, @NotNull Long totalCount) {
        this.results = results;
        this.totalCount = totalCount;
    }

    public ResultList(@NotNull List<T> results, @NotNull int totalCount) {
        this.results = results;
        this.totalCount = (long) totalCount;
    }

    public <U> ResultList(@NotNull List<U> results, @NotNull Long totalCount, Function<U, T> mapper) {
        this.results = results.stream().map(mapper).collect(Collectors.toList());
        this.totalCount = totalCount;
    }

    @NonNull
    public <U> ResultList<U> map(Function<T, U> mapper) {
        List<U> mappedResults = this.results.stream().map(mapper)
                .collect(Collectors.toList());
        return new ResultList<>(mappedResults, totalCount);
    }

    public static <T> ResultList<T> empty() {
        return new ResultList<>(new ArrayList<>(), 0L);
    }

    @NotNull
    public Stream<T> stream() {
        return results.stream();
    }

    public void forEach(Consumer<T> consumer) {
        results.forEach(consumer);
    }

    @NonNull
    public Optional<T> getAnyResult() {
        return results.stream().findAny();
    }

    @NotNull
    public List<T> getResults() {
        return results;
    }

    @NotNull
    public Long getTotalCount() {
        return totalCount;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

}
