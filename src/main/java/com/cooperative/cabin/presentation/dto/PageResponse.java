package com.cooperative.cabin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Respuesta paginada genérica")
public class PageResponse<T> {
    @Schema(description = "Elementos de la página")
    private List<T> items;
    @Schema(example = "0")
    private int page;
    @Schema(example = "20")
    private int size;
    @Schema(example = "100")
    private long total;

    public PageResponse() {
    }

    public PageResponse(List<T> items, int page, int size, long total) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }
}


