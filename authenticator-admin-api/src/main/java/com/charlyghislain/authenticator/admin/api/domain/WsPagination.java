package com.charlyghislain.authenticator.admin.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.ws.rs.QueryParam;
import java.util.List;


public class WsPagination {

    @QueryParam("offset")
    private int offset;
    @QueryParam("length")
    private int length;
    @Nullable
    @NullableField
    @QueryParam("sorts")
    private List<WsSort> wsSorts;

    public WsPagination() {
    }

    public WsPagination(int length) {
        this.offset = 0;
        this.length = length;
    }

    public WsPagination(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public WsPagination(int offset, int length, @Nullable List<WsSort> wsSorts) {
        this.offset = offset;
        this.length = length;
        this.wsSorts = wsSorts;
    }

    public int getOffset() {
        return offset;
    }


    public int getLength() {
        return length;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setWsSorts(List<WsSort> wsSorts) {
        this.wsSorts = wsSorts;
    }

    @Nullable
    public List<WsSort> getWsSorts() {
        return wsSorts;
    }

}
