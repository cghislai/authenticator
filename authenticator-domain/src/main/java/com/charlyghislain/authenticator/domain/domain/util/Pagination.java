package com.charlyghislain.authenticator.domain.domain.util;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Pagination<T extends WithId> {

    private int offset;
    private int length;
    @NotNull
    private List<Sort<T>> sorts = new ArrayList<>();


    public Pagination(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public Pagination(int length) {
        this.length = length;
    }

    public Pagination() {
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<Sort<T>> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort<T>> sorts) {
        this.sorts = sorts;
    }
}
